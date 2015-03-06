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
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.engine.gdx.AbstractWidget;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ToggleImagesList extends AbstractWidget {

	private Array<ChangeListener> changeListeners;

	private Skin skin;

	private LinearLayout container;

	private String selectedValue;

	private Map<ToggleImageButton, String> values;

	public ToggleImagesList(Skin skin, boolean horizontal) {
		this.skin = skin;
		ToggleImagesListStyle style = skin.get(ToggleImagesListStyle.class);
		if (horizontal) {
			container = new LinearLayout(true, style.background);
		} else {
			container = new LinearLayout(false, style.background);
		}
		values = new IdentityHashMap<ToggleImageButton, String>();
		changeListeners = new Array<ChangeListener>();
		addActor(container);
	}

	public void addChangeListener(ChangeListener listener) {
		changeListeners.add(listener);
	}

	public ToggleImagesList button(Drawable icon, String value) {
		ToggleImageButton button = new ToggleImageButton(icon, skin);
		button.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				select((ToggleImageButton) event.getListenerActor());
			}
		});
		values.put(button, value);
		container.add(button).left();
		return this;
	}

	private void select(ToggleImageButton target) {
		for (ToggleImageButton b : values.keySet()) {
			b.setChecked(false);
		}
		String value = values.get(target);
		if (!value.equals(selectedValue)) {
			selectedValue = values.get(target);
			for (ChangeListener c : changeListeners) {
				c.changed(selectedValue);
			}
		}
		target.setChecked(true);
	}

	public String getSelectedValue() {
		return selectedValue;
	}

	@Override
	public float getPrefWidth() {
		return container.getPrefWidth();
	}

	@Override
	public float getPrefHeight() {
		return container.getPrefHeight();
	}

	@Override
	public float getMaxWidth() {
		return getPrefWidth();
	}

	@Override
	public float getMaxHeight() {
		return getPrefHeight();
	}

	@Override
	public void layout() {
		setBounds(container, 0, 0, getWidth(), getHeight());
	}

	public void setValue(String value) {
		if (value == null) {
			select(null);
			return;
		}

		for (Entry<ToggleImageButton, String> e : values.entrySet()) {
			if (value.equals(e.getValue())) {
				select(e.getKey());
				return;
			}
		}
	}

	public interface ChangeListener {
		void changed(String newValue);
	}

	public static class ToggleImagesListStyle {
		public Drawable background;
	}
}
