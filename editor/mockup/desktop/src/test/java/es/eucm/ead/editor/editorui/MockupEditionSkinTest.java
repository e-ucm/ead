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
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.view.widgets.DropDown;
import es.eucm.ead.editor.view.widgets.IconButton;
import es.eucm.ead.editor.view.widgets.Toolbar;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.engine.I18N;

public class MockupEditionSkinTest extends MockupUITest {

	private static final float BIG_PAD = 160;
	private static final float NORMAL_PAD = 40;
	private static final float SMALL_PAD = 20;
	private static final float TEXT_PAD = 50;

	private float icon_size;

	private Skin skin;

	private Toolbar leftBar;
	private Toolbar topBar;
	private Toolbar paintPanel;

	private Table leftPanel;

	private Table guidePanel;

	private Table bubble_left;
	private Table bubble_top;

	@Override
	protected Actor buildUI(Skin skin, I18N i18n) {
		Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
		this.skin = skin;

		icon_size = stage.getHeight() * 0.05f;

		LinearLayout root = new LinearLayout(false);

		LinearLayout top = new LinearLayout(true);
		Table bot = new Table();

		createTopToolbar();
		createLeftToolbar();

		createPaintPanel();
		createGuidePanel();
		createBubbles();

		IconButton union = createNavigationButton();
		top.add(union);
		top.add(topBar).expandX();

		bot.add(leftBar).expandY().left().fill();
		Table center = new Table();
		center.align(Align.center);
		center.add(guidePanel).expand().fill();
		center.row();
		center.add(paintPanel).expandX().fill();
		bot.add(center).expand().fill();

		root.addActor(leftPanel);
		root.addActor(bubble_left);
		root.addActor(bubble_top);

		root.setFillParent(true);
		root.add(top).expandX();
		root.add(bot).expandX().expandY();

		return root;
	}

	public static void main(String[] args) {
		new LwjglApplication(new MockupEditionSkinTest(), "Skin test", 1200,
				700);
	}

