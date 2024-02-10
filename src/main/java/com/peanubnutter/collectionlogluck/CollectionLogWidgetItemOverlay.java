package com.peanubnutter.collectionlogluck;

import com.peanubnutter.collectionlogluck.luck.LogItemInfo;
import com.peanubnutter.collectionlogluck.luck.LuckCalculationResult;
import com.peanubnutter.collectionlogluck.model.CollectionLogItem;
import net.runelite.api.widgets.InterfaceID;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.ui.overlay.WidgetItemOverlay;

import javax.inject.Inject;
import java.awt.*;

public class CollectionLogWidgetItemOverlay extends WidgetItemOverlay {

    @Inject
    private CollectionLogLuckPlugin collectionLogLuckPlugin;

    @Inject
    private CollectionLogLuckConfig config;

    // on a scale from 0 to 255
    private static final int LUCK_OVERLAY_ALPHA = 35;
    private static final int LUCK_OVERLAY_TEXT_ALPHA = 200;

    public CollectionLogWidgetItemOverlay() {
        super();

        // For now, only draw on collection log until adventure log is supported.
        drawAfterInterface(InterfaceID.COLLECTION_LOG);
    }

    @Override
    public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem widgetItem) {
        if (config.hidePersonalLuckCalculation()) {
            return;
        }

        // Note: This assumes this code is called for the Collection Log, not another player's Adventure Log
        String username = collectionLogLuckPlugin.getClient().getLocalPlayer().getName();

        collectionLogLuckPlugin.fetchCollectionLog(username, false, collectionLog -> {
            // Collection log request may still be in progress
            if (collectionLog == null) return;

            LogItemInfo logItemInfo = LogItemInfo.findByItemId(widgetItem.getId());
            if (logItemInfo == null) return;

            CollectionLogItem item = collectionLog.searchForItem(logItemInfo.getItemName());
            if (item == null) return;

            // Don't show any background color for unsupported drops
            String incalculableReason = logItemInfo.getDropProbabilityDistribution().getIncalculableReason(item, config);
            if (incalculableReason != null) return;

            LuckCalculationResult luckCalculationResult = collectionLogLuckPlugin.fetchLuckCalculationResult(
                logItemInfo.getDropProbabilityDistribution(),
                item,
                collectionLog,
                config);

            Rectangle r = widgetItem.getCanvasBounds();
            Color luckColor = luckCalculationResult.getLuckColor();

            Color renderColor = new Color(luckColor.getRed(), luckColor.getGreen(), luckColor.getBlue(), LUCK_OVERLAY_ALPHA);
            graphics.setColor(renderColor);

            graphics.fill3DRect(r.x, r.y, r.width, r.height, false);

            if (config.showCollectionLogOverlayText()) {
                String overallLuckText = Math.round(100 * luckCalculationResult.getOverallLuck()) + "%";

                // drop shadow
                graphics.setColor(Color.BLACK);
                graphics.drawString(overallLuckText, r.x + 0.5f, r.y + r.height + 0.5f);

                Color textColor = new Color(luckColor.getRed(), luckColor.getGreen(), luckColor.getBlue(), LUCK_OVERLAY_TEXT_ALPHA)
                        .brighter().brighter();
                graphics.setColor(textColor);

                graphics.drawString(overallLuckText, r.x, r.y + r.height);
            }

        });
    }
}
