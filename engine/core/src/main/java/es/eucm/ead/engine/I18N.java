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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package es.eucm.ead.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

/**
 * Internationalization (I18N) for eAdventure. This is intended to be used as a
 * singleton.
 * 
 * @author mfreire
 */
public class I18N {

	private static final String messageFileName = "i18n/messages";
	private static final String messageFileExtension = ".properties";
	private static final String languageIndex = "i18n/i18n.properties";
	private static final String defaultLanguage = "default";

	private static final String argMarker = "{}";

	private static Properties messages = new Properties();
	private static String lang;

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

	private static final ArrayList<Lang> available = new ArrayList<Lang>();

	// avoid accidental instantiation
	private I18N() {

	}

	/**
	 * @return a list of available languages
	 */
	public static ArrayList<Lang> getAvailable() {
		if (available.isEmpty()) {
			Properties all = new Properties();
			try {
				all.load(Engine.assets.resolve(languageIndex).reader());
				for (Object k : all.keySet()) {
					String fileName = k.equals(defaultLanguage) ? (messageFileName + messageFileExtension)
							: (messageFileName + '_' + k + messageFileExtension);
					if (Engine.assets.resolve(fileName).exists()) {
						available
								.add(new Lang("" + k, all.getProperty("" + k)));
					} else {
						Gdx.app.log("I18N", "Referenced in " + languageIndex
								+ " but not found: " + fileName);
					}
				}
			} catch (IOException ioe) {
				Gdx.app
						.error("I18N", "Error locating available languages",
								ioe);
			}
		}
		return available;
	}

	/**
	 * Changes the language used for string lookup.
	 * 
	 * @param lang
	 *            ISO-639 language code; null or any other not-found value
	 *            will be interpreted as the default language
	 */
	public static void setLang(String lang) {
		if (lang == null || defaultLanguage.equals(lang) || lang.isEmpty()) {
			lang = "";
		}

		I18N.lang = lang;
		// loads properties, using nested defaults
		I18N.messages = new Properties();
		try {
			// global defaults (messages.properties)
			overlayMessages("");

			// specific language (more specific: messages_en.properties)
			int first = lang.indexOf('_');
			String langWithoutCountry = (first == -1) ? lang : lang.substring(
					0, first);
			overlayMessages("_" + langWithoutCountry);

			// language + country (most specific: messages_en_US.properties)
			if (first > 0) {
				overlayMessages("_" + lang);
			}
			Gdx.app.log("I18N", "Loaded all messages (" + I18N.messages.size()
					+ " total); lang is " + lang);
		} catch (IOException e) {
			Gdx.app.error("I18N", "Error loading messages", e);
		}
	}

	/**
	 * Overlays current messages with more-specific variants. The previous
	 * messages will be used as defaults for non-locatable keys.
	 * @param suffix - something like "_es_ES", "_es" or ""
	 * @throws IOException 
	 */
	private static void overlayMessages(String suffix) throws IOException {
		FileHandle m = Engine.assets.resolve(messageFileName + suffix
				+ messageFileExtension);
		if (m.exists()) {
			Gdx.app.log("I18N", "Loading messages: " + m.name());
			I18N.messages = new Properties(messages);
			I18N.messages.load(m.reader());
		} else {
			Gdx.app.error("I18N", "Missing properties file: " + m.path());
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
	public static String m(String key, Object... args) {
		String result = messages.getProperty(key);
		if (result == null) {
			Gdx.app.log("I18N", "No message for key " + key + ", lang " + lang);
			result = key;
		}

		int end = result.indexOf(argMarker);
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
				// tried substitutions more substitutions than args; probably an error
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
	public static String m(int cardinality, String keyOne, String keyMany,
			Object... args) {
		return m(cardinality == 1 ? keyOne : keyMany, args);
	}

}
