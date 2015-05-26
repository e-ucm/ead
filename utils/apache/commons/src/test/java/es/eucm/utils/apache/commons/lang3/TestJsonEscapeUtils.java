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
package es.eucm.utils.apache.commons.lang3;

import com.badlogic.gdx.utils.Array;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

/**
 * Created by jtorrente on 23/05/2015.
 */
public class TestJsonEscapeUtils {

	static String STR1 = "String";
	static String STR2 = "\"String\"";
	static String STR3 = "String with spaces and strange symbols ;:= ñ á";
	static String STR4 = "http://www.test.com";

	static class Obj {
		String[] strArray = new String[] { STR1, null };
		String[] nullArray = null;
		boolean bool = false;
		int[] intArray = new int[] { 2, 3 };
		String str = STR2;

		Array<String> strLArray = new Array<String>(new String[] { null, STR3 });
		Array<Obj> objLArray = null;

		HashMap<Integer, String> map = new HashMap<Integer, String>();
		ArrayList<String> arrayList = new ArrayList<String>();

		public Obj(boolean recursive) {
			map.put(1, STR4);
			arrayList.add(STR1);
			if (recursive) {
				objLArray = new Array<Obj>(new Obj[] { new Obj(false), null });
			} else {
				objLArray = null;
			}
		}

		public Obj() {
			this(true);
		}
	}

	@Test
	public void testBasicCases() {
		escapeUnescape(null);
		escapeUnescape(1);
		escapeUnescape(1.0F);
		escapeUnescape(true);
		escapeUnescape("Str");
	}

	private void escapeUnescape(Object o) {
		JsonEscapeUtils.escapeObject(o);
		JsonEscapeUtils.unescapeObject(o);
	}

	@Test
	public void testObject() {
		Obj o = new Obj();
		JsonEscapeUtils.escapeObject(o);
		String eSTR1 = JsonEscapeUtils.escapeJsonString(STR1);
		String eSTR2 = JsonEscapeUtils.escapeJsonString(STR2);
		String eSTR3 = JsonEscapeUtils.escapeJsonString(STR3);
		String eSTR4 = JsonEscapeUtils.escapeJsonString(STR4);
		checkObj(o, eSTR1, eSTR2, eSTR3, eSTR4, true);
		JsonEscapeUtils.unescapeObject(o);
		checkObj(o, STR1, STR2, STR3, STR4, true);
	}

	private void checkObj(Obj o, String s1, String s2, String s3, String s4,
			boolean recursive) {
		assertEquals(s1, o.strArray[0]);
		assertEquals(s1, o.arrayList.get(0));
		assertEquals(s2, o.str);
		assertEquals(s3, o.strLArray.get(1));
		assertEquals(s4, o.map.get(1));
		if (recursive) {
			checkObj(o.objLArray.get(0), s1, s2, s3, s4, false);
			assertNull(o.objLArray.get(1));
		}
		assertNull(o.strArray[1]);
		assertNull(o.nullArray);
		assertNull(o.strLArray.get(0));
		assertFalse(o.bool);
		assertEquals(2, o.intArray[0]);
		assertEquals(3, o.intArray[1]);
	}

	@Test
	public void testStrings() {
		String eSTR1 = JsonEscapeUtils.escapeJsonString(STR1);
		String eSTR2 = JsonEscapeUtils.escapeJsonString(STR2);
		String eSTR3 = JsonEscapeUtils.escapeJsonString(STR3);
		String eSTR4 = JsonEscapeUtils.escapeJsonString(STR4);

		assertEquals("\"String\"", eSTR1);
		assertEquals("\"\\\"String\\\"\"", eSTR2);
		assertEquals(
				"\"String with spaces and strange symbols ;:= \\u00F1 \\u00E1\"",
				eSTR3);
		assertEquals("\"http:\\/\\/www.test.com\"", eSTR4);

		assertEquals(STR1, JsonEscapeUtils.unEscapeJsonString(eSTR1));
		assertEquals(STR2, JsonEscapeUtils.unEscapeJsonString(eSTR2));
		assertEquals(STR3, JsonEscapeUtils.unEscapeJsonString(eSTR3));
		assertEquals(STR4, JsonEscapeUtils.unEscapeJsonString(eSTR4));
	}
}
