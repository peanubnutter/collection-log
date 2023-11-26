package com.peanubnutter.collectionlogluck;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("collectionlogluck")
public interface CollectionLogLuckConfig extends Config
{

	String NUM_INVALID_BARROWS_KC_KEY = "num_invalid_barrows_kc";
	String BARROWS_BOLT_RACKS_ENABLED_KEY = "barrows_bolt_racks_enabled";
	String AVG_PERSONAL_COX_POINTS_KEY = "avg_personal_cox_points";
	String AVG_PERSONAL_COX_CM_POINTS_KEY = "avg_personal_cox_cm_points";
	String AVG_PERSONAL_TOB_POINTS_KEY = "avg_personal_tob_points";
	String AVG_PERSONAL_TOB_HM_POINTS_KEY = "avg_personal_tob_hm_points";
	String ENTRY_TOA_UNIQUE_CHANCE_KEY = "entry_toa_unique_chance";
	String REGULAR_TOA_UNIQUE_CHANCE_KEY = "regular_toa_unique_chance";
	String EXPERT_TOA_UNIQUE_CHANCE_KEY = "expert_toa_unique_chance";
	String AVG_NIGHTMARE_TEAM_SIZE_KEY = "avg_nightmare_team_size";
	String AVG_NIGHTMARE_REWARDS_FRACTION_KEY = "avg_nightmare_rewards_fraction";
	String AVG_NEX_REWARDS_FRACTION_KEY = "avg_nex_rewards_fraction";
	String NUM_ROLLS_PER_WINTERTODT_CRATE_KEY = "num_rolls_per_wintertodt_crate";
	String AVG_ZALCANO_REWARDS_FRACTION_KEY = "avg_zalcano_rewards_fraction";
	String AVG_ZALCANO_POINTS_KEY = "avg_zalcano_points";
	String NUM_FIRE_CAPES_SACRIFICED_KEY = "num_fire_capes_sacrificed";
	String NUM_INFERNAL_CAPES_SACRIFICED_KEY = "num_infernal_capes_sacrificed";
	String AVG_CALLISTO_REWARDS_FRACTION_KEY = "avg_callisto_rewards_fraction";
	String AVG_VENENATIS_REWARDS_FRACTION_KEY = "avg_venenatis_rewards_fraction";
	String AVG_VETION_REWARDS_FRACTION_KEY = "avg_vetion_rewards_fraction";
	String NUM_ABYSSAL_LANTERNS_PURCHASED_KEY = "num_abyssal_lanterns_purchased";
	String NUM_CRYSTAL_WEAPON_SEEDS_PURCHASED_KEY = "num_crystal_weapon_seeds_purchased";
	String SKOTIZO_KC_PRE_BUFF_KEY = "skotizo_kc_pre_buff";
	String KQ_KC_PRE_D_PICK_BUFF_KEY = "kq_kc_pre_d_pick_buff";
	String KBD_KC_PRE_D_PICK_BUFF_KEY = "kbd_kc_pre_d_pick_buff";

	// Used in GET request to collectionlog.net. Should be up-to-date with collection log plugin updates.
	String COLLECTION_LOG_VERSION = "3.1.0";

	@ConfigSection(
		name = "Appearance",
		description = "Luck display settings across the plugin",
		position = 1
	)
	String appearanceSection = "appearance";

	// Other players' luck will always show, for example though the !log command, but the player may want to hide
	// their own luck because it could be unpleasant to see.
	@ConfigItem(
			keyName = "hide_personal_luck_calculation",
			name = "Hide personal luck",
			description = "Disable the display of your own luck calculations",
			position = 1,
			section = appearanceSection
	)
	default boolean hidePersonalLuckCalculation()
	{
		return false;
	}

	@ConfigItem(
			keyName = "show_detailed_luck",
			name = "Show detailed luck",
			description = "Instead of overall luck meter, show luck (% players unluckier than you) and dryness (% players luckier than you) separately.",
			position = 2,
			section = appearanceSection
	)
	default boolean showDetailedLuck()
	{
		return false;
	}


	// ############### Luck section ###############

	@ConfigSection(
			name = "Luck calculation",
			description = "Config options for calculation collection log luck",
			position = 2
	)
	String luckSection = "Luck calculation";

	// ############### Raids, in order, are at the top since it's likely most interesting to people. ###############

	@ConfigItem(
			keyName = AVG_PERSONAL_COX_POINTS_KEY,
			name = "CoX points per raid",
			description = "The average # of points you personally receive per Chambers of Xeric raid.",
			position = 10,
			section = luckSection
	)
	default int avgPersonalCoxPoints()
	{
		return 30_000;
	}

	@ConfigItem(
			keyName = AVG_PERSONAL_COX_CM_POINTS_KEY,
			name = "CoX CM points per raid",
			description = "The average # of points you personally receive per Chambers of Xeric Challenge Mode raid.",
			position = 11,
			section = luckSection
	)
	default int avgPersonalCoxCmPoints()
	{
		return 45_000;
	}

