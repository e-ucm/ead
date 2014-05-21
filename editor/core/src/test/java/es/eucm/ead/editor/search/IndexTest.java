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

package es.eucm.ead.editor.search;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.reflect.Field;
import es.eucm.ead.engine.mock.MockApplication;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Comparator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * 
 * @author mfreire
 */
public class IndexTest {

	public IndexTest() {
	}

	@Before
	public void setUp() {
		MockApplication.initStatics();
	}

	@After
	public void tearDown() {
	}

	public static class T1 {
		private String one = "1";
		private String two = "2";
		private String three = "3";
		protected String four = "4";
		private int eighteen = 18;

		public void set(String one, String two, String three, String four) {
			this.one = one;
			this.two = two;
			this.three = three;
			this.four = four;
		}

		@Override
		public String toString() {
			return one + ", " + two + ", " + three + ", " + four;
		}
	}

	public static class T2 extends T1 {
		private int five = 5;
		private int six = 6;
		private String seven = "7";
	}

	public void assertFieldNamesMatch(Array<String> expected, Array<Field> found) {
		assertEquals(expected.size, found.size);
		expected.sort();
		found.sort(new Comparator<Field>() {
			@Override
			public int compare(Field o1, Field o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		for (int i = 0; i < expected.size; i++) {
			assertEquals(expected.get(i), found.get(i).getName());
		}
	}

	/**
	 * Test of getIndexedFields method, of class Index.
	 */
	@Test
	public void testGetIndexedFields() throws Exception {
		System.out.println("getIndexedFields");
		Index i = new Index();
		assertFieldNamesMatch(new Array<String>(new String[] { "one", "two",
				"three", "four" }), i.getIndexedFields(new T1()));
		assertFieldNamesMatch(new Array<String>(new String[] { "one", "two",
				"three", "four", "seven" }), i.getIndexedFields(new T2()));
	}

	/**
	 * Test of add method, of class Index.
	 */
	@Test
	public void testAdd() {
		System.out.println("add");

		Index instance = new Index();

		// add a few objects
		T1 a = new T1();
		a.set("ananas", "boleto", "cardamomo", "delicioso");
		instance.add(a, true);
		T1 b = new T1();
		b.set("ultimatum", "worcester", "xilofono", "yucatan");
		instance.add(b, true);

		// now find them
		assertTrue(instance.search("nant").getMatches().size == 0);
		assertEquals(instance.search("ana").getMatches().get(0).getObject(), a);
		assertEquals(instance.search("nan").getMatches().get(0).getObject(), a);
		assertEquals(instance.search("as").getMatches().get(0).getObject(), a);

		assertTrue(instance.search("wir").getMatches().size == 0);
		assertEquals(instance.search("orc").getMatches().get(0).getObject(), b);
		assertEquals(instance.search("lof").getMatches().get(0).getObject(), b);
		assertEquals(instance.search("tan").getMatches().get(0).getObject(), b);

		// add another b
		T1 bb = new T1();
		bb.set("ultimo", "william", "xoseba", "yoli");
		instance.add(bb, true);

		// find it
		assertEquals(instance.search("ult").getMatches().size, 2);
		assertEquals(instance.search("li").getMatches().size, 2);
	}

	/**
	 * Test of remove method, of class Index.
	 */
	@Test
	public void testRemove() {
		System.out.println("add");

		Index instance = new Index();

		// add a few objects
		T1 a = new T1();
		a.set("ananas", "boleto", "cardamomo", "delicioso");
		instance.add(a, true);
		T1 b = new T1();
		b.set("ultimatum", "worcester", "xilofono", "yucatan");
		instance.add(b, true);

		// and remove the first one
		instance.remove(a, true);

		// now fail to find them
		assertTrue(instance.search("nant").getMatches().size == 0);
		assertTrue(instance.search("ana").getMatches().size == 0);
		assertTrue(instance.search("nan").getMatches().size == 0);
		assertTrue(instance.search("as").getMatches().size == 0);

		// but find the non-removed ones
		assertTrue(instance.search("wir").getMatches().size == 0);
		assertEquals(instance.search("orc").getMatches().get(0).getObject(), b);
		assertEquals(instance.search("lof").getMatches().get(0).getObject(), b);
		assertEquals(instance.search("tan").getMatches().get(0).getObject(), b);
	}

	/**
	 * Test of refresh method, of class Index.
	 */
	@Test
	public void testRefresh() {
		System.out.println("refresh");

		Index instance = new Index();

		// add a few objects
		T1 a = new T1();
		a.set("ax", "bx", "cx", "dx");
		instance.add(a, true);
		T1 b = new T1();
		b.set("ux", "wx", "xx", "yx");
		instance.add(b, true);

		// now update their values
		a.set("aay", "bay", "cay", "day");
		instance.refresh(a);
		b.set("uay", "way", "xay", "yay");
		instance.refresh(b);

		// the old ones are gone, the new ones here to stay
		assertTrue(instance.search("bx").getMatches().size == 0);
		assertTrue(instance.search("dx").getMatches().size == 0);
		assertTrue(instance.search("wx").getMatches().size == 0);
		assertTrue(instance.search("yx").getMatches().size == 0);
		assertEquals(instance.search("bay").getMatches().get(0).getObject(), a);
		assertEquals(instance.search("day").getMatches().get(0).getObject(), a);
		assertEquals(instance.search("way").getMatches().get(0).getObject(), b);
		assertEquals(instance.search("yay").getMatches().get(0).getObject(), b);
	}
}
