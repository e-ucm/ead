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
package es.eucm.ead.editor.view.widgets.modelwidgets;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.schema.components.ModelComponent;

public class ModelSelectBox extends LinearLayout {

	private SelectBox<String> selectBox;

	protected float base;

	private float factor;

	private ModelComponent component;

	private float value;

	public ModelSelectBox(Skin skin, String name, Array<String> items,
			String labelStyle, String selectBoxStyle) {
		super(true);

		base = 0;
		value = 0;

		add(new Label(name, skin, labelStyle)).expandX().left();
		selectBox = new SelectBox<String>(skin, selectBoxStyle);

		selectBox.setItems(items);

		selectBox.getSelection().setProgrammaticChangeEvents(false);
		add(selectBox).right();
	}

	public void calculateFactor(float value) {
		factor = 0;

		if (selectBox.getSelectedIndex() == 2) {
			if (value < base) {
				factor = 4f;
			} else {
				factor = 2f;
			}
		} else if (selectBox.getSelectedIndex() == 1) {
			if (value < base) {
				factor = 2f;
			} else {
				factor = 0.5f;
			}
		} else if (selectBox.getSelectedIndex() == 0) {
			if (value > base) {
				factor = 0.25f;
			} else {
				factor = 0.5f;
			}
		}
	}

	public void setInitValue(float value) {
		this.value = value;
	}

	public void setBaseValue(float value) {
		base = value;
	}

	public void loadComponent(ModelComponent component) {
		this.component = component;

		if (value < base) {
			selectBox.setSelectedIndex(0);
		} else if (value == base) {
			selectBox.setSelectedIndex(1);
		} else {
			selectBox.setSelectedIndex(2);
		}
	}

	public float getFactor() {
		return factor;
	}

	public ModelComponent getComponent() {
		return component;
	}
}
