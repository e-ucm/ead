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
package es.eucm.ead.editor.editorui.widgets;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.model.SetSelection;
import es.eucm.ead.editor.editorui.MockupUITest;
import es.eucm.ead.editor.editorui.OpenMockGame;
import es.eucm.ead.editor.editorui.OpenMockGame.Game;
import es.eucm.ead.editor.view.widgets.editionview.LeftEditionToolbar;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.editor.components.Documentation;
import es.eucm.ead.schema.editor.components.Parent;
import es.eucm.ead.schema.editor.components.VariableDef;
import es.eucm.ead.schema.editor.components.VariableDef.Type;
import es.eucm.ead.schema.editor.components.Variables;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.entities.ResourceCategory;

public class LeftToolbarPanelsTest extends MockupUITest {

	public static final String SCENE0 = ResourceCategory.SCENE
			.getCategoryPrefix()
			+ ResourceCategory.SCENE.getNamePrefix()
			+ "0.json";

	@Override
	protected Actor builUI(Skin skin, I18N i18n) {
		Container container = new Container();
		container.setFillParent(true);

		Variables variables = new Variables();
		VariableDef var1 = new VariableDef();
		var1.setInitialValue("btrue");
		var1.setName("first");
		var1.setType(Type.BOOLEAN);

		VariableDef var2 = new VariableDef();
		var2.setInitialValue("btrue");
		var2.setName("second");
		var2.setType(Type.BOOLEAN);

		variables.getVariablesDefinitions().add(var1);
		variables.getVariablesDefinitions().add(var2);

		Game game = new Game();
		game.setGame(new ModelEntity());

		ModelEntity scene = new ModelEntity();
		ModelEntity scene2 = new ModelEntity();
		ModelEntity scene3 = new ModelEntity();

		ModelEntity sceneElement = new ModelEntity();
		scene.getChildren().add(sceneElement);
		Parent parent = new Parent();
		parent.setParent(scene);
		sceneElement.getComponents().add(parent);

		game.addScene("scenes/scene1.json", scene);
		Documentation d1 = new Documentation();
		d1.setName("Escena1");
		scene.getComponents().add(d1);

		game.addScene("scenes/scene2.json", scene2);
		Documentation d2 = new Documentation();
		d2.setName("Escena2");
		scene2.getComponents().add(d2);

		game.addScene("scenes/scene3.json", scene3);
		Documentation d3 = new Documentation();
		d3.setName("Escena3");
		scene3.getComponents().add(d3);

		controller.action(OpenMockGame.class, game);

		controller.getModel().getGame().getComponents().add(variables);
		controller.action(SetSelection.class, Selection.SCENE,
				Selection.SCENE_ELEMENT, sceneElement);

		LeftEditionToolbar widget = new LeftEditionToolbar(controller,
				"white_left", 80, 70, 20);

		container.setWidget(widget);

		return container;
	}

	public static void main(String[] args) {
		new LwjglApplication(new LeftToolbarPanelsTest(), "TEXT", 700, 700);
	}

}