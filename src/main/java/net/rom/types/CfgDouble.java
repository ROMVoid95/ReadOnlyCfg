package net.rom.types;

import net.rom.CfgParser;

public class CfgDouble implements CfgParser<Double> {
    @Override
    public Double parse(String value) {
        return Double.parseDouble(value);
    }
}
