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
package es.eucm.ead.editor.editorui.widgets;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import es.eucm.ead.editor.control.MockupController.Dpi;
import es.eucm.ead.editor.editorui.MockupUITest;
import es.eucm.ead.editor.view.widgets.HorizontalToolbar;
import es.eucm.ead.editor.view.widgets.PositionedHiddenPanel;
import es.eucm.ead.editor.view.widgets.iconwithpanel.IconWithScalePanel;
import es.eucm.ead.engine.I18N;

public class IconWithScalePanelTest extends MockupUITest {

	@Override
	protected Actor builUI(Skin skin, I18N i18n) {

		Table table = new Table();
		table.setFillParent(true);

		HorizontalToolbar topBar = new HorizontalToolbar(skin, 20, "white_top");
		HorizontalToolbar botBar = new HorizontalToolbar(skin, 30,
				"white_bottom");

		IconWithScalePanel widgetTopLeft = new IconWithScalePanel("menu", 0,
				-1, skin, Color.ORANGE);
		PositionedHiddenPanel panel = widgetTopLeft.getPanel();
		panel.defaults().pad(20);
		panel.add(new TextButton("Button1", skin));
		panel.add(new TextButton("Button2", skin));
		panel.add(new TextButton("Button3", skin));

		IconWithScalePanel widgetTopRight = new IconWithScalePanel("menu", 5,
				1, skin, Color.GREEN);
		panel = widgetTopRight.getPanel();
		panel.defaults().pad(20);
		panel.add(new TextButton("Button1", skin));
		panel.add(new TextButton("Button2", skin));
		panel.add(new TextButton("Button3", skin));
		panel.add(new TextButton("Button4", skin));

		IconWithScalePanel widgetBotLeft = new IconWithScalePanel("menu", 0,
				skin, Color.BLUE);
		panel = widgetBotLeft.getPanel();
		panel.defaults().pad(20);
		panel.add(new TextButton("Button1", skin));
		panel.add(new TextButton("Button2", skin));
		panel.add(new TextButton("Button3", skin));

		IconWithScalePanel widgetBotRight = new IconWithScalePanel("menu", 10,
				3, skin);
		panel = widgetBotRight.getPanel();
		panel.defaults().pad(20);
		panel.add(new TextButton("Button1", skin));
		panel.add(new TextButton("Button2", skin));
		panel.add(new TextButton("Button3", skin));
		panel.add(new TextButton("Button4", skin));
		panel.add(new TextButton("Button5", skin));
		panel.add(new TextButton("Button6", skin));
		panel.add(new TextButton("Button7", skin));

		IconWithScalePanel widgetBotRight2 = new IconWithScalePanel("menu", 15,
				skin, Color.RED);
		panel = widgetBotRight2.getPanel();
		panel.defaults().pad(20);
		panel.add(new TextButton("Button1", skin));
		panel.add(new TextButton("Button2", skin));
		panel.add(new TextButton("Button3", skin));

		topBar.leftAdd(widgetTopLeft).top();
		topBar.rightAdd(widgetTopRight);
		topBar.backgroundColor(Color.ORANGE);

		botBar.leftAdd(widgetBotLeft).bottom();
		botBar.rightAdd(widgetBotRight);
		botBar.rightAdd(widgetBotRight2);
		botBar.backgroundColor(Color.GRAY);

		table.add(topBar).expandX().fill();
		table.row();
		table.add().expand().fill();
		table.row();
		table.add(botBar).expandX().fill();

		final IconWithScalePanel[] panels = new IconWithScalePanel[] {
				widgetTopLeft, widgetTopRight, widgetBotLeft, widgetBotRight };

		table.addAction(Actions.forever(Actions.sequence(Actions.delay(2f,
				Actions.run(new Runnable() {
					@Override
					public void run() {
						for (IconWithScalePanel widget : panels) {
							if (widget.getPanel().hasParent()) {
								widget.hidePanel();
							} else {
								widget.showPanel();
							}
						}
					}
				})))));

		return table;
	}

	public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1280;
		config.height = 720;
		config.overrideDensity = MathUtils.round(Dpi.HDPI.getMaxDpi());
		config.title = "TEST";
		new LwjglApplication(new IconWithScalePanelTest(), config);
	}

}