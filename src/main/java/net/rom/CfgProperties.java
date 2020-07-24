package net.rom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

public class CfgProperties extends Properties {

	private static final long serialVersionUID = -4808862176310382672L;

	@Override
	public Enumeration<Object> keys() {
		Enumeration<Object> keysEnum = super.keys();
		List<Object> keyList = new ArrayList<Object>();

		while (keysEnum.hasMoreElements()) {
			keyList.add(keysEnum.nextElement());
		}
		keyList.sort(Comparator.comparing(Object::toString));
		return Collections.enumeration(keyList);
	}
}
