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
package es.eucm.ead.editor.view.builders.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import es.eucm.ead.editor.control.actions.editor.ChangeView;
import es.eucm.ead.editor.control.actions.model.scene.NewScene;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.builders.project.ProjectView;
import es.eucm.ead.editor.view.widgets.AbstractWidget;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.engine.I18N;

public class Navigation extends AbstractWidget {

	private static final float ADD_SCENE_SEPARATION_DP = 16F;
	private static final float PREF_WIDTH = .3F;

	private Button addScene;

	private Table scenes;

	public Navigation(Skin skin, I18N i18N) {
		ScrollPane scrollPane;
		addActor(scrollPane = new ScrollPane(scenes = new Table(), skin.get(
				SkinConstants.STYLE_NAVIGATION, ScrollPaneStyle.class)));
		scrollPane.setFillParent(true);
		addActor(addScene = WidgetBuilder.button(skin, SkinConstants.STYLE_ADD,
				NewScene.class, ""));
		scenes.add(WidgetBuilder.button(skin, SkinConstants.IC_HOME,
				i18N.m("project"), SkinConstants.STYLE_CONTEXT,
				ChangeView.class, ProjectView.class));
		scenes.add(WidgetBuilder.button(skin, SkinConstants.IC_PLAY,
				i18N.m("test.all"), SkinConstants.STYLE_CONTEXT));
	}

	public Table getScenes() {
		return scenes;
	}

	@Override
	public float getPrefWidth() {
		return Math.max(scenes.getPrefWidth(), Gdx.graphics.getWidth()
				* PREF_WIDTH);
	}

	@Override
	public float getPrefHeight() {
		return Gdx.graphics.getHeight();
	}

	@Override
	public void layout() {
		float width = getPrefWidth(addScene);
		float dpToPixels = WidgetBuilder.dpToPixels(ADD_SCENE_SEPARATION_DP);
		setBounds(addScene, getWidth() - width - dpToPixels, dpToPixels, width,
				getPrefHeight(addScene));
	}

}
