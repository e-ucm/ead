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
package es.eucm.ead.editor.ui.scenes.map;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Scaling;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Model.FieldListener;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.model.events.FieldEvent;
import es.eucm.ead.editor.view.listeners.SceneNameListener;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.editor.components.GameData;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.FieldName;
import es.eucm.ead.schemax.entities.ResourceCategory;

/**
 * This widget indicates what {@link ModelEntity} scene is the initial scene.
 */
public class InitialSceneWidget extends LinearLayout implements FieldListener {

	private Image preview;
	private Label name;
	private Controller controller;
	private InitialSceneNameListener nameListener;

	public InitialSceneWidget(Controller controller) {
		super(false);
		this.controller = controller;
		preview = new Image();
		preview.setScaling(Scaling.fit);
		nameListener = new InitialSceneNameListener(controller);
		name = new Label("", controller.getApplicationAssets().getSkin());

		add(preview);
		add(name);
	}

	public void prepare() {

		Model model = controller.getModel();
		model.addFieldListener(Q.getComponent(model.getGame(), GameData.class),
				this);
		update();
	}

	public void release() {

		nameListener.remove();
		Model model = controller.getModel();
		model.removeListenerFromAllTargets(this);
	}

	private void update() {

		Model model = controller.getModel();
		String newId = Q.getComponent(model.getGame(), GameData.class)
				.getInitialScene();
		ModelEntity initialScene = (ModelEntity) model.getResource(newId,
				ResourceCategory.SCENE);

		nameListener.setUp(initialScene);

		String name = initialScene == null ? "" : Q
				.getName(initialScene, newId);
		updateName(name);
	}

	private void updateName(String name) {
		I18N i18n = controller.getApplicationAssets().getI18N();
		this.name.setText(i18n.m("general.mockup.initial-scene") + ": " + name);
	}

	@Override
	public void modelChanged(FieldEvent event) {
		update();
	}

	@Override
	public boolean listenToField(String fieldName) {
		return FieldName.INITIAL_SCENE.equals(fieldName);
	}

	/**
	 * Notifies that the name of the initial scene has changed.
	 */
	private class InitialSceneNameListener extends SceneNameListener {

		public InitialSceneNameListener(Controller controller) {
			super(controller);
		}

		@Override
		public void nameChanged(String name) {
			updateName(name);
		}

	}
}
