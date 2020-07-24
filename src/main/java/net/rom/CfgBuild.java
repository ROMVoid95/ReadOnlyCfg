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
	private final File configFile;
	private final CfgProperties properties;
	private final Class<?> configclass;
	private final Map<Class<?>, CfgParser<?>> cParsers = new HashMap<>();

	public static final Logger LOG = (Logger) LoggerFactory.getLogger(CfgBuild.class);

	public CfgBuild(Class<?> configclass, File configFile) {
		this.configFile = configFile;
		this.configclass = configclass;
		this.properties = new CfgProperties();
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
	public void build(boolean cleanfile) throws Exception {
		if (configFile == null)
			throw new IllegalStateException("File not initialized");
		if (configFile.exists()) {
			properties.load(new FileInputStream(configFile));
		}
		CfgProperties cleanProperties = new CfgProperties();
		for (Field field : configclass.getDeclaredFields()) {
			if (!field.isAnnotationPresent(Cfg.class)) {
				continue;
			}
			try {
				boolean isPrivate = !field.isAccessible();
				if (isPrivate) {
					field.setAccessible(true);
				}
				String variableName = field.getName().toLowerCase();
				Object defaultValue = field.get(null);
				Object value = configFile.exists() ? properties.getOrDefault(variableName, defaultValue) : defaultValue;
				try {
					if (cParsers.containsKey(field.getType())) {
						field.set(null, cParsers.get(field.getType()).parse(String.valueOf(value)));
						properties.setProperty(variableName,
								cParsers.get(field.getType()).toStringValue(field.get(null)));
						cleanProperties.setProperty(variableName, properties.getProperty(variableName));
					}
				} catch (Exception e) {
					LOG.error("Unknown Configuration Type. Variable name: '" + field.getName() + "'; Unknown Class: "
							+ field.getType().getName());
				}

				if (isPrivate) {
					field.setAccessible(false);
				}

			} catch (IllegalAccessException e) {
				LOG.error("Could not load configuration, IllegalAccessException", e.getCause());
			} catch (Exception e) {
				LOG.error("Could not load configuration, GenericExemption", e.getCause());
			}
		}
		if (cleanfile) {
			cleanProperties.store(new FileOutputStream(configFile), null);
		} else {
			properties.store(new FileOutputStream(configFile), null);
		}
	}

}
