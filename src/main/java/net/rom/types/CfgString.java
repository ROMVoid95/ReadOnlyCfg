package net.rom.types;

import net.rom.CfgParser;

public class CfgString implements CfgParser<String> {
    @Override
    public String parse(String value) {
        return value;
    }
}
