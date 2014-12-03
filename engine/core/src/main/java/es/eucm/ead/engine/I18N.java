/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2014 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          CL Profesor Jose Garcia Santesmases 9,
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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package es.eucm.ead.engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import es.eucm.ead.engine.assets.Assets;

/**
 * Internationalization (I18N) for eAdventure. This class is intended to be used
 * as a singleton.
 * 
 * @author mfreire
 */
public class I18N {

	/**
	 * used in language index file to refer to the key to use as default
	 * language
	 */
	public static final String DEFAULT_LANGUAGE_KEY = "default";
	/** name of index file: references available languages and sets default */
	public static final String LANGUAGE_INDEX = "/i18n.props";
	/** prefix for internationalized message files */
	public static final String MESSAGE_FILE_NAME = "/messages";
	/** extension for internationalized message files */
	public static final String MESSAGE_FILE_EXTENSION = ".props";
	/** used within message files to indicate parameters */
	public static final String ARG_MARKER = "{}";

	private Assets assets;
	private String currentLanguage;
	private String defaultLanguage;

	private String i18nPath = "i18n";

	private Map<String, String> messages;
	private List<Lang> available = new ArrayList<Lang>();

	public I18N(Assets assets) {
		this.assets = assets;
		messages = new HashMap<String, String>();
	}

	/**
	 * Sets the root where all i18n files are
	 */
	public void setI18nPath(String i18nPath) {
		this.i18nPath = i18nPath;
	}

	/**
	 * Reads and returns the list of available languages from the manifest (see
	 * {@link #LANGUAGE_INDEX}); the language manifest MUST contain a single
	 * {@link #DEFAULT_LANGUAGE_KEY}) line, indicating which of the remaining
	 * entries will be used as a fallback during internationalized (i18n) string
	 * lookup.
	 * 
	 * @return a list of available languages
	 */
	public List<Lang> getAvailable() {
		if (available.isEmpty()) {
			Map<String, String> all = new HashMap<String, String>();
			FileHandle i18nFile = assets.resolve(i18nPath + LANGUAGE_INDEX);
			if (assets.checkFileExistence(i18nFile)) {
				load(i18nFile, all);
			}

			boolean defaultFound = false;

			for (String k : all.keySet()) {
				String fileName;
				if (DEFAULT_LANGUAGE_KEY.equals(k)) {
					defaultLanguage = all.get(k);
					continue;
				}
				fileName = i18nPath + MESSAGE_FILE_NAME + '_' + k
						+ MESSAGE_FILE_EXTENSION;
				if (assets.checkFileExistence(assets.resolve(fileName))) {
					Lang lang = new Lang(k, all.get(k));
					available.add(lang);
					if (k.equals(defaultLanguage)) {
						defaultFound = true;
					}
				} else {
					Gdx.app.log("I18N", "Referenced in " + i18nPath
							+ LANGUAGE_INDEX + " but not found: " + fileName);
				}
			}

			if (!defaultFound) {
				Gdx.app.error("I18N", "Default language (" + defaultLanguage
						+ ") according to " + i18nPath + LANGUAGE_INDEX
						+ " not found.");
			}
		}
		return available;
	}

