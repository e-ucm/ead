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
package es.eucm.ead.editor.view.widgets.options.constraints;

import es.eucm.ead.editor.view.widgets.options.Option;

/**
 * Constraint for ranges, with a min value and a max value
 * 
 */
public class RangeConstraint extends Constraint {

	private Integer max;

	private Integer min;

	public RangeConstraint(Option<?> option) {
		super(option);
	}

	@Override
	public boolean isValid() {
		Integer value = (Integer) option.getControlValue();
		return !((value == null) || (min != null && value < min) || (max != null && value > max));
	}

	@Override
	public String getTooltip() {
		String tooltip = "";
		if (min != null) {
			// tooltip += Editor.i18n.m("constraint.range.min", min);
		}

		if (max != null) {
			// tooltip += (min == null ? "" : " ")
			// + Editor.i18n.m("constraint.range.max", max);
		}
		return tooltip;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public void setMin(int min) {
		this.min = min;
	}
}
