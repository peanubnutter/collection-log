package com.peanubnutter.collectionlogluck.luck.drop;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.peanubnutter.collectionlogluck.luck.LogItemSourceInfo;
import com.peanubnutter.collectionlogluck.luck.RollInfo;
import com.peanubnutter.collectionlogluck.model.CollectionLog;
import com.peanubnutter.collectionlogluck.model.CollectionLogItem;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DupeProtectedFirstSetBinomialDropTest {

    @Test
    public void test_setAlmostComplete() {
        int kc = 100;
        // 3 pieces obtained
        double expectedLuck = 0.73497;
        double expectedDryness = 0.10467;
        // expected probabilities calculated online, with the following sig digits
        double tolerance = 0.00001;

        DupeProtectedFirstSetBinomialDrop drop = new DupeProtectedFirstSetBinomialDrop(
                new RollInfo(LogItemSourceInfo.LUNAR_CHESTS_OPENED, 1.0 / 224),
                ImmutableList.of(29022, 29025, 29028, 28997)
        );

        List<CollectionLogItem> items = new ArrayList<>();
        items.add(new CollectionLogItem(29022, "Blood moon chestplate", 0, false, 0));
        items.add(new CollectionLogItem(29025, "Blood moon tassets", 1, true, 0));
        items.add(new CollectionLogItem(29028, "Blood moon helm", 1, true, 0));
        items.add(new CollectionLogItem(28997, "Dual macuahuitl", 1, true, 0));

        CollectionLog mockCollectionLog = CollectionLogLuckTestUtils.getMockCollectionLogWithKcsAndItems(
                ImmutableMap.of(LogItemSourceInfo.LUNAR_CHESTS_OPENED.getName(), kc),
                items
        );

        // All items should have the same luck, since even for unobtained items, the progress towards the complete set
        // is what we're really measuring for luck. This might confuse some users, but it might be best / easiest.
        for (CollectionLogItem mockItem : items) {
            double actualLuck = drop.calculateLuck(mockItem, mockCollectionLog, null);
            assertEquals(expectedLuck, actualLuck, tolerance);

            double actualDryness = drop.calculateDryness(mockItem, mockCollectionLog, null);
            assertEquals(expectedDryness, actualDryness, tolerance);
        }
    }

    @Test
    public void test_setJustComplete() {
        int kc = 100;
        // 4 pieces obtained.
        double expectedLuck = 0.89533;
        double expectedDryness = 0.03397;
        // expected probabilities calculated online, with the following sig digits
        double tolerance = 0.00001;

        DupeProtectedFirstSetBinomialDrop drop = new DupeProtectedFirstSetBinomialDrop(
                new RollInfo(LogItemSourceInfo.LUNAR_CHESTS_OPENED, 1.0 / 224),
                ImmutableList.of(29022, 29025, 29028, 28997)
        );

        List<CollectionLogItem> items = new ArrayList<>();
        items.add(new CollectionLogItem(29022, "Blood moon chestplate", 1, true, 0));
        items.add(new CollectionLogItem(29025, "Blood moon tassets", 1, true, 0));
        items.add(new CollectionLogItem(29028, "Blood moon helm", 1, true, 0));
        items.add(new CollectionLogItem(28997, "Dual macuahuitl", 1, true, 0));

        CollectionLog mockCollectionLog = CollectionLogLuckTestUtils.getMockCollectionLogWithKcsAndItems(
                ImmutableMap.of(LogItemSourceInfo.LUNAR_CHESTS_OPENED.getName(), kc),
                items
        );

        // All items should have the same luck, since even for unobtained items, the progress towards the complete set
        // is what we're really measuring for luck. This might confuse some users, but it might be best / easiest.
        for (CollectionLogItem mockItem : items) {
            double actualLuck = drop.calculateLuck(mockItem, mockCollectionLog, null);
            assertEquals(expectedLuck, actualLuck, tolerance);

            double actualDryness = drop.calculateDryness(mockItem, mockCollectionLog, null);
            assertEquals(expectedDryness, actualDryness, tolerance);
        }
    }

    @Test
    public void test_setPastComplete() {
        int kc = 100;
        // expected probabilities calculated online, with the following sig digits
        double tolerance = 0.00001;

        DupeProtectedFirstSetBinomialDrop drop = new DupeProtectedFirstSetBinomialDrop(
                new RollInfo(LogItemSourceInfo.LUNAR_CHESTS_OPENED, 1.0 / 224),
                ImmutableList.of(29022, 29025, 29028, 28997)
        );

        List<CollectionLogItem> items = new ArrayList<>();
        items.add(new CollectionLogItem(29022, "Blood moon chestplate", 2, true, 0));
        items.add(new CollectionLogItem(29025, "Blood moon tassets", 1, true, 0));
        items.add(new CollectionLogItem(29028, "Blood moon helm", 1, true, 0));
        items.add(new CollectionLogItem(28997, "Dual macuahuitl", 1, true, 0));

        CollectionLog mockCollectionLog = CollectionLogLuckTestUtils.getMockCollectionLogWithKcsAndItems(
                ImmutableMap.of(LogItemSourceInfo.LUNAR_CHESTS_OPENED.getName(), kc),
                items
        );

        double actualLuck = drop.calculateLuck(items.get(0), mockCollectionLog, null);
        assertEquals(0.92594, actualLuck, tolerance);
        double actualDryness = drop.calculateDryness(items.get(0), mockCollectionLog, null);
        assertEquals(0.01043, actualDryness, tolerance);

        actualLuck = drop.calculateLuck(items.get(1), mockCollectionLog, null);
        assertEquals(0.63927, actualLuck, tolerance);
        actualDryness = drop.calculateDryness(items.get(1), mockCollectionLog, null);
        assertEquals(0.07406, actualDryness, tolerance);

        actualLuck = drop.calculateLuck(items.get(2), mockCollectionLog, null);
        assertEquals(0.63927, actualLuck, tolerance);
        actualDryness = drop.calculateDryness(items.get(2), mockCollectionLog, null);
        assertEquals(0.07406, actualDryness, tolerance);

        actualLuck = drop.calculateLuck(items.get(3), mockCollectionLog, null);
        assertEquals(0.63927, actualLuck, tolerance);
        actualDryness = drop.calculateDryness(items.get(3), mockCollectionLog, null);
        assertEquals(0.07406, actualDryness, tolerance);

    }
}