	@ConfigItem(
			keyName = AVG_PERSONAL_TOB_POINTS_KEY,
			name = "ToB point fraction",
			description = "The average fraction of max team points you receive per Theatre of Blood raid, including MVP points.",
			position = 12,
			section = luckSection
	)
	default double avgPersonalTobPointFraction()
	{
		return 0.25;
	}

	@ConfigItem(
			keyName = AVG_PERSONAL_TOB_HM_POINTS_KEY,
			name = "ToB HM point fraction",
			description = "The average fraction of max team points you receive per Theatre of Blood Hard Mode raid, including MVP points.",
			position = 13,
			section = luckSection
	)
	default double avgPersonalTobHmPointFraction()
	{
		return 0.2;
	}

	// Note: This assumes that there is no reason to ever do a raid less than 50 invocation level.
	@ConfigItem(
			keyName = ENTRY_TOA_UNIQUE_CHANCE_KEY,
			name = "Entry ToA Unique Chance",
			description = "Use a plugin/calc to estimate your chance of a unique for your typical raid setup. Defaults to 50 invocation level.",
			position = 14,
			section = luckSection
	)
	default double entryToaUniqueChance()
	{
		return 0.0076;
	}

	@ConfigItem(
			keyName = REGULAR_TOA_UNIQUE_CHANCE_KEY,
			name = "Regular ToA Unique Chance",
			description = "Use a plugin/calc to estimate your chance of a unique for your typical raid setup. Defaults to 150 invocation level.",
			position = 15,
			section = luckSection
	)
	default double regularToaUniqueChance()
	{
		return 0.0202;
	}

	@ConfigItem(
			keyName = EXPERT_TOA_UNIQUE_CHANCE_KEY,
			name = "Expert ToA Unique Chance",
			description = "Use a plugin/calc to estimate your chance of a unique for your typical raid setup. Defaults to 300 invocation level.",
			position = 16,
			section = luckSection
	)
	default double expertToaUniqueChance()
	{
		return 0.0440;
	}

	// ############### Team based bosses with contribution (% damage dealt and/or MVP mechanic) ###############

	@ConfigItem(
			keyName = AVG_NIGHTMARE_TEAM_SIZE_KEY,
			name = "Nightmare team size",
			description = "Average team size when killing The Nightmare of Ashihama. Decimals can be used.",
			position = 20,
			section = luckSection
	)
	default double avgNightmareTeamSize() {
		return 5;
	}

	@ConfigItem(
			keyName = AVG_NIGHTMARE_REWARDS_FRACTION_KEY,
			name = "Nightmare rewards fraction",
			description = "Avg. fraction of contribution to killing The Nightmare of Ashihama." +
					" This should include MVP bonuses, so multiply by 1.05 if always MVP, or less accordingly.",
			position = 21,
			section = luckSection
	)
	default double avgNightmareRewardsFraction() {
		// average MVP rate of 20% with an average contribution on a 5-man team
		return 0.202;
	}

	@ConfigItem(
			keyName = AVG_NEX_REWARDS_FRACTION_KEY,
			name = "Nex rewards fraction",
			description = "Avg. fraction of contribution to killing Nex." +
					" This should include MVP bonuses, so multiply by 1.1 if always MVP, or less accordingly.",
			position = 22,
			section = luckSection
	)
	default double avgNexRewardsFraction() {
		// average MVP rate of 20% with an average contribution on a 5-man team
		return 0.204;
	}

	@ConfigItem(
			keyName = AVG_CALLISTO_REWARDS_FRACTION_KEY,
			name = "Callisto rewards fraction",
			description = "Avg. fraction of contribution to killing Callisto." +
					" Set to 0.1 if team size >= 10, or 1 if soloing.",
			position = 23,
			section = luckSection
	)
	default double avgCallistoRewardsFraction() {
		return 0.2;
	}

	@ConfigItem(
			keyName = AVG_VENENATIS_REWARDS_FRACTION_KEY,
			name = "Venenatis rewards fraction",
			description = "Avg. fraction of contribution to killing Venenatis." +
					" Set to 0.1 if team size >= 10, or 1 if soloing.",
			position = 24,
			section = luckSection
	)
	default double avgVenenatisRewardsFraction() {
		return 0.5;
	}

	@ConfigItem(
			keyName = AVG_VETION_REWARDS_FRACTION_KEY,
			name = "Vet'ion rewards fraction",
			description = "Avg. fraction of contribution to killing Vet'ion." +
					" Set to 0.1 if team size >= 10, or 1 if soloing.",
			position = 25,
			section = luckSection
	)
	default double avgVetionRewardsFraction() {
		return 0.5;
	}

	@ConfigItem(
			keyName = AVG_ZALCANO_REWARDS_FRACTION_KEY,
			name = "Zalcano rewards fraction",
			description = "Avg. fraction of contribution to killing Zalcano, taking into account team size.",
			position = 26,
			section = luckSection
	)
	default double avgZalcanoRewardsFraction() {
		// 4 man is most efficient
		return 0.25;
	}

