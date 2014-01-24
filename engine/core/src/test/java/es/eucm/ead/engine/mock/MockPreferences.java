/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2013 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          C Profesor Jose Garcia Santesmases sn,
 *          28040 Madrid (Madrid), Spain.
 *
 *          For more info please visit:  <http://e-adventure.e-ucm.es> or
 *          <http://www.e-ucm.es>
 *
 * ****************************************************************************
 *
 *  This file is part of eAdventure
 *
 *      eAdventure is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU Lesser General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      (at your option) any later version.
 *
 *      eAdventure is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU Lesser General Public License for more details.
 *
 *      You should have received a copy of the GNU Lesser General Public License
 *      along with eAdventure.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.eucm.ead.engine.mock;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.StreamUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

public class MockPreferences implements Preferences {
	private final String name;
	private final Properties properties = new Properties();
	private final FileHandle file;

	public MockPreferences(String name, String directory) {
		this(new MockFileHandle(new File(directory, name), FileType.External));
	}

	public MockPreferences(FileHandle file) {
		this.name = file.name();
		this.file = file;
		if (!file.exists())
			return;
		InputStream in = null;
		try {
			in = new BufferedInputStream(file.read());
			properties.loadFromXML(in);
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			StreamUtils.closeQuietly(in);
		}
	}

	@Override
	public void putBoolean(String key, boolean val) {
		properties.put(key, Boolean.toString(val));
	}

	@Override
	public void putInteger(String key, int val) {
		properties.put(key, Integer.toString(val));
	}

	@Override
	public void putLong(String key, long val) {
		properties.put(key, Long.toString(val));
	}

	@Override
	public void putFloat(String key, float val) {
		properties.put(key, Float.toString(val));
	}

	@Override
	public void putString(String key, String val) {
		properties.put(key, val);
	}

	@Override
	public void put(Map<String, ?> vals) {
		for (Entry<String, ?> val : vals.entrySet()) {
			if (val.getValue() instanceof Boolean)
				putBoolean(val.getKey(), (Boolean) val.getValue());
			if (val.getValue() instanceof Integer)
				putInteger(val.getKey(), (Integer) val.getValue());
			if (val.getValue() instanceof Long)
				putLong(val.getKey(), (Long) val.getValue());
			if (val.getValue() instanceof String)
				putString(val.getKey(), (String) val.getValue());
			if (val.getValue() instanceof Float)
				putFloat(val.getKey(), (Float) val.getValue());
		}
	}

	@Override
	public boolean getBoolean(String key) {
		return getBoolean(key, false);
	}

	@Override
	public int getInteger(String key) {
		return getInteger(key, 0);
	}

	@Override
	public long getLong(String key) {
		return getLong(key, 0);
	}

	@Override
	public float getFloat(String key) {
		return getFloat(key, 0);
	}

	@Override
	public String getString(String key) {
		return getString(key, "");
	}

	@Override
	public boolean getBoolean(String key, boolean defValue) {
		return Boolean.parseBoolean(properties.getProperty(key,
				Boolean.toString(defValue)));
	}

	@Override
	public int getInteger(String key, int defValue) {
		return Integer.parseInt(properties.getProperty(key,
				Integer.toString(defValue)));
	}

	@Override
	public long getLong(String key, long defValue) {
		return Long.parseLong(properties.getProperty(key,
				Long.toString(defValue)));
	}

	@Override
	public float getFloat(String key, float defValue) {
		return Float.parseFloat(properties.getProperty(key,
				Float.toString(defValue)));
	}

	@Override
	public String getString(String key, String defValue) {
		return properties.getProperty(key, defValue);
	}

	@Override
	public Map<String, ?> get() {
		Map<String, Object> map = new HashMap<String, Object>();
		for (Entry<Object, Object> val : properties.entrySet()) {
			if (val.getValue() instanceof Boolean)
				map.put((String) val.getKey(),
						(Boolean) Boolean.parseBoolean((String) val.getValue()));
			if (val.getValue() instanceof Integer)
				map.put((String) val.getKey(),
						(Integer) Integer.parseInt((String) val.getValue()));
			if (val.getValue() instanceof Long)
				map.put((String) val.getKey(),
						(Long) Long.parseLong((String) val.getValue()));
			if (val.getValue() instanceof String)
				map.put((String) val.getKey(), (String) val.getValue());
			if (val.getValue() instanceof Float)
				map.put((String) val.getKey(),
						(Float) Float.parseFloat((String) val.getValue()));
		}

		return map;
	}

	@Override
	public boolean contains(String key) {
		return properties.containsKey(key);
	}

	@Override
	public void clear() {
		properties.clear();
	}

	@Override
	public void flush() {
		OutputStream out = null;
		try {
			out = new BufferedOutputStream(file.write(false));
			properties.storeToXML(out, null);
		} catch (Exception ex) {
			throw new GdxRuntimeException("Error writing preferences: " + file,
					ex);
		} finally {
			StreamUtils.closeQuietly(out);
		}
	}

	@Override
	public void remove(String key) {
		properties.remove(key);
	}
}
