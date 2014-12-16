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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;

/**
 * A button that changes its drawable when it's clicked, passing through all its
 * states
 */
public class StatesButton extends Button {

	private Array<Drawable> states = new Array<Drawable>();

	private int index = -1;

	private Image stateImage;

	public StatesButton(StatesButtonStyle style, Drawable... states) {
		this(style);
		for (Drawable state : states) {
			addState(state);
		}
	}

	public StatesButton(Skin skin, String... states) {
		this(skin.get(StatesButtonStyle.class), skin, states);
	}

	public StatesButton(String style, Skin skin, String... states) {
		this(skin.get(style, StatesButtonStyle.class), skin, states);
	}

	public StatesButton(StatesButtonStyle style, Skin skin, String... states) {
		this(style);
		for (String state : states) {
			addState(skin.getDrawable(state));
		}
	}

	public StatesButton(StatesButtonStyle style) {
		super(style);
		add(stateImage = new Image()).pad(WidgetBuilder.dpToPixels(12));
		stateImage.setColor(style.iconColor);
		addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				incIndex();
			}
		});
	}

	public void incIndex() {
		index = (index + 1) % states.size;
		stateImage.setDrawable(states.get(index));

		ChangeEvent changeEvent = Pools.obtain(ChangeEvent.class);
		fire(changeEvent);
		Pools.free(changeEvent);
	}

	public void addState(Drawable drawableState) {
		if (stateImage.getDrawable() == null) {
			stateImage.setDrawable(drawableState);
			index = 0;
		}
		states.add(drawableState);
	}

	public void setState(int index) {
		if (index < 0 || index >= states.size)
			throw new IllegalArgumentException("State index out of bounds");

		stateImage.setDrawable(states.get(index));
		this.index = index;
	}

	public int getSelectedIndex() {
		return index;
	}

	public Drawable getSelectedDrawable() {
		return states.get(index);
	}

	public static class StatesButtonStyle extends ButtonStyle {
		public Color iconColor;
	}

}
