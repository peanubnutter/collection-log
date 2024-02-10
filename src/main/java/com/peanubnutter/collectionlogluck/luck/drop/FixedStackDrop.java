package com.peanubnutter.collectionlogluck.luck.drop;

import com.peanubnutter.collectionlogluck.CollectionLogLuckConfig;
import com.peanubnutter.collectionlogluck.model.CollectionLog;
import com.peanubnutter.collectionlogluck.model.CollectionLogItem;
import com.peanubnutter.collectionlogluck.luck.RollInfo;

// When a fixed-size stack has a chance to drop (e.g. 1/64 chance for 3 Key master teleport scrolls), this is actually
// a binomial distribution where the number of successes is the number of items received divided by the stack size
public class FixedStackDrop extends BinomialDrop {

    private final int stackSize;

    public FixedStackDrop(RollInfo rollInfo, int stackSize) {
        super(rollInfo);
        this.stackSize = stackSize;
    }

    @Override
    protected int getNumSuccesses(CollectionLogItem item, CollectionLog collectionLog, CollectionLogLuckConfig config) {
        return item.getQuantity() / stackSize;
    }
}
