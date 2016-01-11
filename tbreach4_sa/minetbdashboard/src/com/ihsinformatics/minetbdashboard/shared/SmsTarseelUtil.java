package com.ihsinformatics.minetbdashboard.shared;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class SmsTarseelUtil {

	@SuppressWarnings("rawtypes")
	public static Map convertEntrySetToMap(Set<Entry<Object, Object>> entrySet) {
		Map<Object, Object> mapFromSet = new HashMap<Object, Object>();
		for (Entry<Object, Object> entry : entrySet) {
			mapFromSet.put(entry.getKey(), entry.getValue());
		}
		return mapFromSet;
	}
}
