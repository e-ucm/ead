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
package es.eucm.ead.editor.ui.scenes.ribbon;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.Action;
import es.eucm.ead.editor.control.actions.editor.AddLabel;
import es.eucm.ead.editor.control.actions.editor.AddSceneElementFromResource;
import es.eucm.ead.editor.control.actions.model.scene.NewScene;
import es.eucm.ead.editor.view.listeners.ActionOnClickListener;
import es.eucm.ead.editor.view.widgets.IconButton;
import es.eucm.ead.editor.view.widgets.Separator;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.editor.view.widgets.scenes.SceneEditor;
import es.eucm.i18n.I18N;

/**
 * Created by angel on 22/05/14.
 */
public class InsertTab extends LinearLayout {

	private Controller controller;

	public InsertTab(Controller controller) {
		super(true);
		this.controller = controller;

		Skin skin = controller.getApplicationAssets().getSkin();
		I18N i18N = controller.getApplicationAssets().getI18N();
		add(createButton("newscene48x48", i18N.m("general.scene"), skin,
				NewScene.class, i18N.m("scene.untitled")));
		add(new Separator(false, skin));
		add(createButton("image48x48", i18N.m("general.image"), skin,
				new InsertOnClick()));
		add(createButton("shapes48x48", i18N.m("general.shape"), skin, null));
		add(createButton("text48x48", i18N.m("general.text"), skin,
				AddLabel.class));
		add(new Separator(false, skin));
		add(createButton("controls48x48", i18N.m("general.control"), skin, null));
		add(new Separator(false, skin));
		add(createButton("actor48x48", i18N.m("general.actor"), skin, null));
		add(createButton("interactivezone48x48",
				i18N.m("general.interactivezone"), skin, null));
		add(createButton("goal48x48", i18N.m("general.goal"), skin, null));
		add(new Separator(false, skin));
		add(createButton("camera48x48", i18N.m("general.camera"), skin, null));
	}

	private <T extends Action> Actor createButton(String drawable, String text,
			Skin skin, Class<T> editorAction, Object... args) {
		return createButton(drawable, text, skin, new ActionOnClickListener(
				controller, editorAction, args));
	}

	private Actor createButton(String drawable, String text, Skin skin,
			ClickListener clickListener) {
		IconButton button = new IconButton(skin.getDrawable(drawable), 5, skin);
		button.row();
		button.add(new Label(text, skin));

		if (clickListener != null) {
			button.addListener(clickListener);
		}
		return button;
	}

	public class InsertOnClick extends ClickListener {

		private Vector2 center = new Vector2();

		@Override
		public void clicked(InputEvent event, float x, float y) {
			SceneEditor sceneEditor = controller.getViews().getViewsContainer()
					.findActor(SceneEditor.NAME);
			sceneEditor.getViewPortCenter(center);
			controller.action(AddSceneElementFromResource.class, center.x,
					center.y);
		}
	}

}
