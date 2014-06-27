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
package es.eucm.ead.editor.actions;

import es.eucm.ead.editor.control.actions.editor.ImportEntityResources;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schema.renderers.Image;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ImportEntityResourcesTest extends ActionTest {

	@Test
	public void test() {
		openEmpty();

		// We create an entity whose renderer doesn't point to
		// GamseStructure.IMAGES_FOLDER
		ModelEntity myElement = new ModelEntity();
		Image renderer = new Image();
		renderer.setUri("medic.png");
		myElement.getComponents().add(renderer);
		String elemResourcesFolder = controller.getEditorGameAssets()
				.absolute("src/test/resources/import_entity/").file()
				.getAbsolutePath();

		// After this action, the renderer's URI should correctly point to
		// GamseStructure.IMAGES_FOLDER.
		controller.action(ImportEntityResources.class, myElement,
				elemResourcesFolder);

		boolean success = renderer.getUri().equals("images/medic.png");
		assertTrue("The entity's resources weren't imported correctly!",
				success);

		clearEmpty();
	}
}
