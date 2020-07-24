package net.rom.types;

import net.rom.CfgParser;

/**
 * splits the propertie on '#' into in array
 */
public class CfgStringArray implements CfgParser<String[]> {
    final private String SEPERATOR = "#";

    @Override
    public String[] parse(String value) {
        if (value != null) {
            return value.split(SEPERATOR);
        }
        return new String[0];
    }

    @Override
    public String toStringValue(Object o) {
        StringBuilder out = new StringBuilder();
        String[] strings = o instanceof String[] ? (String[]) o : null;
        if (strings != null && strings.length > 0) {
            out.append(strings[0]);
            for (int i = 1; i < strings.length; i++) {
                out.append(SEPERATOR).append(strings[i]);
            }
            return out.toString();
        }
        return "";
    }
}
