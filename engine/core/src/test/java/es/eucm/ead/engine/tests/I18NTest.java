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

package es.eucm.ead.engine.tests;

import com.badlogic.gdx.files.FileHandle;
import es.eucm.ead.engine.assets.GameAssets;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.engine.I18N.Lang;
import es.eucm.ead.engine.mock.MockApplication;
import es.eucm.ead.engine.mock.MockFiles;
import es.eucm.ead.engine.mock.MockImageUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * 
 * @author mfreire
 */
public class I18NTest {

	private I18N i18N;

	@Before
	public void setUp() {
		MockApplication.initStatics();
		assertTrue("Test i18n must be reachable",
				I18NTest.class.getResourceAsStream("/i18n_test") != null);
		i18N = new I18N(new GameAssets(new MockFiles(), new MockImageUtils()) {
			@Override
			public FileHandle resolve(String path) {
				return super.resolve("i18n_test/" + path);
			}
		});
	}

	/**
	 * Test of getAvailable method, of class I18N.
	 */
	@Test
	public void testGetAvailable() {
		List<Lang> result = i18N.getAvailable();
		assertEquals(3, result.size());
		Collections.sort(result, new Comparator<I18N.Lang>() {
			@Override
			public int compare(I18N.Lang o1, I18N.Lang o2) {
				return o1.code.compareTo(o2.code);
			}
		});
		assertEquals(result.get(0).code, "en_US");
		assertEquals(result.get(1).code, "zu");
		assertEquals(result.get(2).code, "zu_UZ");
	}

	/**
	 * Test of setLang method, of class I18N.
	 */
	@Test
	public void testSetLang() {
		// lang must now be "default"
		i18N.setLang(null);
		assertEquals("A simple string", i18N.m("simple"));

		// lang must now be default too
		i18N.setLang("en_US");
		assertEquals("A simple string", i18N.m("simple"));
		assertEquals("nonexistent", i18N.m("nonexistent")); // not in file

		// lang must now be zu
		i18N.setLang("zu");
		assertEquals("A simple string, now in Zulu", i18N.m("simple"));
		assertEquals("nonexistent", i18N.m("nonexistent")); // not in file

		// lang must now be zu_UG - pure fallback
		i18N.setLang("zu_UG");
		assertEquals("A simple string, now in Zulu", i18N.m("simple"));
		assertEquals("nonexistent", i18N.m("nonexistent")); // not in file

		// lang must now be zu_UZ
		i18N.setLang("zu_UZ");
		// fallback to Zulu
		assertEquals("A simple string, now in Zulu", i18N.m("simple"));
		assertEquals("A string, now in Uzbequistani Zulu", i18N.m("singular"));
		// warns, but does not fail
		assertEquals(
				"A string with a lot of arguments: 1-{}, 2-{}, 3-{}, 4-{}, 5-{}, 6-{}, 7-{}, 8-{}",
				i18N.m("args8"));
		assertEquals("nonexistent", i18N.m("nonexistent")); // not in file
	}

	/**
	 * Test of m method, of class I18N.
	 */
	@Test
	public void testM_String_ObjectArr() {
		i18N.setLang("zu_UZ");
		for (int i = 0; i < 1000; i += 10) {
			assertEquals("A string with " + i
					+ " arguments, now in Uzbequistani Zulu",
					i18N.m("args1", "" + i));
		}
		i18N.setLang("sv");
		for (int i = 0; i < 1000; i += 10) {
			assertEquals("A string with " + i + " arguments",
					i18N.m("args1", "" + i));
		}

		// underflow: recycle (and warn)
		assertEquals(
				"A string with a lot of arguments: 1-a, 2-b, 3-c, 4-d, 5-a, 6-b, 7-c, 8-d",
				i18N.m("args8", "a", "b", "c", "d"));
		// overflow: ignore (and warn)
		assertEquals(
				"A string with a lot of arguments: 1-a, 2-b, 3-c, 4-d, 5-e, 6-f, 7-g, 8-h",
				i18N.m("args8", "a", "b", "c", "d", "e", "f", "g", "h", "i",
						"j"));
		// no args: ignore (and warn)
		assertEquals(
				"A string with a lot of arguments: 1-{}, 2-{}, 3-{}, 4-{}, 5-{}, 6-{}, 7-{}, 8-{}",
				i18N.m("args8"));
	}

	/**
	 * Test of m method, of class I18N.
	 */
	@Test
	public void testM_4args() {
		i18N.setLang("zu_UZ");
		for (int i = 0; i < 4; i++) {
			String result = i18N.m(i, "singular", "plural", "" + i);
			if (i == 1) {
				assertEquals("A string, now in Uzbequistani Zulu", result);
			} else {
				assertEquals("A total of " + i + " strings", result);
			}
		}
	}
}
