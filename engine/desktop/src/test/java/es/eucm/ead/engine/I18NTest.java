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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.junit.BeforeClass;
import org.junit.Test;

import es.eucm.ead.engine.application.TestApplication;

/**
 *
 * @author mfreire
 */
public class I18NTest {

	@BeforeClass
	public static void setUpClass() {
		assertTrue("Test i18n must be reachable", I18NTest.class
				.getResourceAsStream("/i18n_test/i18n") != null);
		Engine engine = new Engine("@i18n_test");
		new TestApplication(engine, 800, 600);
		engine.create();
	}

	/**
	 * Test of getAvailable method, of class I18N.
	 */
	@Test
	public void testGetAvailable() {
		System.out.println("getAvailable");
		ArrayList<I18N.Lang> result = I18N.getAvailable();
		assertEquals(result.size(), 3);
		Collections.sort(result, new Comparator<I18N.Lang>() {
			@Override
			public int compare(I18N.Lang o1, I18N.Lang o2) {
				return o1.code.compareTo(o2.code);
			}
		});
		assertEquals(result.get(0).code, "default");
		assertEquals(result.get(1).code, "zu");
		assertEquals(result.get(2).code, "zu_UZ");
	}

	/**
	 * Test of setLang method, of class I18N.
	 */
	@Test
	public void testSetLang() {
		I18N.Type desktopType = I18N.Type.DESKTOP;
		// lang must now be "default"
		I18N.setLang(null, desktopType);
		assertEquals("A simple string", I18N.m("simple"));

		// lang must now be default too 
		I18N.setLang("default", desktopType);
		assertEquals("A simple string", I18N.m("simple"));
		assertEquals("nonexistent", I18N.m("nonexistent")); // not in file

		// lang must now be zu
		I18N.setLang("zu", desktopType);
		assertEquals("A simple string, now in Zulu", I18N.m("simple"));
		assertEquals("nonexistent", I18N.m("nonexistent")); // not in file

		// lang must now be zu_UG - pure fallback
		I18N.setLang("zu_UG", desktopType);
		assertEquals("A simple string, now in Zulu", I18N.m("simple"));
		assertEquals("nonexistent", I18N.m("nonexistent")); // not in file

		// lang must now be zu_UZ
		I18N.setLang("zu_UZ", desktopType);
		// fallback to Zulu
		assertEquals("A simple string, now in Zulu", I18N.m("simple"));
		assertEquals("A string, now in Uzbequistani Zulu", I18N.m("singular"));
		// warns, but does not fail
		assertEquals(
				"A string with a lot of arguments: 1-{}, 2-{}, 3-{}, 4-{}, 5-{}, 6-{}, 7-{}, 8-{}",
				I18N.m("args8"));
		assertEquals("nonexistent", I18N.m("nonexistent")); // not in file
	}

	/**
	 * Test of m method, of class I18N.
	 */
	@Test
	public void testM_String_ObjectArr() {
		I18N.Type desktopType = I18N.Type.DESKTOP;
		I18N.setLang("zu_UZ", desktopType);
		for (int i = 0; i < 1000; i += 10) {
			assertEquals("A string with " + i
					+ " arguments, now in Uzbequistani Zulu", I18N.m("args1",
					"" + i));
		}
		I18N.setLang("sv", desktopType);
		for (int i = 0; i < 1000; i += 10) {
			assertEquals("A string with " + i + " arguments", I18N.m("args1",
					"" + i));
		}

		// underflow: recycle (and warn)
		assertEquals(
				"A string with a lot of arguments: 1-a, 2-b, 3-c, 4-d, 5-a, 6-b, 7-c, 8-d",
				I18N.m("args8", "a", "b", "c", "d"));
		// overflow: ignore (and warn)
		assertEquals(
				"A string with a lot of arguments: 1-a, 2-b, 3-c, 4-d, 5-e, 6-f, 7-g, 8-h",
				I18N.m("args8", "a", "b", "c", "d", "e", "f", "g", "h", "i",
						"j"));
		// no args: ignore (and warn)
		assertEquals(
				"A string with a lot of arguments: 1-{}, 2-{}, 3-{}, 4-{}, 5-{}, 6-{}, 7-{}, 8-{}",
				I18N.m("args8"));
	}

	/**
	 * Test of m method, of class I18N.
	 */
	@Test
	public void testM_4args() {
		I18N.Type desktopType = I18N.Type.DESKTOP;
		I18N.setLang("zu_UZ", desktopType);
		for (int i = 0; i < 4; i++) {
			String result = I18N.m(i, "singular", "plural", "" + i);
			if (i == 1) {
				assertEquals("A string, now in Uzbequistani Zulu", result);
			} else {
				assertEquals("A total of " + i + " strings", result);
			}
		}
	}
}
