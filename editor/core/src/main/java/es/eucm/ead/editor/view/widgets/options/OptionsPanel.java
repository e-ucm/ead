/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2013 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          C Profesor Jose Garcia Santesmases sn,
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
package es.eucm.ead.editor.view.widgets.options;

import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.widgets.AbstractWidget;

import java.util.Map;

public abstract class OptionsPanel extends AbstractWidget {

	public static final float MARGIN = 5.0f;

	protected Controller controller;

	private Array<Option> options;

	private Object target;

	public OptionsPanel(Controller controller, Object target) {
		this.controller = controller;
		this.target = target;
		options = new Array<Option>();
	}

	public void retarget(Object target) {
		this.target = target;
		for (Option option : options) {
			option.retarget(target);
		}
	}

	public NumberOption number(String label, String field) {
		NumberOption option = new NumberOption(controller, label, target, field);
		addOption(option);
		return option;
	}

	public EnumOption values(String label, String field,
			Map<String, Object> values) {
		EnumOption option = new EnumOption(controller, label, target, field,
				values);
		addOption(option);
		return option;
	}

	public StringOption string(String label, String field) {
		StringOption option = new StringOption(controller, label, target, field);
		addOption(option);
		return option;
	}

	public BooleanOption bool(String label, String field) {
		BooleanOption option = new BooleanOption(controller, label, target,
				field);
		addOption(option);
		return option;
	}

	public abstract SubOptionsPanel options(String label, String field);

	protected void addOption(Option option) {
		option.initialize();
		options.add(option);
		addActor(option);
	}

	@Override
	public float getPrefWidth() {
		return super.getChildrenMaxWidth();
	}

	@Override
	public float getPrefHeight() {
		return super.getChildrenTotalHeight();
	}

	@Override
	public void layout() {
		float maxLabelWidth = 0;
		for (Option option : options) {
			maxLabelWidth = Math.max(maxLabelWidth, option.getLabel()
					.getWidth());
		}

		float y = 0;
		for (Option option : options) {
			option.setMargin(maxLabelWidth);
			float height = option.getPrefHeight();
			float width = option.getPrefWidth();
			option.setBounds(0, y, width, height);
			y += height + MARGIN;
		}
	}
}
