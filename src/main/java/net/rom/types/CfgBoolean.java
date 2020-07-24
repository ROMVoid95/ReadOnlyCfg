package net.rom.types;

import net.rom.CfgParser;

public class CfgBoolean implements CfgParser<Boolean> {
    @Override
    public Boolean parse(String value) {
        return Boolean.parseBoolean(value);
    }
}
