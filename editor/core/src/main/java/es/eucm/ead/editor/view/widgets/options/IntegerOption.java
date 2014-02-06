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

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import es.eucm.ead.editor.control.commands.Command;
import es.eucm.ead.engine.gdx.Spinner;
import es.eucm.ead.editor.view.widgets.options.constraints.RangeConstraint;

public class IntegerOption extends AbstractOption<Integer> {

	protected Spinner spinner;
	private RangeConstraint rangeConstraint;

	/**
	 * A number option for integers from min (included) to max (excluded)
	 * 
	 * @param title
	 * @param toolTipText
	 */
	public IntegerOption(String title, String toolTipText) {
		super(title, toolTipText);
		rangeConstraint = new RangeConstraint(this);
		setConstraint(rangeConstraint);
	}

	/**
	 * @param min
	 *            value (inclusive) for this control
	 * @return the configured IntegerOption
	 */
	public IntegerOption min(int min) {
		rangeConstraint.setMin(min);
		return this;
	}

	/**
	 * @param max
	 *            value (inclusive) for this control
	 * @return the configured IntegerOption
	 */
	public IntegerOption max(int max) {
		rangeConstraint.setMax(max);
		return this;
	}

	@Override
	public Integer getControlValue() {
		String text = spinner.getText();
		try {
			return Integer.parseInt(text);
		} catch (NumberFormatException nfe) {
			// do nothing, will overwrite
		}
		return null;
	}

	@Override
	public void setControlValue(Integer newValue) {
		spinner.setText(newValue.toString());
	}

	@Override
	public Actor createControl() {
		spinner = new Spinner(skin);
		spinner.setText("");
		spinner.addListener(new InputListener() {
			@Override
			public boolean handle(Event event) {
				update();
				return super.handle(event);
			}
		});
		return spinner;
	}

	@Override
	protected Command createUpdateCommand() {
		// Users expect to undo/redo entire words, rather than
		// character-by-character
		/*
		 * return new ChangeFieldCommand<Integer>(getControlValue(), accessor,
		 * changed) {
		 * 
		 * @Override public boolean likesToCombine(Integer nextValue) { //
		 * return Math.abs(nextValue - oldValue) <= 1; return true; } };
		 */
		return null;
	}

	@Override
	protected void decorate(boolean valid) {
		String sytleName = valid ? "default" : "invalid";
		// SpinnerStyle style = Editor.assets.getSkin().get(sytleName,
		// SpinnerStyle.class);
		// spinner.setStyle(style);
	}
}
