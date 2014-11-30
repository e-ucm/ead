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
package es.eucm.ead.editor.view.builders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.ead.editor.assets.ApplicationAssets;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.editor.ChangeView;
import es.eucm.ead.editor.control.actions.editor.ExecuteWorker;
import es.eucm.ead.editor.control.workers.SearchRepo;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.widgets.IconButton;
import es.eucm.ead.editor.view.widgets.MultiWidget;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.editor.view.widgets.galleries.SearchGallery;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.engine.I18N;

/**
 * File view. A list with the children of a given file.
 */
public class SearchView implements ViewBuilder {

	private LinearLayout view;
	private SearchGallery searchGallery;
	private Controller controller;

	@Override
	public void initialize(Controller controller) {
		this.controller = controller;
		ApplicationAssets assets = controller.getApplicationAssets();
		Skin skin = assets.getSkin();
		I18N i18N = assets.getI18N();
		view = new LinearLayout(false);
		view.add(buildToolbar(skin, i18N)).expandX();
		view.add(
				searchGallery = new SearchGallery(
						Gdx.graphics.getHeight() / 3.15f, 4, controller))
				.expand(true, true).top();
	}

	@Override
	public Actor getView(Object... args) {
		controller.action(ExecuteWorker.class, SearchRepo.class, searchGallery,
				"");
		return view;
	}

	@Override
	public void release(Controller controller) {
	}

	private Actor buildToolbar(Skin skin, I18N i18N) {
		MultiWidget toolbar = new MultiWidget(skin, SkinConstants.STYLE_TOOLBAR);

		LinearLayout project = new LinearLayout(true);
		project.add(WidgetBuilder.toolbarIcon(SkinConstants.IC_GO, null,
				ChangeView.class, FileView.class));
		project.addSpace();
		IconButton search = WidgetBuilder.toolbarIcon(SkinConstants.IC_SEARCH,
				i18N.m("search"));
		project.add(search);

		final TextField textField = new TextField("", skin);
		textField.setVisible(false);
		textField.addListener(new InputListener() {
			@Override
			public boolean keyUp(InputEvent event, int keycode) {
				if (keycode == Keys.ENTER) {
					controller.action(ExecuteWorker.class, SearchRepo.class,
							searchGallery, textField.getText());
					textField.setVisible(false);
					textField.getStage().unfocus(textField);
					textField.invalidateHierarchy();
				}
				return false;
			}
		});

		search.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				boolean visible = !textField.isVisible();
				textField.setVisible(visible);
				textField.getStage().setKeyboardFocus(textField);
				textField.invalidateHierarchy();
			}
		});
		project.add(textField);

		toolbar.addWidgets(project);
		return toolbar;
	}
}
