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
package es.eucm.ead.editor.view.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.AddScene;
import es.eucm.ead.editor.control.actions.EditScene;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.events.LoadEvent;
import es.eucm.ead.editor.model.events.MapEvent;
import es.eucm.ead.editor.view.listeners.ActionOnClickListener;
import es.eucm.ead.engine.I18N;

import java.util.HashMap;
import java.util.Map;

public class ScenesList extends AbstractWidget {

	private Controller controller;

	private I18N i18N;

	private Skin skin;

	private Map<String, Actor> buttons;

	private Model model;

	public ScenesList(Controller c) {
		this.controller = c;
		this.i18N = controller.getEditorAssets().getI18N();
		this.skin = controller.getEditorAssets().getSkin();
		this.buttons = new HashMap<String, Actor>();

		model = c.getModel();
		model.addLoadListener(new ModelListener<LoadEvent>() {
			@Override
			public void modelChanged(LoadEvent event) {
				addNewSceneButton();
				for (String scene : event.getModel().getScenes().keySet()) {
					addEditButton(scene);
				}
				model.addMapListener(model.getScenes(),
						new ModelListener<MapEvent>() {

							@Override
							public void modelChanged(MapEvent event) {
								switch (event.getType()) {
								case ENTRY_ADDED:
									addEditButton(event.getKey().toString());
									break;
								case ENTRY_REMOVED:
									Actor button = buttons.remove(event
											.getKey().toString());
									button.remove();
									break;
								}
							}
						});
			}
		});
	}

	private void addNewSceneButton() {
		TextButton addButton = new TextButton(i18N.m("scene.add"), skin);
		addButton.addListener(new ActionOnClickListener(controller,
				AddScene.NAME));
		addActor(addButton);
	}

	private void addEditButton(String scene) {
		TextButton textButton = new TextButton(i18N.m("scene.edit", scene),
				skin);
		textButton.addListener(new ActionOnClickListener(controller,
				EditScene.NAME, scene));
		addActor(textButton);
		buttons.put(scene, textButton);
	}

	@Override
	public void layout() {
		float y = 0;
		float size = this.getWidth();
		for (Actor a : getChildren()) {
			a.setBounds(0, y, size, size);
			y += size;
		}
	}

	@Override
	public float getPrefWidth() {
		return Gdx.graphics.getWidth() / 7;
	}

	@Override
	public float getPrefHeight() {
		return Gdx.graphics.getHeight();
	}
}
