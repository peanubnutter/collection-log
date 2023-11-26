package com.peanubnutter.collectionlogluck.luck.drop;

import com.peanubnutter.collectionlogluck.CollectionLogLuckConfig;
import com.peanubnutter.collectionlogluck.model.CollectionLogItem;

// Items whose KC is not tracked by the collection log cannot currently have their luck calculated. In the future,
// it may be possible to use the loot tracker plugin to implement some of these items.
public class MissingKillCountDrop extends AbstractUnsupportedDrop {

    @Override
    public String getIncalculableReason(CollectionLogItem item, CollectionLogLuckConfig config) {
        return "The collection log has no reliable KC for one or more drop sources for " + itemName;
    }
}
