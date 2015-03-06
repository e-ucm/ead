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
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.engine.gdx.AbstractWidget;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.engine.gdx.Spinner;

/**
 * Created by angel on 2/04/14.
 */
public class UITest extends AbstractWidgetTest {

	@Override
	public AbstractWidget createWidget(Controller controller) {
		Skin skin = controller.getApplicationAssets().getSkin();
		LinearLayout layout = new LinearLayout(false);
		layout.add(new Label("This a test label with some text.", skin));
		layout.add(new TextField("This a text field with some text", skin));
		TextArea textArea = new TextArea(
				"This is a text area with some text \nin \nseveral \nlines",
				skin);
		textArea.setPrefRows(10);
		layout.add(textArea);
		SelectBox<String> selectBox = new SelectBox<String>(skin);
		String[] items = new String[200];
		for (int i = 0; i < 200; i++) {
			items[i] = "String " + i;
		}
		selectBox.setItems(items);
		layout.add(selectBox);
		layout.add(new Spinner(skin, 0.1f));

		// Buttons
		LinearLayout buttonsLayout = new LinearLayout(true);
		buttonsLayout.add(new Label("Buttons: ", skin));
		buttonsLayout.add(new Button(skin));
		buttonsLayout.add(new TextButton("Button with text", skin));
		buttonsLayout.add(new ImageButton(skin.getDrawable("undo24x24"), skin
				.getDrawable("redo24x24")));
		layout.add(buttonsLayout);
		// layout.add(new Slider(0, 10, 1, false, skin));
		return layout;
	}

	public static void main(String args[]) {
		new LwjglApplication(new UITest(), "Test for TextArea", 1000, 800);
	}
}
