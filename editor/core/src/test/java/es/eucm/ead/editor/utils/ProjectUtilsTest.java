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
package es.eucm.ead.editor.utils;

import com.badlogic.gdx.utils.Array;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by jtorrente on 26/11/14.
 */
public class ProjectUtilsTest {

	private static Array<String> URIS = new Array<String>(new String[] {
			"images/image1.png", "images/image2.JPEG", "videos/video1.mpg",
			"video2.avi", "sounds/sound1.mp3", "sounds/sound2.WAV",
			"icons/icon1.bmp", "icons/icon2.jpg" });

	@Test
	/**
	 * Tests {@link es.eucm.ead.editor.utils.ProjectUtils#listRefBinaries(Object)}
	 */
	public void testBinaryReferencesSearch() {
		// Create a simple object
		BinRefContainer binRefContainer = new BinRefContainer();
		Array<String> binaryReferences = ProjectUtils
				.listRefBinaries(binRefContainer);
		for (int i = 0; i < URIS.size; i++) {
			String current = URIS.get(i);
			for (int j = 0; j < binaryReferences.size; j++) {
				if (current.toLowerCase().equals(
						binaryReferences.get(j).toLowerCase())) {
					URIS.removeIndex(i);
					binaryReferences.removeIndex(j);
					i--;
					j--;
				}
			}
		}
		assertEquals(0, URIS.size);
		assertEquals(0, binaryReferences.size);
	}

	private static class BinRefContainer extends RefContainer {
		private float aFloat = 0.3F;
		private String uriRef2 = URIS.get(1).toLowerCase();
		private String notAnUri = "A test";
		private String aNull = null;

		private Map<String, String> uriMap = new HashMap<String, String>();
		private Array<SimpleRefHolder> array = new Array<SimpleRefHolder>();
		private List<SimpleRefHolder> list = new ArrayList<SimpleRefHolder>();
		private SimpleRefHolder refHolder;
		private Object anotherNull = null;

		public BinRefContainer() {
			refHolder = new SimpleRefHolder(URIS.get(2));
			list.add(new SimpleRefHolder(URIS.get(3)));
			list.add(new SimpleRefHolder(URIS.get(4)));
			array.add(new SimpleRefHolder(URIS.get(5)));
			array.add(new SimpleRefHolder(URIS.get(0)));
			uriMap.put("uri1", URIS.get(6));
			uriMap.put("uri2", URIS.get(7));
		}
	}

	private static class RefContainer {
		private String uriRef1 = URIS.get(0);
	}

	private static class SimpleRefHolder {
		private String uri;
		private String notUri = "A test2";

		public SimpleRefHolder(String uri) {
			this.uri = uri;
		}
	}
}