	/**
	 * @return if the given language is available
	 */
	public boolean isAvailable(String language) {
		for (Lang lang : getAvailable()) {
			if (lang.code.equals(language)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Changes the language used for string lookup.
	 * 
	 * @param lang
	 *            ISO-639 language code; or null for default language
	 */
	public void setLang(String lang) {

		if (defaultLanguage == null) {
			// makes sure that defaultLanguage has been set
			getAvailable();
		}
		currentLanguage = (lang == null || lang.isEmpty()) ? defaultLanguage
				: lang;

		// loads properties, using nested defaults
		messages.clear();
		try {
			// global defaults (messages.properties)
			overlayMessages("_" + defaultLanguage);

			if (defaultLanguage != null
					&& !defaultLanguage.equals(currentLanguage)) {
				// specific language (more specific: messages_en.properties)
				int first = currentLanguage.indexOf('_');
				String langWithoutCountry = (first == -1) ? currentLanguage
						: currentLanguage.substring(0, first);
				overlayMessages("_" + langWithoutCountry);

				// language + country (most specific: messages_en_US.properties)
				if (first > 0) {
					overlayMessages("_" + currentLanguage);
				}
				Gdx.app.log("I18N", "Loaded all messages (" + messages.size()
						+ " total); lang is '" + currentLanguage
						+ "', default is '" + defaultLanguage + "'");
			}
		} catch (IOException e) {
			Gdx.app.error("I18N", "Error loading messages", e);
		}
	}

	/**
	 * @return the code of the current language.
	 */
	public String getLang() {
		return currentLanguage;
	}

	/**
	 * Overlays current messages with another variant. The previous messages
	 * will be used as defaults for non-locatable keys.
	 * 
	 * @param suffix
	 *            - something like "_es_ES" or "_es"
	 * @throws IOException
	 */
	private void overlayMessages(String suffix) throws IOException {
		FileHandle fileHandle = assets.resolve(i18nPath + MESSAGE_FILE_NAME
				+ suffix + MESSAGE_FILE_EXTENSION);
		if (assets.checkFileExistence(fileHandle)) {
			load(fileHandle, messages);
		} else {
			Gdx.app.error("I18N",
					"Missing properties file: " + fileHandle.path());
		}
	}

	private void load(FileHandle fileHandle, Map<String, String> map) {
		String[] lines = fileHandle.readString().split("\n");
		for (String line : lines) {
			if (line.matches("^\\s*[#].*")) {
				// ignore line-comments
				continue;
			}
			int equalsIndex = line.indexOf('=');
			if (equalsIndex == -1) {
				continue;
			}
			String key = line.substring(0, equalsIndex).trim();
			String value = line.substring(equalsIndex + 1).trim();
			map.put(key, value);
		}
	}

	/**
	 * Simple internationalized (i18n) message lookup.
	 * 
	 * @param key
	 *            string key
	 * @param args
	 *            which will substitute each of the '{}' patterns found in the
	 *            text, in order.
	 * @return the i18n string
	 */
	public String m(String key, Object... args) {
		if (defaultLanguage == null) {
			// makes sure that defaultLanguage has been set
			getAvailable();
			setLang(defaultLanguage);
		}

		String result = messages.get(key);
		if (result == null) {
			Gdx.app.log("I18N", "No message for key " + key + ", lang '"
					+ currentLanguage + "'");
			result = key;
		}

		int end = result.indexOf(ARG_MARKER);
		if (end == -1) {
			if (args.length != 0) {
				Gdx.app.log("I18N", "Extra args passed to " + key + ": "
						+ Arrays.toString(args) + " but none expected in '"
						+ result + "'");
			}
			return result;
		} else if (args.length == 0) {
			Gdx.app.log("I18N", "No args passed to " + key
					+ ": but expected at least 1 in '" + result + "'");
			return result;
		}
		StringBuilder replaced = new StringBuilder(result.length());
		int currentArg = 0;
		int start = 0;
		int substitutions = 0;
		do {
			if (currentArg == args.length) {
				// tried substitutions more substitutions than args; probably an
				// error
				Gdx.app.log("I18N", "Substitution requested in key " + key
						+ " " + " but not enough args " + Arrays.toString(args)
						+ " provided for message '" + result
						+ "'; recycling args");
				currentArg = 0;
			}
			replaced.append(result.substring(start, end)).append(
					args[currentArg]);
			currentArg++;
			substitutions++;
			start = end + "{}".length();
			end = result.indexOf("{}", start);
		} while (end != -1);

		if (currentArg != args.length) {
			Gdx.app.log("I18N", "Bad number of args (" + args.length
					+ ") passed to " + key + ": expected " + substitutions
					+ " args for '" + result + "'");
		}

		replaced.append(result.substring(start));
		return replaced.toString();
	}

	/**
	 * Convenience method to return an internationalized message that varies in
	 * plurality according to the first parameter. For example
	 * "1 message found, delete it?" vs "2 messages found, delete them?"
	 * 
	 * @param cardinality
	 *            either 1 (singular) or any other number (plural)
	 * @param keyOne
	 *            for singular messages
	 * @param keyMany
	 *            for plural messages
	 * @param args
	 *            same as those used in the cardinality-independent version
	 * @return
	 */
	public String m(int cardinality, String keyOne, String keyMany,
			Object... args) {
		return m(cardinality == 1 ? keyOne : keyMany, args);
	}

	/**
	 * A language, with a symbolic name
	 */
	public static class Lang {
		public final String code;
		public final String name;

		private Lang(String code, String name) {
			this.code = code;
			this.name = name;
		}

		@Override
		public String toString() {
			return code + " = " + name;
		}
	}
}
