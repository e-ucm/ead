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
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.view.widgets.IconButton;
import es.eucm.ead.editor.view.widgets.Toolbar;
import es.eucm.ead.editor.view.widgets.iconwithpanel.Settings;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.engine.I18N;

public class MockupGallerySkinTest extends MockupUITest {

	private static final float BIG_PAD = 160;
	private static final float NORMAL_PAD = 40;
	private static final float SMALL_PAD = 20;

	private float icon_size;

	private Skin skin;

	private Toolbar topBar;

	private Table gallery;

	@Override
	protected Actor builUI(Skin skin, I18N i18n) {
		Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
		this.skin = skin;

		icon_size = stage.getHeight() * 0.05f;

		LinearLayout root = new LinearLayout(false);
		root.setFillParent(true);

		createTopToolbar();
		createGallegyTable();

		LinearLayout bot = new LinearLayout(true);
		bot.add(gallery).expand(true, true);

		root.add(topBar).expandX();
		root.add(bot).expand(true, true);

		return root;
	}

	public static void main(String[] args) {
		new LwjglApplication(new MockupGallerySkinTest(), "Skin test", 1200,
				700);
	}

	private void createTopToolbar() {
		topBar = new Toolbar(skin, "white_top") {
			@Override
			public float getPrefHeight() {
				return 0.08f * stage.getHeight();
			}
		};

		topBar.align(Align.right);

		Image eAdventure = new Image(skin, "eAdventure");
		topBar.add(eAdventure).size(icon_size * 4.5f, icon_size)
				.padRight(BIG_PAD * 2);

		IconButton search = new IconButton("search80x80", 0, skin);
		topBar.add(search).size(icon_size).padRight(NORMAL_PAD);

		SelectBox<String> selectBox = new SelectBox<String>(skin);
		Array array = new Array();
		array.add("A-Z");
		array.add("Z-A");
		selectBox.setItems(array);

		topBar.add(selectBox).size(icon_size * 4, icon_size)
				.padRight(NORMAL_PAD);

		Settings settings = new Settings(controller, 0f, icon_size);
		settings.getPanel().addTouchableActor(topBar);

		topBar.add(settings).size(icon_size).padRight(SMALL_PAD);

	}

	private void createGallegyTable() {
		gallery = new Table();
		gallery.align(Align.top);

		Table project1 = new Table();
		project1.setBackground(skin.getDrawable("project"));
		Image project = new Image(skin, "new_project80x80");
		project1.add(project);

		Table name1 = new Table();
		name1.setBackground(skin.getDrawable("project_name"));
		Label text1 = new Label("Create new", skin);
		text1.setFontScale(0.5f);
		name1.add(text1);

		Table project2 = new Table();
		project2.setBackground(skin.getDrawable("project"));
		Image first = new Image(skin, "first80x80");
		first.setScale(0.5f);
		project2.add(first).expand().top().left();

		Table name2 = new Table();
		name2.setBackground(skin.getDrawable("project_name"));
		Label text2 = new Label("Name", skin);
		text2.setFontScale(0.5f);

		name2.add(text2);

		gallery.add(project1).size(300, 150).padRight(NORMAL_PAD)
				.padTop(NORMAL_PAD);
		gallery.add(project2).size(300, 150).padRight(NORMAL_PAD)
				.padTop(NORMAL_PAD);
		gallery.row();
		gallery.add(name1).width(300).padRight(NORMAL_PAD);
		gallery.add(name2).width(300).padRight(NORMAL_PAD);

	}

}
