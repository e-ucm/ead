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
package es.eucm.ead.repobuilder.libs;

import es.eucm.ead.repobuilder.RepoLibraryBuilder;
import es.eucm.ead.schema.editor.components.repo.RepoCategories;
import es.eucm.ead.schema.editor.components.repo.licenses.DefaultLicenses;
import es.eucm.ead.schema.editor.components.repo.licenses.RepoLicense;

/**
 * Created by Javier Torrente on 25/09/14.
 */
public class VectorCharacters extends RepoLibraryBuilder {
	public VectorCharacters() {
		super("vectorcharacters");
	}

	@Override
	protected void doBuild() {
		setCommonProperty(AUTO_IDS, "true");
		setCommonProperty(LIB_NAME, "vectorcharacters-monsters");
		setCommonProperty(PUBLISHER, "vectorcharacters");
		setCommonProperty(CATEGORIES,
				RepoCategories.ELEMENTS_CHARACTERS.toString());

		setCommonProperty(MAX_WIDTH, "650");
		setCommonProperty(MAX_HEIGHT, "650");
		setCommonProperty(TAGS,
				"VectorCharacters,animated;animado,characters;personajes,cartoon");
		setCommonProperty(AUTHOR_NAME, "VectorCharacters");
		setCommonProperty(AUTHOR_URL, "http://vectorcharacters.net/");
		setCommonProperty(LICENSE,
				DefaultLicenses.License.LINK_AUTHOR.toString());

		float d1 = 0.125F;

		// Blue monster
		repoEntity("Funky monster", "Monstruo azul", "", "", "blue.png", null)
				.tag("monster", "monstruo").tag("ghost", "fantasma")
				.tag("sing", "cantar").tag("dance", "bailar")
				.tag("music", "música");
		for (int i = 0; i < 4; i++) {
			frame("blue_01.png", d1).frame("blue_04.png", d1)
					.frame("blue_02.png", d1).frame("blue_03.png", d1);
		}
		frame("blue_06.png", d1).frame("blue_05.png", d1)
				.frame("blue_06.png", d1).frame("blue_08.png", d1)
				.frame("blue_07.png", d1).frame("blue_08.png", d1);
		adjustEntity(getLastEntity());

		// Evil monster
		repoEntity("Evil monster", "Monstruo malvado", "", "", "evil.png", null)
				.tag("monster", "monstruo").tag("blink", "parpadear");
		float blink = 0.12F;
		float normal = 4;
		frame("evil_01.png", normal).frame("evil_02.png", blink)
				.frame("evil_01.png", normal).frame("evil_02.png", blink)
				.frame("evil_01.png", normal).frame("evil_02.png", blink)
				.frame("evil_03.png", normal).frame("evil_02.png", blink)
				.frame("evil_03.png", normal).frame("evil_02.png", blink)
				.adjustEntity(getLastEntity());

		// Red monster
		repoEntity("Red monster", "Monstruo rojo", "", "", "red.png", null)
				.tag("monster", "monstruo");
		frame("red_01.png", d1).frame("red_02.png", d1).frame("red_01.png", d1)
				.frame("red_03.png", d1).frame("red_04.png", d1)
				.frame("red_03.png", d1).frame("red_01.png", d1)
				.frame("red_05.png", d1).frame("red_06.png", d1)
				.frame("red_05.png", d1).frame("red_01.png", d1)
				.adjustEntity(getLastEntity());

		repoLib("Monsters from VectorCharacters.net",
				"Monstruos de VectorCharacters.net",
				"Cartoon characters from VectorCharacters, animated by mokap team",
				"Personajes cartoon de VectorCharacters, animados por el equipo de mokap",
				"vectorcharacters.png");

	}

}
