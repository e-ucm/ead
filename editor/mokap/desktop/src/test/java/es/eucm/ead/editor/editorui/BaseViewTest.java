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
package es.eucm.ead.editor.editorui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import es.eucm.ead.editor.view.widgets.baseview.BaseView;
import es.eucm.ead.editor.view.widgets.baseview.BaseView.BaseViewStyle;
import es.eucm.ead.editor.assets.ApplicationAssets;
import es.eucm.ead.editor.view.widgets.IconButton;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.engine.I18N;

public class BaseViewTest extends UITest {

	@Override
	protected Actor buildUI(Skin skin, I18N i18n) {
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 0.0f);

		ApplicationAssets assets = controller.getApplicationAssets();

		Drawable blank = assets.getSkin().getDrawable("blank");

		BaseViewStyle style = new BaseViewStyle();
		style.navigationBackground = blank;
		style.navigationBackgroundAlpha = 0.5f;
		final BaseView glassContainer = new BaseView(style);
		LinearLayout toolbar = new LinearLayout(true, blank)
				.backgroundColor(Color.RED);
		toolbar.add(new IconButton("back80x80", assets.getSkin()));

		LinearLayout navigation = new LinearLayout(false, blank);

		for (int i = 0; i < 30; i++) {
			LinearLayout wide = new LinearLayout(true);
			wide.add(new IconButton("camera250x250", assets.getSkin()));
			wide.add(new IconButton("camera250x250", assets.getSkin()));
			wide.add(new IconButton("camera250x250", assets.getSkin()));
			wide.add(new IconButton("camera250x250", assets.getSkin()));
			navigation.add(wide);
		}

		LinearLayout selectionContext = new LinearLayout(false, blank);
		for (int i = 0; i < 30; i++) {
			LinearLayout wide = new LinearLayout(true);
			wide.add(new IconButton("camera250x250", assets.getSkin()));
			wide.add(new IconButton("camera250x250", assets.getSkin()));
			wide.add(new IconButton("camera250x250", assets.getSkin()));
			selectionContext.add(wide);
		}

		glassContainer.setSize(800, 600);
		glassContainer.setToolbar(toolbar);
		glassContainer.setNavigation(navigation);
		glassContainer.setSelectionContext(selectionContext);

		stage.addListener(new InputListener() {
			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				switch (keycode) {
				case Input.Keys.N:
					glassContainer.toggleNavigation();
					break;
				}
				return true;
			}
		});

		return glassContainer;
	}

	public static void main(String[] args) {
		new LwjglApplication(new BaseViewTest(), "Glass Container", 800, 600);
	}
}
