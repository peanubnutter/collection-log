package com.peanubnutter.collectionlogluck.luck.drop;

import com.peanubnutter.collectionlogluck.CollectionLogLuckConfig;
import com.peanubnutter.collectionlogluck.model.CollectionLogItem;

public class UnimplementedDrop extends AbstractUnsupportedDrop {

   @Override
    public String getIncalculableReason(CollectionLogItem item, CollectionLogLuckConfig config) {
        return itemName + " is not currently supported but may be in the future.";
    }
}
