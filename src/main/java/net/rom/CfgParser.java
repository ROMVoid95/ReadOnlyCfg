package net.rom;

public interface CfgParser<Type> {
	
	Type parse(String value);
	
	default String toStringValue(Object o) {
		return String.valueOf(o);
	}
}
