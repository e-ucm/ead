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
package es.eucm.ead.editor.editorui.perspectives;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import es.eucm.ead.editor.editorui.EditorUITest;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Model.FieldListener;
import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.events.FieldEvent;
import es.eucm.ead.editor.model.events.LoadEvent;
import es.eucm.ead.editor.ui.perspectives.PerspectiveButtons;
import es.eucm.ead.editor.view.widgets.PlaceHolder;
import es.eucm.ead.schema.editor.components.EditState;
import es.eucm.ead.schemax.FieldName;

/**
 * Created by angel on 22/05/14.
 */
public class PerspectiveButtonsTest extends EditorUITest implements
		FieldListener {
	@Override
	protected void builUI(Group root) {
		Skin skin = controller.getApplicationAssets().getSkin();
		PlaceHolder placeHolder = new PlaceHolder();

		PerspectiveButtons perspectiveButtons = new PerspectiveButtons(
				controller);
		placeHolder.setContent(perspectiveButtons);
		placeHolder.setFillParent(true);

		perspectiveButtons.background(skin.getDrawable("blank"));

		root.addActor(placeHolder);

		controller.getModel().addLoadListener(new ModelListener<LoadEvent>() {
			@Override
			public void modelChanged(LoadEvent event) {

				switch (event.getType()) {
				case LOADED:
					addEditSceneListener();
					break;
				}
			}
		});
	}

	private void addEditSceneListener() {
		EditState editState = Model.getComponent(controller.getModel()
				.getGame(), EditState.class);
		controller.getModel().removeListenerFromAllTargets(this);
		controller.getModel().addFieldListener(editState, this);
	}

	public static void main(String[] args) {
		new LwjglApplication(new PerspectiveButtonsTest(),
				"Perspective buttons test", 1000, 500);
	}

	@Override
	public boolean listenToField(FieldName fieldName) {
		return fieldName == FieldName.EDIT_SCENE;
	}

	@Override
	public void modelChanged(FieldEvent event) {
		Gdx.app.log("PerspectiveButtonsTest",
				"Scene edited updated " + event.getValue());
	}
}
