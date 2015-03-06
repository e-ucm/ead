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
package es.eucm.ead.editor.widgets;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.engine.gdx.AbstractWidget;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;

/**
 * Created by angel on 20/03/14.
 */
public class LinearLayoutTest extends AbstractWidgetTest {
	@Override
	public AbstractWidget createWidget(Controller controller) {
		setFillWindow(true);
		controller.getApplicationAssets().loadSkin("skins/light/skin");
		Skin skin = controller.getApplicationAssets().getSkin();

		Drawable blank = skin.getDrawable("blank");
		Drawable outline = skin.getDrawable("outline");
		Drawable bg1 = skin.getDrawable("bg-light");
		Drawable bg2 = skin.getDrawable("bg-dark");

		LinearLayout container = new LinearLayout(false);
		container.background(blank);

		LinearLayout topbar = new LinearLayout(true);
		topbar.pad(5);
		topbar.background(outline);

		LinearLayout mainControls = new LinearLayout(true);
		mainControls.background(bg2);
		mainControls.add(new Label("eAdventure Logo", skin)).expand(true, true);

		LinearLayout globalControls1 = new LinearLayout(true);
		globalControls1.background(bg1).defaultWidgetsMargin(5).pad(0, 0, 0, 0);
		globalControls1.add(new Label("Back", skin));
		globalControls1.add(new Label("Forward", skin));
		globalControls1.add(new Label("Search", skin));

		LinearLayout globalControls2 = new LinearLayout(true);
		globalControls2.background(bg1).defaultWidgetsMargin(5);
		globalControls2.add(new Label("Save", skin));
		globalControls2.add(new Label("Cut", skin));
		globalControls2.add(new Label("Copy", skin));
		globalControls2.add(new Label("Paste", skin));
		globalControls2.add(new Label("Undo", skin));
		globalControls2.add(new Label("Redo", skin));

		LinearLayout globalControls = new LinearLayout(false)
				.background(outline).pad(5).defaultWidgetsMargin(5);
		globalControls.add(globalControls1).left();
		globalControls.add(globalControls2).left();

		mainControls.add(globalControls).centerY();

		topbar.add(mainControls);
		topbar.addSpace();

		LinearLayout perspective = new LinearLayout(true);
		perspective.defaultWidgetsMargin(5.f);
		perspective.add(new Label("Home", skin));
		perspective.add(new Label("Interface", skin));
		perspective.add(new Label("Education & Gamification", skin));
		perspective.add(new Label("Test", skin));
		perspective.add(new Label("Export", skin)).margin(5, 5, 50, 5);

		topbar.add(perspective);

		LinearLayout editContainer = new LinearLayout(true).background(bg1);

		LinearLayout sceneLayers = new LinearLayout(false).background(blank)
				.pad(20);
		sceneLayers.add(new Label("Layer 1", skin));
		sceneLayers.add(new Label("Layer 2", skin));
		sceneLayers.add(new Label("Layer 3", skin));
		sceneLayers.add(new Label("Layer 4", skin));
		sceneLayers.add(new Label("Layer 5", skin));

		editContainer.add(sceneLayers).top();

		topbar.background(outline);

		container.add(editContainer).expand(true, true);
		container.add(topbar).expandX();
		return container;
	}

	public static void main(String args[]) {
		new LwjglApplication(new LinearLayoutTest(), "Test for Linear Layout",
				800, 600);
	}
}