	private void createBubbles() {
		bubble_left = new Table(skin);
		bubble_left.setBackground(skin.getDrawable("bubble_left"));

		bubble_left.setBounds(0.05f * stage.getWidth(),
				0.5f * stage.getHeight(), 200, 300);
		bubble_left.setVisible(false);
		bubble_left.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				bubble_left.setVisible(false);
			}
		});

		bubble_top = new Table(skin);
		bubble_top.setBackground(skin.getDrawable("bubble_top"));

		bubble_top.setBounds(0.7f * stage.getWidth(),
				0.92f * stage.getHeight() - 200, 300, 200);
		bubble_top.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				bubble_left.setVisible(true);
				bubble_top.setVisible(false);
			}
		});

		Label bubble1 = new Label("Hi", skin);
		Label bubble2 = new Label("I'm a bubble \n Click me", skin);

		bubble_left.add(bubble1);
		bubble_top.add(bubble2);
	}

	private void createPaintPanel() {
		paintPanel = new Toolbar(skin, "white_bottom");

		// Colors
		Image color1 = new Image(skin, "rectangle");
		color1.setColor(Color.YELLOW);

		Image color2 = new Image(skin, "rectangle");
		color2.setColor(Color.ORANGE);

		Image color3 = new Image(skin, "rectangle");
		color3.setColor(Color.RED);

		Image color4 = new Image(skin, "rectangle");
		color4.setColor(Color.GREEN);

		Image color5 = new Image(skin, "rectangle");
		color5.setColor(Color.BLUE);

		Image color6 = new Image(skin, "rectangle");
		color6.setColor(Color.BLACK);

		Slider slider = new Slider(0, 100, 1, false, skin, "white-horizontal");

		TextButton save = new TextButton("Save", skin, "white");

		TextButton disabled = new TextButton("Disabled", skin, "white");
		disabled.setDisabled(true);

		paintPanel.add(color1).padLeft(NORMAL_PAD);
		paintPanel.add(color2);
		paintPanel.add(color3);
		paintPanel.add(color4);
		paintPanel.add(color5);
		paintPanel.add(color6);
		paintPanel.add(slider).expandX().fill().padLeft(NORMAL_PAD)
				.padRight(NORMAL_PAD);
		paintPanel.add(save).padLeft(NORMAL_PAD);
		paintPanel.add(disabled).padLeft(NORMAL_PAD);
	}

	private void createGuidePanel() {
		guidePanel = new Table(skin);
		guidePanel.setBackground(skin.getDrawable("dialog"));

		LinearLayout top = new LinearLayout(false);
		LinearLayout bot = new LinearLayout(true);

		IconButton photo = new IconButton("camera250x250", 0, skin);
		Label photoText = new Label(
				"You can create your background scene instantly \n Take a Picture",
				skin);
		photoText.setAlignment(Align.center);

		top.add(photo);
		top.add(photoText);

		Table paintT = new Table();
		IconButton paint = new IconButton("paint150x150", 0, skin);
		Label paintText = new Label(
				"You can paint your first image \n with your fingers", skin);
		paintText.setAlignment(Align.center);
		paintT.add(paint);
		paintT.row();
		paintT.add(paintText).pad(0, TEXT_PAD, 0, TEXT_PAD);

		Table repoT = new Table();
		IconButton repo = new IconButton("repository150x150", 0, skin);
		Label repoText = new Label(
				"Without ideas? \n You can find items shared by other \n in the repository",
				skin);
		repoText.setAlignment(Align.center);
		repoT.add(repo);
		repoT.row();
		repoT.add(repoText).pad(0, TEXT_PAD, 0, TEXT_PAD);

		Table androidT = new Table();
		IconButton android = new IconButton("android_gallery150x150", 0, skin);
		Label androidText = new Label(
				"Do you have the image that wanted? \n Add the image from \n your phone's gallery",
				skin);
		androidText.setAlignment(Align.center);
		androidT.add(android);
		androidT.row();
		androidT.add(androidText).pad(0, TEXT_PAD, 0, TEXT_PAD);

		bot.add(paintT);
		bot.add(repoT);
		bot.add(androidT);

		guidePanel.add(top);
		guidePanel.row();
		guidePanel.add(bot);

	}

	private void createTopToolbar() {
		topBar = new Toolbar(skin, "white_top");

		topBar.align(Align.right);

		IconButton play = new IconButton("play80x80", 0, skin);

		IconButton undo = new IconButton("undo80x80", 0, skin);
		IconButton redo = new IconButton("redo80x80", 0, skin);

		IconButton camera = new IconButton("camera80x80", 0, skin);
		IconButton repository = new IconButton("repository80x80", 0, skin);
		IconButton android = new IconButton("android_gallery80x80", 0, skin);

		IconButton paint = new IconButton("paint80x80", 0, skin);
		IconButton text = new IconButton("text80x80", 0, skin);

		IconButton zones = new IconButton("interactive80x80", 0, skin);
		IconButton gate = new IconButton("gateway80x80", 0, skin);

		IconButton others = new IconButton("others80x80", 0, skin);

		Image logo = new Image(skin, "eAdventure");
		topBar.add(logo).size(icon_size * 4.5f, icon_size)
				.padRight(NORMAL_PAD * 2);

		topBar.add(play).size(icon_size).padRight(BIG_PAD);

		topBar.add(undo).size(icon_size).pad(SMALL_PAD);
		topBar.add(redo).size(icon_size).padRight(BIG_PAD);

		topBar.add(camera).size(icon_size).padRight(SMALL_PAD);
		topBar.add(repository).size(icon_size).padRight(SMALL_PAD);
		topBar.add(android).size(icon_size).padRight(NORMAL_PAD);

		topBar.add(paint).size(icon_size).padRight(SMALL_PAD);
		topBar.add(text).size(icon_size).padRight(NORMAL_PAD);

		topBar.add(zones).size(icon_size).padRight(SMALL_PAD);
		topBar.add(gate).size(icon_size).padRight(NORMAL_PAD);

		topBar.add(others).size(icon_size).padRight(SMALL_PAD);
	}

	private void createLeftToolbar() {
		leftBar = new Toolbar(skin, "white_left") {
			@Override
			public float getPrefWidth() {
				return 0.05f * stage.getWidth();
			}
		};

		leftBar.align(Align.center);

		IconButton gateway = new IconButton("gateway_reverse80x80", 0, skin);
		IconButton variables = new IconButton("variable80x80", 0, skin);
		IconButton sound = new IconButton("sound80x80", 0, skin);
		IconButton conversations = new IconButton("conversation80x80", 0, skin);
		IconButton visibility = new IconButton("visibility80x80", 0, skin);
		IconButton lock = new IconButton("lock80x80", 0, skin);
		IconButton tween = new IconButton("tween80x80", 0, skin);

		leftBar.addInNewRow(gateway).size(icon_size).padBottom(NORMAL_PAD);
		leftBar.addInNewRow(variables).size(icon_size).padBottom(NORMAL_PAD);
		leftBar.addInNewRow(sound).size(icon_size).padBottom(NORMAL_PAD);
		leftBar.addInNewRow(conversations).size(icon_size)
				.padBottom(NORMAL_PAD);
		leftBar.addInNewRow(visibility).size(icon_size).padBottom(NORMAL_PAD);
		leftBar.addInNewRow(lock).size(icon_size).padBottom(NORMAL_PAD);
		leftBar.addInNewRow(tween).size(icon_size).padBottom(NORMAL_PAD);
	}

	private void createLeftPanel() {
		leftPanel = new Table(skin);
		leftPanel.setBackground(skin.getDrawable("left_panel"));

		leftPanel.setBounds(0, 0, stage.getWidth() / 2,
				0.92f * stage.getHeight());
		leftPanel.setVisible(false);

		LinearLayout[] lists = new LinearLayout[2];

		for (int i = 0; i < lists.length; ++i) {
			// Multiple DropDowns
			IconButton gateway = new IconButton("gateway_reverse80x80", 0, skin);
			IconButton variables = new IconButton("variable80x80", 0, skin);
			IconButton sound = new IconButton("sound80x80", 0, skin);
			IconButton conversations = new IconButton("conversation80x80", 0,
					skin);
			IconButton visibility = new IconButton("visibility80x80", 0, skin);
			IconButton lock = new IconButton("lock80x80", 0, skin);
			IconButton tween = new IconButton("tween80x80", 0, skin);

			Array a = new Array();
			a.add(gateway);
			a.add(variables);
			a.add(sound);
			Array b = new Array();
			b.add(visibility);
			b.add(lock);
			Array c = new Array();
			c.add(tween);
			c.add(conversations);

			LinearLayout list = new LinearLayout(true);

			DropDown drop1 = new DropDown(skin, "white_left");
			drop1.setItems(a);
			DropDown drop2 = new DropDown(skin, "white_center");
			drop2.setItems(b);
			DropDown drop3 = new DropDown(skin, "white_right");
			drop3.setItems(c);

			list.add(drop1);
			list.add(drop2);
			list.add(new IconButton("lock80x80", 0, skin, "white_center"));
			list.add(drop3);
			lists[i] = list;
		}

		leftPanel.add(lists[0]).padBottom(NORMAL_PAD);
		leftPanel.row();
		// SelectBox
		SelectBox<String> selectBox = new SelectBox<String>(skin);
		Array array = new Array();
		array.add("SelectBox");
		array.add("0001");
		array.add("0010");
		array.add("0100");
		selectBox.setItems(array);

		leftPanel.add(selectBox).padBottom(NORMAL_PAD);
		leftPanel.row();

		// TextField
		TextField text = new TextField("TextField", skin);

		leftPanel.add(text).expandX().fill().padBottom(NORMAL_PAD);
		leftPanel.row();
		leftPanel.add(lists[1]).padBottom(NORMAL_PAD);
		leftPanel.bottom();
	}

	private IconButton createNavigationButton() {
		IconButton union = new IconButton("menu", 0, skin, "white_union") {
			@Override
			public float getPrefHeight() {
				return topBar.getPrefHeight();
			}

			@Override
			public float getPrefWidth() {
				return leftBar.getPrefWidth();
			}
		};

		createLeftPanel();

		union.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				leftPanel.setVisible(!leftPanel.isVisible());
				if (bubble_left.isVisible() || bubble_top.isVisible()) {
					bubble_left.setVisible(false);
					bubble_top.setVisible(false);
				}
			}
		});

		return union;
	}

}
