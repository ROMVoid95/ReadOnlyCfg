package net.rom.types;

import net.rom.CfgParser;

public class CfgInteger implements CfgParser<Integer> {
    @Override
    public Integer parse(String value) {
        return Integer.parseInt(value);
    }
}
