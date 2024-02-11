package com.peanubnutter.collectionlogluck.luck.drop;

import com.peanubnutter.collectionlogluck.CollectionLogLuckConfig;
import com.peanubnutter.collectionlogluck.model.CollectionLogItem;

public class UnimplementedDrop extends AbstractUnsupportedDrop {

   @Override
    public String getIncalculableReason(CollectionLogItem item, CollectionLogLuckConfig config) {
        return "Collection Log Luck plugin does not currently support luck calculation for " + itemName;
    }
}
