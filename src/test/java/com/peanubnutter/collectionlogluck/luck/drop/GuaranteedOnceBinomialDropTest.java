package com.peanubnutter.collectionlogluck.luck.drop;

import com.peanubnutter.collectionlogluck.model.CollectionLog;
import com.peanubnutter.collectionlogluck.model.CollectionLogItem;
import com.peanubnutter.collectionlogluck.luck.LogItemSourceInfo;
import com.peanubnutter.collectionlogluck.luck.RollInfo;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GuaranteedOnceBinomialDropTest {

    @Test
    public void testGuaranteedOnceBinomial_justBeforeGuaranteedDrop() {
        double dropChance = 0.01;
        int kc = 100;
        int dropGuaranteedOnKc = 101;
        int numObtained = 1;
        double expectedLuck = 0.36603;
        double expectedDryness = 0.26424;
        // expected probabilities calculated online, with the following sig digits
        double tolerance = 0.00001;

        GuaranteedOnceBinomialDrop drop = new GuaranteedOnceBinomialDrop(new RollInfo(LogItemSourceInfo.VORKATH_KILLS, dropChance), dropGuaranteedOnKc);

        CollectionLogItem mockItem = new CollectionLogItem(1234, "a vorkath head or something", numObtained, true, 0);

        CollectionLog mockCollectionLog = CollectionLogLuckTestUtils.getMockCollectionLogWithKc(
                LogItemSourceInfo.VORKATH_KILLS.getName(), kc);

        double actualLuck = drop.calculateLuck(mockItem, mockCollectionLog, null);
        assertEquals(expectedLuck, actualLuck, tolerance);

        double actualDryness = drop.calculateDryness(mockItem, mockCollectionLog, null);
        assertEquals(expectedDryness, actualDryness, tolerance);
    }

    @Test
    public void testGuaranteedOnceBinomial_atGuaranteedDrop() {
        double dropChance = 0.01;
        int kc = 101;
        int dropGuaranteedOnKc = 101;
        int numObtained = 2;
        double expectedLuck = 0.36603;
        double expectedDryness = 0.26424;
        // expected probabilities calculated online, with the following sig digits
        double tolerance = 0.00001;

        GuaranteedOnceBinomialDrop drop = new GuaranteedOnceBinomialDrop(new RollInfo(LogItemSourceInfo.VORKATH_KILLS, dropChance), dropGuaranteedOnKc);

        CollectionLogItem mockItem = new CollectionLogItem(1234, "a vorkath head or something", numObtained, true, 0);

        CollectionLog mockCollectionLog = CollectionLogLuckTestUtils.getMockCollectionLogWithKc(
                LogItemSourceInfo.VORKATH_KILLS.getName(), kc);

        double actualLuck = drop.calculateLuck(mockItem, mockCollectionLog, null);
        assertEquals(expectedLuck, actualLuck, tolerance);

        double actualDryness = drop.calculateDryness(mockItem, mockCollectionLog, null);
        assertEquals(expectedDryness, actualDryness, tolerance);
    }

}