package com.peanubnutter.collectionlogluck;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.google.inject.Provides;
import com.peanubnutter.collectionlogluck.luck.CollectionLogItemAliases;
import com.peanubnutter.collectionlogluck.luck.LogItemInfo;
import com.peanubnutter.collectionlogluck.luck.drop.AbstractDrop;
import com.peanubnutter.collectionlogluck.model.CollectionLog;
import com.peanubnutter.collectionlogluck.model.CollectionLogItem;
import com.peanubnutter.collectionlogluck.model.CollectionLogPage;
import com.peanubnutter.collectionlogluck.util.CollectionLogDeserializer;
import com.peanubnutter.collectionlogluck.util.CollectionLogLuckApiClient;
import com.peanubnutter.collectionlogluck.util.JsonUtils;
import com.peanubnutter.collectionlogluck.util.LuckUtils;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatCommandManager;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.Text;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@PluginDescriptor(
        name = "Collection Log Luck",
        description = "Calculates and displays luck for collection log items.",
        tags = {"collection", "log", "luck"}
)
public class CollectionLogLuckPlugin extends Plugin {

    private static final Pattern COLLECTION_LOG_LUCK_CHECK_REGEX = Pattern.compile("^You have received (.*) x (.*)\\.$");
    private static final String COLLECTION_LOG_LUCK_COMMAND_STRING = "!luck";
    private static final Pattern COLLECTION_LOG_COMMAND_PATTERN = Pattern.compile("!luck\\s*(.+)\\s*", Pattern.CASE_INSENSITIVE);

    private Map<Integer, Integer> loadedCollectionLogIcons;

    // caches collection log per username. Cleared on logout (including hopping worlds).
    private Map<String, CollectionLog> loadedCollectionLogs;

    @Getter
    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private ChatCommandManager chatCommandManager;

    @Inject
    private CollectionLogLuckConfig config;

    @Inject
    private ItemManager itemManager;

    @Inject
    private CollectionLogLuckApiClient apiClient;

    @Inject
    private JsonUtils jsonUtils;

