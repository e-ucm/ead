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
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.ead.editor.editorui.EditorUITest;
import es.eucm.ead.editor.view.widgets.IconButton;
import es.eucm.ead.editor.view.widgets.StretchableButton;

public class StretchableButtonTest extends EditorUITest {

	public static void main(String args[]) {
		new LwjglApplication(new StretchableButtonTest(),
				"Test for TweenTrack", 1000, 800);
	}

	@Override
	protected void builUI(Group root) {
		controller.getApplicationAssets().loadSkin("skins/light/skin");
		Skin skin = controller.getApplicationAssets().getSkin();

		IconButton button1 = new IconButton(skin.getDrawable("cut24x24"), 5,
				skin);
		IconButton button2 = new IconButton(skin.getDrawable("save24x24"), 5,
				skin);
		final IconButton button3 = new IconButton(skin.getDrawable("cut24x24"),
				5, skin);
		IconButton button4 = new IconButton(skin.getDrawable("save24x24"), 5,
				skin);
		final IconButton button5 = new IconButton(
				skin.getDrawable("copy24x24"), 5, skin);

		StretchableButton st1 = new StretchableButton(button1,
				button2.getPrefWidth(), skin.getDrawable("blank"), skin);

		StretchableButton st2 = new StretchableButton(button2,
				button2.getPrefWidth(), skin.getDrawable("bg-light"), skin);

		final StretchableButton st3 = new StretchableButton(button3, 150f,
				skin.getDrawable("bg-dark"), skin);

		StretchableButton st4 = new StretchableButton(button4,
				skin.getDrawable("blank"), skin);

		Table t = new Table();
		t.setFillParent(true);

		t.add(st1);
		t.add(st2);
		t.add(st3);
		t.add(st4);

		st3.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (st3.getWidget() == button3) {
					st3.setWidget(button5);
				} else {
					st3.setWidget(button3);
				}
			}
		});

		root.addActor(t);
	}

}
