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

public class DupeProtectedSetBinomialDropTest {

    @Test
    public void test_setAlmostComplete() {
        int kc = 100;
        // 2 pieces obtained
        double expectedLuck = 0.91487;
        double expectedDryness = 0.01297;
        // expected probabilities calculated online, with the following sig digits
        double tolerance = 0.00001;

        DupeProtectedSetBinomialDrop drop = new DupeProtectedSetBinomialDrop(
                new RollInfo(LogItemSourceInfo.ABYSSAL_SIRE_KILLS, 1.0 / 100.0 * 62.0 / 128.0 / 3.0),
                ImmutableList.of(13276, 13275, 13274)
        );

        List<CollectionLogItem> items = new ArrayList<>();
        items.add(new CollectionLogItem(13276, "Bludgeon claw", 0, false, 0));
        items.add(new CollectionLogItem(13275, "Bludgeon spine", 1, true, 0));
        items.add(new CollectionLogItem(13274, "Bludgeon axon", 1, true, 0));

        CollectionLog mockCollectionLog = CollectionLogLuckTestUtils.getMockCollectionLogWithKcsAndItems(
                ImmutableMap.of(LogItemSourceInfo.ABYSSAL_SIRE_KILLS.getName(), kc),
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
        // 3 pieces obtained
        double expectedLuck = 0.98704;
        double expectedDryness = 0.00149;
        // expected probabilities calculated online, with the following sig digits
        double tolerance = 0.00001;

        DupeProtectedSetBinomialDrop drop = new DupeProtectedSetBinomialDrop(
                new RollInfo(LogItemSourceInfo.ABYSSAL_SIRE_KILLS, 1.0 / 100.0 * 62.0 / 128.0 / 3.0),
                ImmutableList.of(13276, 13275, 13274)
        );

        List<CollectionLogItem> items = new ArrayList<>();
        items.add(new CollectionLogItem(13276, "Bludgeon claw", 1, true, 0));
        items.add(new CollectionLogItem(13275, "Bludgeon spine", 1, true, 0));
        items.add(new CollectionLogItem(13274, "Bludgeon axon", 1, true, 0));

        CollectionLog mockCollectionLog = CollectionLogLuckTestUtils.getMockCollectionLogWithKcsAndItems(
                ImmutableMap.of(LogItemSourceInfo.ABYSSAL_SIRE_KILLS.getName(), kc),
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
        // 4 pieces obtained
        double expectedLuck = 0.99851;
        double expectedDryness = 0.00014;
        // expected probabilities calculated online, with the following sig digits
        double tolerance = 0.00001;

        DupeProtectedSetBinomialDrop drop = new DupeProtectedSetBinomialDrop(
                new RollInfo(LogItemSourceInfo.ABYSSAL_SIRE_KILLS, 1.0 / 100.0 * 62.0 / 128.0 / 3.0),
                ImmutableList.of(13276, 13275, 13274)
        );

        List<CollectionLogItem> items = new ArrayList<>();
        items.add(new CollectionLogItem(13276, "Bludgeon claw", 2, true, 0));
        items.add(new CollectionLogItem(13275, "Bludgeon spine", 1, true, 0));
        items.add(new CollectionLogItem(13274, "Bludgeon axon", 1, true, 0));

        CollectionLog mockCollectionLog = CollectionLogLuckTestUtils.getMockCollectionLogWithKcsAndItems(
                ImmutableMap.of(LogItemSourceInfo.ABYSSAL_SIRE_KILLS.getName(), kc),
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
}