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
package es.eucm.ead.buildtools;

import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ReformatPomsTest {

	@Test
	public void testCountIndents() throws Exception {
		assertEquals("counting indents", 1, ReformatPoms.countIndents("\t"));
		assertEquals("counting indents", 2, ReformatPoms.countIndents("\t\t"));
		assertEquals("counting indents", 3, ReformatPoms.countIndents("\t\t\t"));
		assertEquals("counting indents", 5,
				ReformatPoms.countIndents("\t\t\t\t\t"));
	}

	@Test
	public void testIndentLine() throws Exception {
		assertEquals("indenting line", "\t123",
				ReformatPoms.indentLine("123", 1));
		assertEquals("indenting line", "\t\t\t123",
				ReformatPoms.indentLine("123", 3));
	}

	/**
	 * reads a single less-than-1 MB file into a string.
	 */
	private static String read(String fileName) {
		try {
			InputStream is = ReformatPomsTest.class.getClassLoader()
					.getResourceAsStream("xml-prettify/" + fileName + ".xml");
			byte[] buffer = new byte[1024 * 1024];
			int length = is.read(buffer);
			is.close();
			return new String(buffer, 0, length, "UTF-8").replaceAll(
					ReformatPoms.expandedTab, "\t");
		} catch (Exception e) {
			System.err.println("could not read " + fileName + ": "
					+ e.getMessage());
			e.printStackTrace();
			fail("could not read " + fileName + ": " + e.getMessage());
			return null;
		}
	}

	@Test
	public void testPostProcess() throws Exception {
		assertEquals("postprocess: long xml entity", read("longEntity2"),
				ReformatPoms.postProcess(read("longEntity")));
		assertEquals("postprocess: long xml comment", read("longComment2"),
				ReformatPoms.postProcess(read("longComment")));
		assertEquals("postprocess: long xml textual content",
				read("longTextual2"),
				ReformatPoms.postProcess(read("longTextual")));
	}

	@Test
	public void testRegexExpressions() throws Exception {
		String singleTagWithAttributes = "<a b=\"123\" c=\"456\">";
		String singleTagWithoutAttributes = "<abracadabra >";
		String openTextAndClose = "<abra>o cierre</abra>";
		String openTextAndOpen = "<abra>o cierre<cierre>";
		assertArrayEquals(new String[] { "<a b=\"123", "c=\"456\">" },
				singleTagWithAttributes.split("[\"] "));
		assertEquals(
				true,
				ReformatPoms.openingTagWithAttrsRegex.matcher(
						singleTagWithAttributes).matches());
		assertEquals(
				false,
				ReformatPoms.openingTagWithAttrsRegex.matcher(
						singleTagWithoutAttributes).matches());
		assertEquals(true,
				ReformatPoms.openTagTextAndCloseRegex.matcher(openTextAndClose)
						.matches());
		assertEquals(false,
				ReformatPoms.openTagTextAndCloseRegex.matcher(openTextAndOpen)
						.matches());
	}
}
