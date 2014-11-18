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
package es.eucm.ead.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

import com.badlogic.gdx.scenes.scene2d.utils.Align;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.MokapController;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.editor.ChangeView;
import es.eucm.ead.editor.control.actions.editor.OpenLastProject;
import es.eucm.ead.editor.control.actions.editor.Save;
import es.eucm.ead.editor.control.actions.model.EditScene;
import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.Model.SelectionListener;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.model.events.LoadEvent;
import es.eucm.ead.editor.model.events.LoadEvent.Type;
import es.eucm.ead.editor.model.events.SelectionEvent;
import es.eucm.ead.editor.platform.Platform;
import es.eucm.ead.editor.view.builders.home.HomeView;
import es.eucm.ead.editor.view.builders.project.ProjectView;
import es.eucm.ead.editor.view.builders.scene.SceneView;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.schema.editor.components.GameData;

public class MokapApplicationListener extends EditorApplicationListener {

	public MokapApplicationListener(Platform platform) {
		super(platform);
	}

	@Override
	public void create() {
		Gdx.graphics.setContinuousRendering(false);
		super.create();
	}

	@Override
	protected void initialize() {
		super.initialize();
		controller.getModel().addLoadListener(new ModelListener<LoadEvent>() {
			@Override
			public void modelChanged(LoadEvent event) {
				if (event.getType() == Type.LOADED) {
					controller.action(
							EditScene.class,
							Q.getComponent(controller.getModel().getGame(),
									GameData.class).getInitialScene());
					controller.action(ChangeView.class, ProjectView.class);
				}
			}
		});

		controller.getModel().addSelectionListener(new SelectionListener() {
			@Override
			public boolean listenToContext(String contextId) {
				return true;
			}

			@Override
			public void modelChanged(SelectionEvent event) {
				if (event.getType() == SelectionEvent.Type.FOCUSED) {
					if (Selection.EDITED_GROUP.equals(event.getContextId())) {
						controller.action(ChangeView.class, SceneView.class);
					}
				}
			}
		});

		controller.action(OpenLastProject.class, HomeView.class);
		stage.setActionsRequestRendering(true);
	}

	@Override
	public void resize(int width, int height) {
		super.stage.getViewport().update(width, height, true);
	}

	protected Controller buildController() {
		WidgetGroup modalContainer = new WidgetGroup();
		modalContainer.setFillParent(true);

		WidgetGroup viewContainer = new WidgetGroup();
		viewContainer.setFillParent(true);

		stage.addActor(viewContainer);
		stage.addActor(modalContainer);

		MokapController controller = new MokapController(this.platform,
				Gdx.files, viewContainer, modalContainer);
		WidgetBuilder.setController(controller);
		return controller;
	}

	@Override
	public void pause() {
		((MokapController) controller).pause();
		controller.action(Save.class);
	}
}
