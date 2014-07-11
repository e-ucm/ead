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
package es.eucm.ead.editor.search;

import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.commands.FieldCommand;
import es.eucm.ead.editor.control.commands.ListCommand.AddToListCommand;
import es.eucm.ead.editor.control.commands.ListCommand.RemoveFromListCommand;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.events.LoadEvent;
import es.eucm.ead.editor.model.events.LoadEvent.Type;
import es.eucm.ead.editor.platform.MockPlatform;
import es.eucm.ead.editor.indexes.Index.Match;
import es.eucm.ead.engine.mock.MockApplication;
import es.eucm.ead.engine.mock.MockFiles;
import es.eucm.ead.schema.components.ModelComponent;
import es.eucm.ead.schema.components.Tags;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schema.renderers.Image;
import es.eucm.ead.schemax.FieldName;
import es.eucm.ead.schemax.entities.ResourceCategory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class IndexTest {

	private Controller controller;

	private Model model;

	@BeforeClass
	public static void setUpClass() {
		MockApplication.initStatics();
	}

	@Before
	public void setUp() {
		this.controller = new Controller(new MockPlatform(), new MockFiles(),
				null, null);
		this.model = controller.getModel();
	}

	@Test
	public void testSearchSimpleModel() {
		ModelEntity scene = new ModelEntity();
		ModelEntity sceneElement = new ModelEntity();
		Image image = new Image();
		image.setUri("images/someimage.png");

		sceneElement.getComponents().add(image);
		scene.getChildren().add(sceneElement);

		model.getIndex().setFuzzyFactor(0.1f);

		model.putResource("scene", ResourceCategory.SCENE, scene);

		model.notify(new LoadEvent(Type.LOADED, model));
		assertTrue(matchesContainObject(model.search("images/someimage.png"),
				image));
		assertTrue(matchesContainObject(model.search("images"), image));

		ModelEntity sceneElement2 = new ModelEntity();
		Tags tags = new Tags();
		tags.getTags().add("tag");
		sceneElement2.getComponents().add(tags);

		controller.getCommands()
				.command(
						new AddToListCommand(scene, scene.getChildren(),
								sceneElement2));

		assertTrue(matchesContainObject(model.search("tag"), image));
		assertTrue(matchesContainObject(model.search("tg"), image));
	}

	@Test
	public void testMassiveModel() {
		// Create 1000 scene elements
		ModelEntity scene = new ModelEntity();
		for (int i = 0; i < 10; i++) {
			ModelEntity child1 = new ModelEntity();
			child1.getComponents().add(new ComponentWithString(i + ""));
			scene.getChildren().add(child1);
			for (int j = 0; j < 10; j++) {
				ModelEntity child2 = new ModelEntity();
				child2.getComponents()
						.add(new ComponentWithString(i + "#" + j));
				child1.getChildren().add(child2);
				for (int k = 0; k < 10; k++) {
					ModelEntity child3 = new ModelEntity();
					child3.getComponents().add(
							new ComponentWithString(i + "#" + j + "#" + k));
					child2.getChildren().add(child3);
				}
			}
		}
		model.putResource("scene", ResourceCategory.SCENE, scene);
		model.notify(new LoadEvent(Type.LOADED, model));

		model.getIndex().setMaxSearchHits(1000);
		model.getIndex().setFuzzyFactor(0.99f);

		assertEquals(1000, model.search("abcdefghijk").size);
		assertEquals(1000, model.search("11").size);
		for (int i = 0; i < 10; i++)
			for (int j = 0; j < 10; j++)
				for (int k = 0; k < 10; k++) {
					String name = i + "#" + j + "#" + k;
					Array<Match> matches = model.search(name);
					assertEquals(1, matches.size);
					ComponentWithString componentWithString = (ComponentWithString) matches
							.first().getObject();
					assertEquals(name, componentWithString.name);
				}
	}

	@Test
	public void testIndexUpdatesWhenAddAndRemoveFromList() {
		ModelEntity scene = new ModelEntity();
		model.putResource("scene", ResourceCategory.SCENE, scene);
		model.notify(new LoadEvent(Type.LOADED, model));

		ModelEntity sceneElement = new ModelEntity();
		ComponentWithString componentWithString = new ComponentWithString("ñor");
		sceneElement.getComponents().add(componentWithString);

		assertEquals(0, model.search("ñor").size);
		controller.getCommands().command(
				new AddToListCommand(scene, scene.getChildren(), sceneElement));
		matchesContainObject(model.search("ñor"), componentWithString);
		controller.getCommands().command(
				new RemoveFromListCommand(scene, scene.getChildren(),
						sceneElement));
		assertEquals(0, model.search("ñor").size);
	}

	@Test
	public void testIndexUpdatesWhenFieldChanges() {
		ModelEntity scene = new ModelEntity();
		model.putResource("scene", ResourceCategory.SCENE, scene);
		ComponentWithString componentWithString = new ComponentWithString("ñor");
		scene.getComponents().add(componentWithString);

		model.notify(new LoadEvent(Type.LOADED, model));

		model.getIndex().setFuzzyFactor(0.99f);
		assertEquals(1, model.search("ñor").size);

		controller.getCommands().command(
				new FieldCommand(componentWithString, FieldName.NAME, "ngd"));

		assertEquals(0, model.search("ñor").size);
		assertEquals(1, model.search("ngd").size);
	}

	public boolean matchesContainObject(Array<Match> matches, Object object) {
		for (Match match : matches) {
			if (match.getObject().equals(object)) {
				return true;
			}
		}
		return false;
	}

	public static class ComponentWithString extends ModelComponent {
		private String name;
		private String stringField1 = "11";
		private String stringField2 = "22";
		private String stringField3 = "33";
		private String stringField4 = "44";
		private String stringField5 = "55";
		private String stringField6 = "66";
		private String stringField7 = "77";
		private String stringField8 = "88";
		private String stringField9 = "99";
		private String stringField10 = "00";
		private String stringField11 = "abcdefghijk";
		private String stringField12 = "BB";
		private String stringField13 = "CC";
		private String stringField14 = "DD";
		private String stringField15 = "EE";
		private String stringField16 = "FF";
		private String stringField17 = "GG";
		private String stringField18 = "HH";
		private String stringField19 = "II";
		private String stringField20 = "JJ";

		public ComponentWithString(String name) {
			this.name = name;
		}
	}
}
