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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

public class MultiStateButton extends TextButton {

	private int count;

	private Array<String> states;
	private Array<Color> colors;

	private float width;

	private float margin;

	public MultiStateButton(Skin skin, Array<String> statesArray,
			Array<Color> colorsArray) {
		this(skin, statesArray, colorsArray, 0);
	}

	public MultiStateButton(Skin skin, Array<String> statesArray,
			Array<Color> colorsArray, float margin) {

		super(statesArray.first(), skin, "to_color");
		setColor(colorsArray.first());

		this.width = 0;
		this.margin = margin;

		this.count = 0;
		this.states = statesArray;
		this.colors = colorsArray;

		for (String state : states) {
			Label aux = new Label(state, skin);
			if (aux.getWidth() > width) {
				width = aux.getWidth();
			}
		}

		addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				count++;
				setText(states.get(count % states.size));
				setColor(colors.get(count % colors.size));
			}
		});
	}

	public void selectText(String text) {
		if (states.contains(text, true)) {
			this.count = states.indexOf(text, true);
			setText(states.get(count % states.size));
			setColor(colors.get(count % colors.size));
		}
	}

	@Override
	public float getPrefWidth() {
		return width + margin;
	}

}
