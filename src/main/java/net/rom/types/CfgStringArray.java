package net.rom.types;

import net.rom.CfgBuild;
import net.rom.CfgParser;

/**
 * splits the propertie on '#' into in array
 */
public class CfgStringArray implements CfgParser<String[]> {
	private final transient String SEPERATOR = "#";

	@Override
	public String[] parse(String value) {
		if (value != null) {
			return value.split(SEPERATOR);
		}
		return new String[0];
	}

	@Override
	public String toStringValue(Object o) {
		String[] strings = o instanceof String[] ? (String[]) o : null;
		for (int i = 1; i < strings.length; i++) {
			try {
				StringBuilder out = new StringBuilder().append(strings[0]);
				out.append(SEPERATOR).append(strings[i]);
				return out.toString();
			} catch (Exception e) {
				CfgBuild.LOG.info("String Array Type invalid");
			}
		}
		return "";
	}
}
