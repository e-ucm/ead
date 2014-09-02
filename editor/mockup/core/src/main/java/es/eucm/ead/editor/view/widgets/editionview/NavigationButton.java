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
package es.eucm.ead.editor.view.widgets.editionview;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.editor.ChangeView;
import es.eucm.ead.editor.control.actions.model.EditScene;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Model.SelectionListener;
import es.eucm.ead.editor.model.events.SelectionEvent;
import es.eucm.ead.editor.model.events.SelectionEvent.Type;
import es.eucm.ead.editor.view.builders.gallery.ScenesView;
import es.eucm.ead.editor.view.widgets.IconButton;
import es.eucm.ead.editor.view.widgets.iconwithpanel.IconWithScalePanel;
import es.eucm.ead.engine.I18N;

public class NavigationButton extends IconWithScalePanel implements
		SelectionListener {

	private static final float SPACE = -10, PAD = 30;

	private float height;

	private float width;

	private ScenesTableList sceneList;

	private Model model;

	public NavigationButton(Skin skin, Controller controller, float size) {
		this(skin, controller, size, size);
	}

	public NavigationButton(Skin skin, final Controller controller,
			float height, float width) {
		super("menu", SPACE, skin);
		setStyle(skin.get("white_union", IconButtonStyle.class));

		this.model = controller.getModel();

		I18N i18n = controller.getApplicationAssets().getI18N();

		this.height = height;
		this.width = width;

		InputListener changeView = new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				SceneButton button = (SceneButton) event.getListenerActor();
				controller.action(EditScene.class, controller.getModel()
						.getIdFor(button.getScene()));
			}

		};

		sceneList = new ScenesTableList(controller, changeView, "scene");

		ScrollPane list = new ScrollPane(sceneList);
		list.setScrollingDisabled(true, false);

		IconButton goGallery = new IconButton("home", skin);
		goGallery.add(i18n.m("general.scenes")).padLeft(PAD);
		goGallery.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				controller.action(ChangeView.class, ScenesView.class);
			}
		});

		panel.align(Align.top);
		panel.add(goGallery).pad(PAD, PAD, PAD, PAD).top();
		panel.row();
		panel.add(list).top();

		controller.getModel().addSelectionListener(this);

	}

	@Override
	protected void showPanel() {
		sceneList.selectScene(model.getIdFor(model.getSelection().getSingle(
				Selection.SCENE)));
		super.showPanel();
	}

	@Override
	public float getPrefHeight() {
		return height;
	}

	@Override
	public float getPrefWidth() {
		return width;
	}

	@Override
	public void modelChanged(SelectionEvent event) {
		if (event.getType() == Type.FOCUSED || event.getType() == Type.REMOVED) {
			sceneList.selectScene(model.getIdFor(model.getSelection()
					.getSingle(Selection.SCENE)));
		}
	}

	@Override
	public boolean listenToContext(String contextId) {
		return contextId.equals(Selection.EDITED_GROUP);
	}
}
