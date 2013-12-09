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

package es.eucm.ead.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

/**
 * Internationalization (I18N) for the editor. This is intended to be used 
 * as a singleton.
 * @author mfreire
 */
public class I18N {

	private static Properties messages = new Properties();
	private static String lang;
	private static final String messageFileName = "messages";
	private static final String messageFileExtension = ".properties";

	private I18N() {
	}

	/**
	 * Changes the language used for string lookup.
	 * @param lang ISO-639 language code 
	 */
	public final static void setLang(String lang) {

		// loads properties, using nested defaults
		messages = new Properties();
		try {
			// attempts to load global defaults
			FileHandle propsFile = Gdx.files.internal(messageFileName
					+ messageFileExtension);
			if (propsFile.exists()) {
				Gdx.app.log("I18N", "Loading messages: " + propsFile.name());
				messages.load(propsFile.reader());
			}
			// if a language such as en_EN, attempts to load the 'en' as next default
			if (lang.indexOf('_') > 0) {
				String prefix = lang.substring(0, lang.indexOf('_'));
				propsFile = Gdx.files.internal(messageFileName + '_' + prefix
						+ messageFileExtension);
				if (propsFile.exists()) {
					Gdx.app
							.log("I18N", "Loading messages: "
									+ propsFile.name());
					messages = new Properties(messages);
					messages.load(propsFile.reader());
				}
			}
			// attempts to load specified language over previous defaults
			propsFile = Gdx.files.internal(messageFileName + '_' + lang
					+ messageFileExtension);
			if (propsFile.exists()) {
				Gdx.app.log("I18N", "Loading messages: " + propsFile.name());
				messages = new Properties(messages);
				messages.load(propsFile.reader());
			}
		} catch (IOException ioe) {
			Gdx.app.error("I18N", "error loading messages");
		}

		Gdx.app.log("I18N", "Loaded all messages (" + messages.size()
				+ " total); lang is " + lang);
	}

	/**
	 * Simple internationalized (i18n) message lookup.
	 * @param key
	 * @param args which will substitute each of the '{}' patterns found in the
	 * text, in order.
	 * @return 
	 */
	public static String m(String key, Object... args) {
		String result = messages.getProperty(key);
		if (result == null) {
			Gdx.app.log("I18N", "No message for key " + key + ", lang " + lang);
			result = key;
		}
		int end = result.indexOf("{}");
		if (end == -1) {
			return result;
		} else if (args.length == 0) {
			return result;
		}
		StringBuilder replaced = new StringBuilder(result.length());
		int currentArg = 0;
		int start = 0;
		do {
			if (currentArg == args.length) {
				Gdx.app.log("I18N", "Substitution requested in key " + key
						+ " " + " but not enough args " + Arrays.toString(args)
						+ " provided for message '" + result + "'");
				if (args.length == 0) {
					return result;
				} else {
					currentArg %= args.length;
				}
			}
			replaced.append(result.substring(start, end)).append(
					args[currentArg]);
			currentArg++;
			start = end + "{}".length();
			end = result.indexOf("{}", start);
		} while (end != -1);
		replaced.append(result.substring(start));
		return replaced.toString();
	}

	/**
	 * Convenience method to return an internationalized message 
	 * that varies in plurality according to the first parameter. 
	 * For example "1 message found, delete it?" 
	 * vs "2 messages found, delete them?"
	 * @param cardinality either 1 (singular) or any other number (plural)
	 * @param keyOne for singular messages
	 * @param keyMany for plural messages
	 * @param args same as those used in the cardinality-independent version
	 * @return 
	 */
	public static String m(int cardinality, String keyOne, String keyMany,
			Object... args) {
		return m(cardinality == 1 ? keyOne : keyMany, args);
	}

}
