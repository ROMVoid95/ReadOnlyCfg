package net.rom.types;

import net.rom.CfgParser;

public class CfgFloat implements CfgParser<Float> {
    @Override
    public Float parse(String value) {
        return Float.parseFloat(value);
    }
}
