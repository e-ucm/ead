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
package es.eucm.ead.editor.view.widgets;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;

/**
 * A label that can be edited when double clicked.
 */
public class EditableLabel extends TextField {

	private float prefWidth;

	public EditableLabel(String text, Skin skin) {
		super(text, skin);
		init();
	}

	public EditableLabel(String text, Skin skin, String styleName) {
		super(text, skin, styleName);
		init();
	}

	public EditableLabel(String text, TextFieldStyle style) {
		super(text, style);
		init();
	}

	@Override
	public float getPrefWidth() {
		return prefWidth;
	}

	public void setPrefWidth(float prefWidth) {
		this.prefWidth = prefWidth;
	}

	private void init() {
		setDisabled(true);
		addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (getTapCount() == 2) {
					setDisabled(false);
					getStage().setKeyboardFocus(EditableLabel.this);
					selectAll();
				}
			}

			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				switch (keycode) {
				case Keys.ESCAPE:
				case Keys.ENTER:
					setDisabled(true);
					getStage().setKeyboardFocus(getParent());
					return true;
				}
				return false;
			}
		});
		addListener(new FocusListener() {
			@Override
			public void keyboardFocusChanged(FocusEvent event, Actor actor,
					boolean focused) {
				setDisabled(!focused);
			}
		});
	}
}
