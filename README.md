
# Collection Log Luck Plugin ![Plugin Installs](https://img.shields.io/endpoint?url=https://i.pluginhub.info/shields/installs/plugin/collection-log-luck) ![Plugin Rank](https://img.shields.io/endpoint?url=https://i.pluginhub.info/shields/rank/plugin/collection-log-luck)

A [Runelite](https://github.com/runelite/runelite) plugin that calculates luck for collection log items by pulling data from [collectionlog.net](https://collectionlog.net).

## Features
* Share your luck stats with other players or view other players' luck using a chat command.
* Click "Check" on supported items in the collection log to calculate your luck.
* Configurable assumptions for certain drops for more accurate calculations.
* Coming soon: Per-boss and account-wide luck calculation

## Commands
Type `!luck` followed by an item name. Alternatively, click "Check" on any item icon in the collection log pane to 
show your luck stats for that item.

<img src="https://i.imgur.com/5pPIw0C.png" width="500" />

The command will try to infer the intended item name if you abbreviate or misspell it.

<img src="https://i.imgur.com/AHNv1V0.gif" width="500" />
Note: The command syntax shown above is outdated.

Examples:
```
!luck tumeken's shadow
!luck enhanced weapon seed
!luck zulrah pet
```

In the future, luck for an entire page or your entire account can be shared rather than single items.

## Troubleshooting

The first time loading your own (or someone else's) collection log, there may be a slight delay. Reliability of this
plugin depends upon the reliability of collectionlog.net. This plugin will not re-download your or someone else's
collection log until you re-log.

If the plugin still displays out of date data after re-logging, consider re-uploading your collection log to
collectionlog.net using the separate Collection Log Plugin.

## Luck Configuration

Displaying your own luck can be disabled in the plugin configuration.

If you enable detailed luck stats in the plugin configuration, you will see luck and dryness separately. Luck is the
percent of players that you are luckier than. Dryness is the percent of player that you are drier than. By default,
these are combined into a single "overall" luck meter. An overall luck near 50% or luck/dryness near 0 might just mean
you have low KC and luck calculation is unreliable at that point.

The collection log does not have enough information to calculate luck for every item in the game, for example if KC is
not tracked for some minigames or monsters. In some cases, providing additional information can allow a 
fairly accurate calculation. Double check the luck calculation settings for best results:

<img src="https://i.imgur.com/E2z85Ub.png" width="180" />
