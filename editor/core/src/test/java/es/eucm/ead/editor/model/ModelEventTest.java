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
package es.eucm.ead.editor.model;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.Assert.*;
import static org.junit.Assert.assertArrayEquals;

/**
 * 
 * @author mfreire
 */
public class ModelEventTest {

	private ModelEvent a, b, c, d, e, f, g, h, i;
	private static final DependencyNode[] dns = new DependencyNode[10];

	@BeforeClass
	public static void setUpClass() throws Exception {
		for (int i = 0; i < dns.length; i++) {
			dns[i] = new DependencyNode("" + i, new Integer(i));
		}
	}

	@Before
	public void setUp() {
		a = new ModelEvent("A", null, null, dns[0], dns[4]);
		b = new ModelEvent("B", null, null, dns[4], dns[1]);
		c = new ModelEvent("C", null, null, dns[3], dns[0]);
		d = new ModelEvent("D", null, new DependencyNode[] { dns[0], dns[4] });
		e = new ModelEvent("E", null, new DependencyNode[] { dns[4], dns[1] });
		f = new ModelEvent("F", null, new DependencyNode[] { dns[3], dns[0] });
		g = new ModelEvent("H", new DependencyNode[] { dns[0], dns[4] }, null);
		i = new ModelEvent("I", new DependencyNode[] { dns[4], dns[1] }, null);
		h = new ModelEvent("J", new DependencyNode[] { dns[3], dns[0] }, null);
	}

	/**
	 * Test of merge method, of class ModelEvent.
	 */
	@Test
	public void testMerge() {
		System.out.println("merge");
		DependencyNode[] first = new DependencyNode[] { dns[0], dns[1], dns[4] };
		DependencyNode[] second = new DependencyNode[] { dns[0], dns[1],
				dns[3], dns[4] };

		a.merge(b);
		assertArrayEquals(a.getChanged().toArray(), first);
		a.merge(c);
		assertArrayEquals(a.getChanged().toArray(), second);
		d.merge(e);
		assertArrayEquals(d.getRemoved().toArray(), first);
		d.merge(f);
		assertArrayEquals(d.getRemoved().toArray(), second);
		g.merge(i);
		assertArrayEquals(g.getAdded().toArray(), first);
		g.merge(h);
		assertArrayEquals(g.getAdded().toArray(), second);
	}

	/**
	 * Test of changes method, of class ModelEvent.
	 */
	@Test
	public void testChangesAndContains() {
		assertTrue(a.changes(dns[0]));
		assertTrue(a.changes(dns[4]));
		assertTrue(a.changes(dns[4], dns[9]));
		assertTrue(a.changes(dns[9], dns[4]));
		assertFalse(a.changes(dns[1]));
		assertFalse(a.changes(dns[2]));
		assertFalse(a.changes(dns[3]));
		assertFalse(a.changes(dns[9]));
		// now with removed & d
		assertTrue(ModelEvent.contains(d.getRemoved(), dns[0]));
		assertTrue(ModelEvent.contains(d.getRemoved(), dns[4]));
		assertTrue(ModelEvent.contains(d.getRemoved(), dns[4], dns[9]));
		assertTrue(ModelEvent.contains(d.getRemoved(), dns[9], dns[4]));
		assertFalse(ModelEvent.contains(d.getRemoved(), dns[1]));
		assertFalse(ModelEvent.contains(d.getRemoved(), dns[2]));
		assertFalse(ModelEvent.contains(d.getRemoved(), dns[3]));
		assertFalse(ModelEvent.contains(d.getRemoved(), dns[9]));
	}
}
