package net.rom.types;

import net.rom.CfgParser;

public class CfgLong implements CfgParser<Long> {
    @Override
    public Long parse(String value) {
        return Long.parseLong(value);
    }
}