    @Provides
    CollectionLogLuckConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(CollectionLogLuckConfig.class);
    }

    @Override
    protected void startUp() {
        loadedCollectionLogIcons = new HashMap<>();
        loadedCollectionLogs = new HashMap<>();
        chatCommandManager.registerCommandAsync(COLLECTION_LOG_LUCK_COMMAND_STRING, this::processLuckCommandMessage);
    }

    @Override
    protected void shutDown() {
        loadedCollectionLogIcons.clear();
        loadedCollectionLogs.clear();
        chatCommandManager.unregisterCommand(COLLECTION_LOG_LUCK_COMMAND_STRING);
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        if (!isValidWorldType()) {
            loadedCollectionLogs.clear();
            return;
        }

        if (gameStateChanged.getGameState() == GameState.LOGIN_SCREEN ||
                gameStateChanged.getGameState() == GameState.HOPPING) {
            loadedCollectionLogs.clear();
        }
    }

    private boolean isValidWorldType() {
        List<WorldType> invalidTypes = ImmutableList.of(
                WorldType.DEADMAN,
                WorldType.NOSAVE_MODE,
                WorldType.SEASONAL,
                WorldType.TOURNAMENT_WORLD
        );

        for (WorldType worldType : invalidTypes) {
            if (client.getWorldType().contains(worldType)) {
                return false;
            }
        }

        return true;
    }

    @Subscribe
    public void onChatMessage(ChatMessage chatMessage) {
        if (chatMessage.getType() != ChatMessageType.GAMEMESSAGE) {
            return;
        }

        Matcher checkLuckMatcher = COLLECTION_LOG_LUCK_CHECK_REGEX.matcher(chatMessage.getMessage());
        if (checkLuckMatcher.matches()) {
            processCheckItemMessage(checkLuckMatcher);
        }
    }

    /**
     * Display luck-related information when the player "check"s an item in the collection log.
     *
     * @param checkLuckMatcher the matcher containing command info
     */
    private void processCheckItemMessage(Matcher checkLuckMatcher) {
        // Note: this assumes this function is called for the local player
        if (config.hidePersonalLuckCalculation()) {
            return;
        }
        if (checkLuckMatcher.groupCount() < 2) {
            // Matcher didn't find 2 groups for some reason
            return;
        }

        // For now, assume that the "check item" message is for the local player. Some day, this could support the
        // "check item" functionality through another player's house Adventure Log
        String username = client.getLocalPlayer().getName();

        fetchCollectionLogAndRunAsync(username, collectionLog -> {
            String message = buildLuckCommandMessage(collectionLog, checkLuckMatcher.group(2));

            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", message, null);
        });
    }

    private String getChatMessageSenderUsername(ChatMessage chatMessage) {
        if (chatMessage.getType().equals(ChatMessageType.PRIVATECHATOUT)) {
            String username = client.getLocalPlayer().getName();
            if (username != null) {
                return Text.sanitize(username);
            }
            return "";
        }
        return Text.sanitize(chatMessage.getName());
    }

    /**
     * After a "!luck" chat message, fetches collection log for the chatting user and then replaces the message
     *
     * @param chatMessage The ChatMessage event
     * @param message     Text of the message
     */
    private void processLuckCommandMessage(ChatMessage chatMessage, String message) {
        String username = getChatMessageSenderUsername(chatMessage);

        fetchCollectionLogAndRunAsync(username, collectionLog -> replaceCommandMessage(chatMessage, message, collectionLog));
    }

    private void fetchCollectionLogAndRunAsync(String rawUsername, Consumer<CollectionLog> callback) {
        final String sanitizedUsername = Text.sanitize(rawUsername);

        // Only fetch collection log if necessary
        if (loadedCollectionLogs.containsKey(sanitizedUsername)) {
            CollectionLog collectionLog = loadedCollectionLogs.get(sanitizedUsername);
            clientThread.invoke(() -> callback.accept(collectionLog));
            return;
        }

        try {
            apiClient.getCollectionLog(sanitizedUsername, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    log.error("Unable to resolve !log command: " + e.getMessage());
                    clientThread.invoke(() -> callback.accept(null));
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    JsonObject collectionLogJson = apiClient.processResponse(response);
                    response.close();

                    if (collectionLogJson == null) {
                        clientThread.invoke(() -> callback.accept(null));
                        return;
                    }

                    CollectionLog collectionLog = jsonUtils.fromJsonObject(
                            collectionLogJson.getAsJsonObject("collectionLog"),
                            CollectionLog.class,
                            new CollectionLogDeserializer()
                    );
                    loadedCollectionLogs.put(sanitizedUsername, collectionLog);
                    clientThread.invoke(() -> callback.accept(collectionLog));
                }
            });
        } catch (IOException e) {
            log.error("Unable to resolve !luck command: " + e.getMessage());
            clientThread.invoke(() -> callback.accept(null));
        }
    }

    private void replaceCommandMessage(ChatMessage chatMessage, String message, CollectionLog collectionLog) {
        Matcher commandMatcher = COLLECTION_LOG_COMMAND_PATTERN.matcher(message);
        if (!commandMatcher.matches()) {
            return;
        }

        String replacementMessage;
        if (collectionLog == null) {
            String username = getChatMessageSenderUsername(chatMessage);
            replacementMessage = "No Collection Log data found for user: " + username;
        } else {
            String commandTarget = commandMatcher.group(1);
            replacementMessage = buildLuckCommandMessage(collectionLog, commandTarget);
        }

        chatMessage.getMessageNode().setValue(replacementMessage);
        client.runScript(ScriptID.BUILD_CHATBOX);
    }

    /**
     * Loads a list of Collection Log items into the client's mod icons.
     *
     * @param collectionLogItems List of items to load
     */
    private void loadItemIcons(List<CollectionLogItem> collectionLogItems) {
        List<CollectionLogItem> itemsToLoad = collectionLogItems
                .stream()
                .filter(item -> !loadedCollectionLogIcons.containsKey(item.getId()))
                .collect(Collectors.toList());

        final IndexedSprite[] modIcons = client.getModIcons();

        final IndexedSprite[] newModIcons = Arrays.copyOf(modIcons, modIcons.length + itemsToLoad.size());
        int modIconIdx = modIcons.length;

        for (int i = 0; i < itemsToLoad.size(); i++) {
            final CollectionLogItem item = itemsToLoad.get(i);
            final ItemComposition itemComposition = itemManager.getItemComposition(item.getId());
            final BufferedImage image = ImageUtil.resizeImage(itemManager.getImage(itemComposition.getId()), 18, 16);
            final IndexedSprite sprite = ImageUtil.getImageIndexedSprite(image, client);
            final int spriteIndex = modIconIdx + i;

            newModIcons[spriteIndex] = sprite;
            loadedCollectionLogIcons.put(item.getId(), spriteIndex);
        }

        client.setModIcons(newModIcons);
    }

    /**
     * Convert to actual item name rather than "display" name (e.g. remove " (Members)" suffixes)
     * It may be possible to simply remove the suffix directly, but I haven't checked that it works for every item.
     * For example, there may be items whose display name differs from its "real" name in a way that isn't simply
     * adding " (Members)"
     *
     * @param itemDisplayName An item's display name which
     * @return The item's true name regardless of membership status
     */
    private String itemDisplayNameToItemName(String itemDisplayName) {
        for (int i = 0; i < client.getItemCount(); i++) {
            ItemComposition itemComposition = client.getItemDefinition(i);
            if (itemComposition.getName().equalsIgnoreCase(itemDisplayName)) {
                return itemComposition.getMembersName();
            }
        }
        return itemDisplayName;
    }

    /**
     * Builds the replacement messages for the !luck... command
     *
     * @param collectionLog The collection log to use for the luck calculation (which may be another player's)
     * @param commandTarget The item or page for which to calculate luck. If omitted, calculates account-level luck
     * @return Replacement message
     */
    private String buildLuckCommandMessage(CollectionLog collectionLog, String commandTarget) {
        boolean collectionLogIsLocalPlayer =
                client.getLocalPlayer().getName().equalsIgnoreCase(collectionLog.getUsername());

        if (collectionLogIsLocalPlayer && config.hidePersonalLuckCalculation()) {
            // This should make it obvious that 1) The player can go to the config to change this setting, and 2) other
            // players can still see their luck if they type in a !log luck command.
            return "Your luck is set to be hidden from you in the plugin config.";
        }
        // !luck [account|total|overall]
        if (commandTarget == null
                || commandTarget.equalsIgnoreCase("account")
                || commandTarget.equalsIgnoreCase("total")
                || commandTarget.equalsIgnoreCase("overall")) {
            return "Account-level luck calculation is not yet supported.";
        }

        // !luck <page-name>
        String pageName = CollectionLogPage.aliasPageName(commandTarget);
        if (collectionLog.searchForPage(pageName) != null) {
            return "Per-activity or per-page luck calculation is not yet supported.";
        }

        // !luck <item-name>
        String itemName = itemDisplayNameToItemName(commandTarget);
        itemName = CollectionLogItemAliases.aliasItemName(itemName);

        CollectionLogItem item = collectionLog.searchForItem(itemName);
        if (item == null) {
            return "Item " + itemName + " is not recognized.";
        }
        LogItemInfo logItemInfo = LogItemInfo.findByName(itemName);
        if (logItemInfo == null) {
            // This likely only happens if there is an update and the plugin does not yet support new items.
            return "Item " + itemName + " is not yet supported for luck calculation.";
        }

        String warningText = "";

        CollectionLogLuckConfig relevantConfig = config;
        if (!collectionLogIsLocalPlayer) {
            relevantConfig = null;
        }

        // all other unimplemented or unsupported drops take this path
        String failReason = logItemInfo.getDropProbabilityDistribution().getIncalculableReason(item, relevantConfig);
        if (failReason != null) {
            if (failReason.equals(AbstractDrop.INCALCULABLE_MISSING_CONFIG)) {
                // drops from other players will use YOUR config, which can lead to very inaccurate luck calculation!!
                // proceed with calculation but warn about likely inaccuracy
                warningText = " - Warning: Calculation uses YOUR config settings. May be inaccurate.";
            } else {
                return failReason;
            }
        }

        // make sure this item's icon is loaded
        loadItemIcons(ImmutableList.of(item));

        // calculate using player's config, even if the calculation is for another player
        double luck = logItemInfo.getDropProbabilityDistribution().calculateLuck(item, collectionLog, config);
        double dryness = logItemInfo.getDropProbabilityDistribution().calculateDryness(item, collectionLog, config);
        if (luck < 0 || luck > 1 || dryness < 0 || dryness > 1) {
            return "Unknown error calculating luck for item.";
        }
        double overallLuck = LuckUtils.getOverallLuck(luck, dryness);
        Color luckColor = LuckUtils.getOverallLuckColor(overallLuck);

        String luckString = LuckUtils.formatLuckSigDigits(luck);
        String drynessString = LuckUtils.formatLuckSigDigits(dryness);
        String overallLuckString = LuckUtils.formatLuckSigDigits(overallLuck);

        String shownLuckText = overallLuckString + "% luck";
        if (config.showDetailedLuck()) {
            shownLuckText = luckString + "% lucky / " + drynessString + "% dry";
        }

        int numObtained = item.getQuantity();
        String kcDescription = logItemInfo.getDropProbabilityDistribution().getKillCountDescription(collectionLog);

        // rarer than 1 in 100M is likely an error. Note: 0 luck or 0 dryness is normal as a result of low KC and does
        // not need a warning.
        if (luck > 0.99999999 || dryness > 0.99999999) {
            // previous warnings supersede this warning
            if (warningText.isEmpty()) {
                warningText = " - Warning: Check plugin configuration. Did you have many KC" +
                        " before the log existed, or have you reached the max # tracked for this item?";
            }
        }

        // This reports detailed luck stats (luck + dryness) rather than a simplified average. We should do a user study
        // to see if reporting both luck/dryness is too confusing. To me, there is a difference between receiving 0
        // drops in 1 kc and being still at ~50% versus receiving 5 drops in 500 kc and being exactly on drop rate.
        // This difference is lost if reporting averaged luck + dryness as a single number.
        // This could be a toggleable option...
        return new ChatMessageBuilder()
                .append(item.getName() + " ")
                .img(loadedCollectionLogIcons.get(item.getId()))
                .append("x" + numObtained + ": ")
                .append(luckColor, shownLuckText)
                .append(" in ")
                .append(kcDescription)
                .append(warningText)
                .build();
    }

}