	@ConfigItem(
			keyName = AVG_ZALCANO_POINTS_KEY,
			name = "Zalcano points",
			description = "Your average number of points per Zalcano kill. See wiki for more info.",
			position = 27,
			section = luckSection
	)
	default int avgZalcanoPoints() {
		// According to Zalcano community, 210 to 350 points is normal in efficient 4-man
		return 300;
	}

	// ############### Misc minigames and minor bosses. ###############

	@ConfigItem(
			keyName = NUM_ROLLS_PER_WINTERTODT_CRATE_KEY,
			name = "# Wintertodt Rolls",
			description = "The number of rolls per Wintertodt supply crate. 500 pts = 2 rolls. 1k pts = 3 rolls, and so on",
			position = 30,
			section = luckSection
	)
	default double numRollsPerWintertodtCrate()
	{
		return 2.5;
	}

	// Completing Barrows without killing all 6 brothers, for example if rapidly resetting to finish Barrows combat
	// achievements, drastically reduces the chance of receiving unique loot. The player can configure an approximate
	// number of Barrows KC they have wasted, including summing fractional less-than-6-brother-kills, to make the luck
	// calculation more accurate. This is completely optional, and being exact is not really necessary.
	@ConfigItem(
			keyName = NUM_INVALID_BARROWS_KC_KEY,
			name = "# Barrows KC wasted",
			description = "The effective number of Barrows KC wasted by killing < 6 brothers. 4-5 brothers killed ~= 0.5 KC wasted.",
			position = 31,
			section = luckSection
	)
	default int numInvalidBarrowsKc()
	{
		return 0;
	}

	@ConfigItem(
			keyName = BARROWS_BOLT_RACKS_ENABLED_KEY,
			name = "Bolt racks enabled",
			description = "Whether or not you try to get enough points at Barrows to receive bolt racks.",
			position = 32,
			section = luckSection
	)
	default boolean barrowsBoltRacksEnabled()
	{
		return false;
	}

	// ############### Manually purchased or sacrificed items. Requires regular updates by the user. ###############

	@ConfigItem(
			keyName = NUM_FIRE_CAPES_SACRIFICED_KEY,
			name = "# Fire capes sacrificed",
			description = "The number of fire capes sacrificed for a chance at TzRek-Jad.",
			position = 40,
			section = luckSection
	)
	default int numFireCapesSacrificed() {
		return 0;
	}

	@ConfigItem(
			keyName = NUM_INFERNAL_CAPES_SACRIFICED_KEY,
			name = "# Infernal capes sacrificed",
			description = "The number of infernal capes sacrificed for a chance at Jal-nib-rek.",
			position = 41,
			section = luckSection
	)
	default int numInfernalCapesSacrificed() {
		return 0;
	}

	// Purchasing Abyssal Lanterns prevents calculating how many the player has received through the Rewards Guardian.
	// The calculation can be corrected if the player inputs the number purchased from the shop.
	@ConfigItem(
			keyName = NUM_ABYSSAL_LANTERNS_PURCHASED_KEY,
			name = "# Abyssal Lanterns bought",
			description = "The number of Abyssal Lanterns you bought from the Guardians of the Rift shop.",
			position = 42,
			section = luckSection
	)
	default int numAbyssalLanternsPurchased()
	{
		return 0;
	}

	// Purchasing crystal weapon seeds prevents calculating how many the player has received through the Gauntlet.
	// The calculation can be corrected if the player inputs the number purchased from the shop.
	@ConfigItem(
			keyName = NUM_CRYSTAL_WEAPON_SEEDS_PURCHASED_KEY,
			name = "# Crystal weapon seeds bought",
			description = "The number of crystal weapon seeds you bought from the Last Man Standing shop.",
			position = 43,
			section = luckSection
	)
	default int numCrystalWeaponSeedsPurchased()
	{
		return 0;
	}

	// ############### Settings based on historical drop rate changes ###############

	@ConfigItem(
			keyName = SKOTIZO_KC_PRE_BUFF_KEY,
			name = "Skotizo KC pre-buff",
			description = "# of Skotizo kills before the Jar of darkness drop rate buff",
			position = 50,
			section = luckSection
	)
	default int skotizoKcPreBuff() {
		return 0;
	}

	@ConfigItem(
			keyName = KQ_KC_PRE_D_PICK_BUFF_KEY,
			name = "KQ KC pre- d pick buff",
			description = "# of Kalphite Queen kills before the Dragon Pickaxe was added to the drop table.",
			position = 51,
			section = luckSection
	)
	default int kqKcPreDPickBuff() {
		return 0;
	}

	@ConfigItem(
			keyName = KBD_KC_PRE_D_PICK_BUFF_KEY,
			name = "KBD KC pre- d pick buff",
			description = "# of King Black Dragon kills before the Dragon Pickaxe drop rate buff.",
			position = 52,
			section = luckSection
	)
	default int kbdKcPreDPickBuff() {
		return 0;
	}

}