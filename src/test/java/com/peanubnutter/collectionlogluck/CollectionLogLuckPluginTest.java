package com.peanubnutter.collectionlogluck;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class CollectionLogLuckPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(CollectionLogLuckPlugin.class);
		RuneLite.main(args);
	}
}