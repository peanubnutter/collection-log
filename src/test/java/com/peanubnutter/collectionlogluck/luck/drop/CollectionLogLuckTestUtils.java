package com.peanubnutter.collectionlogluck.luck.drop;

import com.google.common.collect.ImmutableMap;
import com.peanubnutter.collectionlogluck.model.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CollectionLogLuckTestUtils {

    public static CollectionLog getMockCollectionLogWithKcsAndItems(
            Map<String, Integer> sourceToKcMap,
            List<CollectionLogItem> collectionLogItems) {
        List<CollectionLogKillCount> killCounts = sourceToKcMap.entrySet().stream()
                .map(entry -> new CollectionLogKillCount(entry.getKey(), entry.getValue(), 0))
                .collect(Collectors.toList());

        List<CollectionLogItem> pageItems = collectionLogItems;

        CollectionLogPage mockPage = new CollectionLogPage("some page", pageItems, killCounts, true);
        Map<String, CollectionLogPage> pages = ImmutableMap.of(mockPage.getName(), mockPage);

        CollectionLogTab mockTab = new CollectionLogTab("some page", pages);
        Map<String, CollectionLogTab> tabs = ImmutableMap.of(mockTab.getName(), mockTab);

        return new CollectionLog("someusername", 0, 0, 0, 0, tabs);
    }

    public static CollectionLog getMockCollectionLogWithKcs(Map<String, Integer> sourceToKcMap) {
        return getMockCollectionLogWithKcsAndItems(sourceToKcMap, Collections.emptyList());
    }

    public static CollectionLog getMockCollectionLogWithKc(String itemSourceName, int kc) {
        return getMockCollectionLogWithKcs(ImmutableMap.of(itemSourceName, kc));
    }

}
