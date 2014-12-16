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

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Slider extends com.badlogic.gdx.scenes.scene2d.ui.Slider {
	public Slider(float min, float max, float stepSize, boolean vertical,
			Skin skin) {
		this(min, max, stepSize, vertical, skin.get("default-"
				+ (vertical ? "vertical" : "horizontal"), SliderStyle.class));
	}

	public Slider(float min, float max, float stepSize, boolean vertical,
			Skin skin, String styleName) {
		this(min, max, stepSize, vertical, skin.get(styleName,
				SliderStyle.class));
	}

	public Slider(float min, float max, float stepSize, boolean vertical,
			SliderStyle style) {
		super(min, max, stepSize, vertical, style);
		final InputListener inputListener = (InputListener) getListeners().get(
				0);
		clearListeners();
		addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				return inputListener.touchDown(event, x, y, pointer, button);
			}

			@Override
			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				inputListener.touchUp(event, x, y, pointer, button);
			}

			@Override
			public void touchDragged(InputEvent event, float x, float y,
					int pointer) {
				getStage().cancelTouchFocusExcept(this, Slider.this);
				inputListener.touchDragged(event, x, y, pointer);
			}
		});
	}
}
