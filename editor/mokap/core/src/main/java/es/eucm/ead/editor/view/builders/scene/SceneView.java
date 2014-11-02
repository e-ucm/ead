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

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.editor.Redo;
import es.eucm.ead.editor.control.actions.editor.Undo;
import es.eucm.ead.editor.control.actions.model.SetSelection;
import es.eucm.ead.editor.model.Model.SelectionListener;
import es.eucm.ead.editor.model.events.SelectionEvent;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.builders.ViewBuilder;
import es.eucm.ead.editor.view.widgets.ContextMenu;
import es.eucm.ead.editor.view.widgets.IconButton;
import es.eucm.ead.editor.view.widgets.MultiToolbar;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.editor.view.widgets.baseview.BaseView;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.engine.I18N;

public class SceneView implements ViewBuilder {

	private BaseView view;

	private SceneEditor sceneEditor;

	private Controller controller;

	@Override
	public void initialize(Controller controller) {
		this.controller = controller;
		Skin skin = controller.getApplicationAssets().getSkin();
		I18N i18N = controller.getApplicationAssets().getI18N();

		view = new BaseView(skin);

		view.setToolbar(buildToolbar(skin, i18N));
		view.setNavigation(buildNavigation(skin, i18N));

		view.setContent(sceneEditor = new SceneEditor(controller));
	}

	@Override
	public Actor getView(Object... args) {
		sceneEditor.prepare();
		return view;
	}

	@Override
	public void release(Controller controller) {

	}

	private MultiToolbar buildToolbar(Skin skin, I18N i18N) {
		final MultiToolbar toolbar = new MultiToolbar(skin);
		toolbar.addToolbars(buildComposeToolbar(skin, i18N),
				buildTransformToolbar(skin, i18N));

		controller.getModel().addSelectionListener(new SelectionListener() {
			@Override
			public boolean listenToContext(String contextId) {
				return Selection.SCENE_ELEMENT.equals(contextId);
			}

			@Override
			public void modelChanged(SelectionEvent event) {
				if (event.getSelection().length == 0) {
					toolbar.setSelectedToolbar(0);
				} else {
					toolbar.setSelectedToolbar(1);
				}
			}
		});
		return toolbar;
	}

	private LinearLayout buildComposeToolbar(Skin skin, I18N i18N) {
		LinearLayout compose = new LinearLayout(true);
		IconButton navigation = WidgetBuilder.toolbarIcon(skin,
				SkinConstants.IC_MENU);
		navigation.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				view.toggleNavigation();
			}
		});
		compose.add(navigation);

		IconButton mode = WidgetBuilder.icon(skin, SkinConstants.IC_COMPOSE,
				SkinConstants.STYLE_DROP_DOWN);
		WidgetBuilder.launchContextMenu(mode, buildModeContextMenu(skin, i18N));

		compose.add(mode);

		compose.addSpace();

		compose.add(WidgetBuilder.toolbarIconWithMenu(skin,
				SkinConstants.IC_ADD, buildInsertContextMenu(skin, i18N)));
		return compose;
	}

	private LinearLayout buildTransformToolbar(Skin skin, I18N i18N) {
		LinearLayout transform = new LinearLayout(true);
		transform.add(WidgetBuilder.toolbarIcon(skin, SkinConstants.IC_CHECK,
				SetSelection.class, Selection.EDITED_GROUP,
				Selection.SCENE_ELEMENT));
		transform.add(WidgetBuilder.toolbarIcon(skin, SkinConstants.IC_UNDO,
				Undo.class));
		transform.add(WidgetBuilder.toolbarIcon(skin, SkinConstants.IC_REDO,
				Redo.class));
		return transform;
	}

	private ContextMenu buildInsertContextMenu(Skin skin, I18N i18n) {
		ContextMenu contextMenu = WidgetBuilder.iconLabelContextPanel(skin,
				SkinConstants.IC_CLOUD, i18n.m("gallery"),
				SkinConstants.IC_CAMERA, i18n.m("picture"),
				SkinConstants.IC_BRUSH, i18n.m("drawing"),
				SkinConstants.IC_TEXT, i18n.m("text"), SkinConstants.IC_ZONE,
				i18n.m("interactive.zone"));
		contextMenu.pack();
		contextMenu.setOriginX(contextMenu.getWidth());
		contextMenu.setOriginY(contextMenu.getHeight());
		return contextMenu;
	}

	private Actor buildModeContextMenu(Skin skin, I18N i18N) {
		ContextMenu contextMenu = WidgetBuilder.iconLabelContextPanel(skin,
				SkinConstants.IC_COMPOSE, i18N.m("compose"),
				SkinConstants.IC_FX, i18N.m("fx"), SkinConstants.IC_PLAY,
				i18N.m("test"));
		contextMenu.pack();
		contextMenu.setOriginY(contextMenu.getHeight());
		return contextMenu;
	}

	private LinearLayout buildNavigation(Skin skin, I18N i18N) {
		LinearLayout navigation = new LinearLayout(false,
				skin.getDrawable(SkinConstants.DRAWABLE_PAGE_LEFT));
		navigation.add(WidgetBuilder.button(skin, SkinConstants.IC_HOME,
				i18N.m("project"), SkinConstants.STYLE_CONTEXT));
		navigation.addSpace();
		return navigation;
	}
}
