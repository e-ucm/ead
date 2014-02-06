/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2013 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          C Profesor Jose Garcia Santesmases sn,
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
package es.eucm.ead.editor.view.widgets.engine.wrappers;

import com.badlogic.gdx.scenes.scene2d.Actor;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Model.FieldListener;
import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.events.FieldEvent;
import es.eucm.ead.editor.model.events.ListEvent;
import es.eucm.ead.editor.model.events.LoadEvent;
import es.eucm.ead.engine.Assets;
import es.eucm.ead.engine.SceneView;
import es.eucm.ead.schema.actors.SceneElement;

import java.util.List;

public class EditorSceneView extends SceneView implements
		ModelListener<ListEvent> {

	private float cameraWidth;

	private float cameraHeight;

	private Model model;

	private List<SceneElement> children;

	public EditorSceneView(Model model, Assets assets) {
		super(assets);
		this.model = model;
		this.model.addLoadListener(new ModelListener<LoadEvent>() {
			@Override
			public void modelChanged(LoadEvent event) {
				addProjectListener();
				addChildrenListener();
			}
		});

	}

	private void addProjectListener() {
		model.addFieldListener(model.getProject(), new FieldListener() {
			@Override
			public void modelChanged(FieldEvent event) {
				addChildrenListener();
			}

			@Override
			public boolean listenToField(String fieldName) {
				return "editScene".equals(fieldName);
			}
		});
	}

	private void addChildrenListener() {
		if (children != null) {
			model.removeListener(children, this);
		}
		children = model.getEditScene().getChildren();
		model.addListListener(children, EditorSceneView.this);
	}

	public void setCameraSize(float width, float height) {
		this.cameraWidth = width;
		this.cameraHeight = height;
	}

	@Override
	public float getPrefWidth() {
		return cameraWidth;
	}

	@Override
	public float getPrefHeight() {
		return cameraHeight;
	}

	@Override
	public void modelChanged(ListEvent event) {
		switch (event.getType()) {
		case ADDED:
			getCurrentScene().addActor((SceneElement) event.getElement());
			break;
		case REMOVED:
			Actor actor = getCurrentScene().getSceneElement(
					(SceneElement) event.getElement());
			actor.remove();
			break;
		}
	}
}
