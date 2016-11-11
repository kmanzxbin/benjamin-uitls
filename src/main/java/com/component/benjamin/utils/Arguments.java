package com.component.benjamin.utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class Arguments {

	Map<String, String> map;
	String indexSymbol = "=";

	public Arguments() {
		this(null);
	}

	public Arguments(String[] args) {
		this(null, args);
	}

	public Arguments(Map<String, String> map, String[] args) {
		this.map = new LinkedHashMap<String, String>();
		if (map != null) {
			this.map.putAll(map);
		}

		if (args != null) {
			this.map.putAll(toMap(args));
		}
	}

	public Map<String, String> toMap(String[] args) {
		if (args != null) {
			Map<String, String> map = new LinkedHashMap<String, String>();
			if (args.length > 0) {
				for (String string : args) {
					int index = string.indexOf(indexSymbol);
					if (index == -1) {
						throw new RuntimeException("can not find " + indexSymbol + " in " + string);
					}
					map.put(string.substring(0, index), string.substring(index + 1));
				}
			}
			return map;
		}
		return null;
	}

	public void setArgs(String[] args) {
		if (args != null) {
			map.putAll(toMap(args));
		}
	}

	public void set(String key, String value) {
		map.put(key, value);
	}

	public void set(String key, int value) {
		map.put(key, value + "");
	}

	public void set(String key, long value) {
		map.put(key, value + "");
	}

	public void set(String key, float value) {
		map.put(key, value + "");
	}

	public void set(String key, double value) {
		map.put(key, value + "");
	}

	public int getInt(String key) {
		return getInt(key, 0);
	}

	public int getInt(String key, int defaultValue) {
		try {
			return Integer.valueOf(map.get(key));
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public long getLong(String key) {
		return getLong(key, 0l);
	}

	public long getLong(String key, long defaultValue) {
		try {
			return Long.valueOf(map.get(key));
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public float getFloat(String key) {
		return getFloat(key, 0f);
	}

	public float getFloat(String key, float defaultValue) {
		try {
			return Float.valueOf(map.get(key));
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public double getDouble(String key) {
		return getDouble(key, 0d);
	}

	public double getDouble(String key, double defaultValue) {
		try {
			return Double.valueOf(map.get(key));
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public String getString(String key) {
		return getString(key, null);
	}

	public String getString(String key, String defaultValue) {
		try {
			return (map.get(key));
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public String toString() {
		return super.toString() + " " + map.toString();
	}
}
