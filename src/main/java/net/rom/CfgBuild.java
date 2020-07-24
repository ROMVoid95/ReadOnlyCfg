package net.rom;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.rom.types.CfgBoolean;
import net.rom.types.CfgDouble;
import net.rom.types.CfgFloat;
import net.rom.types.CfgInteger;
import net.rom.types.CfgLong;
import net.rom.types.CfgString;
import net.rom.types.CfgStringArray;

public class CfgBuild {
	private final transient CfgProperties properties;
	private final transient Class<?> configclass;
	private final transient Map<Class<?>, CfgParser<?>> cParsers = new HashMap<>();
	public static final Logger LOG = (Logger) LoggerFactory.getLogger(CfgBuild.class);

	public CfgBuild(Class<?> configclass, File configFile) throws Exception {
		this.configclass = configclass;
		this.properties = new CfgProperties();
		if (!configFile.exists())
			make(configFile);
		else
			loadParsers();
	}

	/**
	 * loads the configuration parsers for each type
	 */
	private void loadParsers() {
		ArrayList<Class<? extends CfgParser<?>>> classes = new ArrayList<>();
		classes.add(CfgBoolean.class);
		classes.add(CfgFloat.class);
		classes.add(CfgInteger.class);
		classes.add(CfgLong.class);
		classes.add(CfgString.class);
		classes.add(CfgStringArray.class);
		classes.add(CfgDouble.class);
		cParsers.put(int.class, new CfgInteger());
		cParsers.put(boolean.class, new CfgBoolean());
		cParsers.put(double.class, new CfgDouble());
		cParsers.put(long.class, new CfgLong());
		cParsers.put(float.class, new CfgFloat());
		for (Class<? extends CfgParser<?>> parserclass : classes) {
			try {
				Class<?> parserType = (Class<?>) ((ParameterizedType) parserclass.getGenericInterfaces()[0])
						.getActualTypeArguments()[0];
				CfgParser<?> parserInstance = parserclass.getConstructor().newInstance();
				cParsers.put(parserType, parserInstance);
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException
					| NoSuchMethodException e) {
				LOG.error(e.getMessage(), e.getCause());
			}
		}
	}

	/**
	 * Updates the configClass's variables with the configFile's values
	 *
	 * @param cleanfile clear the File of all undefined variables
	 * @throws IOException file can't be accessed
	 */
	private void make(File configFile) throws Exception {
		if (configFile == null)
			throw new IllegalStateException("File not initialized");
		if (configFile.exists()) {
			properties.load(new FileInputStream(configFile));
		}
		for (Field field : configclass.getDeclaredFields()) {
			if (!field.isAnnotationPresent(Cfg.class)) {
				continue;
			}
			try {
				boolean isPrivate = !field.isAccessible();
				if (isPrivate) {
					field.setAccessible(true);
				}

				if (cParsers.containsKey(field.getType())) {
					String variableName = field.getName().toLowerCase(Locale.ROOT);
					field.set(null, cParsers.get(field.getType())
							.parse(String.valueOf(properties.getOrDefault(variableName, ""))));
					properties.setProperty(variableName, cParsers.get(field.getType()).toStringValue(field.get(null)));
				}
				if (isPrivate) {
					field.setAccessible(false);
				}
			} catch (IllegalAccessException e) {
				LOG.error("Could not load configuration, IllegalAccessException", e.getCause());
			} catch (Exception e) {
				LOG.error("Could not load configuration, GenericExemption", e.getCause());
			}

			properties.store(new FileOutputStream(configFile), null);
		}
	}

}
