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
package es.eucm.ead.editor.test.sequences;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.model.AddSceneElement;
import es.eucm.ead.editor.control.actions.model.SetSelection;
import es.eucm.ead.editor.control.actions.model.generic.AddToArray;
import es.eucm.ead.editor.control.views.SceneView;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.nogui.EditorGUITest;
import es.eucm.ead.editor.ui.perspectives.PerspectiveButtons;
import es.eucm.ead.editor.view.ui.effects.EffectWidget;
import es.eucm.ead.editor.view.ui.effects.EffectsWidget;
import es.eucm.ead.editor.view.widgets.options.ParameterOption;
import es.eucm.ead.schema.components.behaviors.Behavior;
import es.eucm.ead.schema.components.behaviors.events.Init;
import es.eucm.ead.schema.effects.GoScene;
import es.eucm.ead.schema.entities.ModelEntity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Test the process of opening a game, adding an scene element, add a behavior
 * and add an effect to the behavior
 */
public class AddEffectWithExpressionEditorGUITest extends EditorGUITest {

	protected void runTest() {
		// TODO Commented test
		/*
		 * openEmptyGame(); click(PerspectiveButtons.SCENE_SELECTOR);
		 * click(PerspectiveButtons.SCENE_SELECTOR + "0");
		 * assertEquals(SceneView.class, controller.getViews().getCurrentView()
		 * .getClass());
		 * 
		 * ModelEntity sceneElement = new ModelEntity();
		 * controller.action(AddSceneElement.class, sceneElement);
		 * 
		 * assertSame(selection.getSingle(Selection.SCENE_ELEMENT),
		 * sceneElement);
		 * 
		 * click(Init.class.getSimpleName().toLowerCase());
		 * 
		 * Behavior behavior = Q.getObjectOfClass(sceneElement.getComponents(),
		 * Behavior.class); assertNotNull(behavior);
		 * assertSame(selection.getSingle(Selection.BEHAVIOR), behavior);
		 * 
		 * GoScene goScene = templates.createEffect(GoScene.class);
		 * controller.action(AddToArray.class, behavior, behavior.getEffects(),
		 * goScene); String effectWidgetId = EffectsWidget.EFFECTS_NAME + "0";
		 * click(effectWidgetId, EffectWidget.EDIT_BUTTON);
		 * click(effectWidgetId, ParameterOption.PARAMETER_BUTTON);
		 * 
		 * Button button = (Button) getActor(effectWidgetId,
		 * ParameterOption.PARAMETER_BUTTON); assertTrue(button.isChecked());
		 * assertEquals(1, goScene.getParameters().size);
		 * 
		 * click("undo"); assertEquals(0, goScene.getParameters().size);
		 * assertFalse(button.isChecked()); click("redo");
		 * assertTrue(button.isChecked()); assertEquals(1,
		 * goScene.getParameters().size);
		 * 
		 * controller.action(SetSelection.class, Selection.EDITED_GROUP,
		 * Selection.SCENE_ELEMENT); assertEquals(0,
		 * selection.getCurrent().length); click("undo");
		 * assertSame(selection.getSingle(Selection.SCENE_ELEMENT),
		 * sceneElement); assertSame(selection.getSingle(Selection.BEHAVIOR),
		 * behavior); assertEquals(0, goScene.getParameters().size);
		 * assertFalse(button.isChecked());
		 */
	}
}
