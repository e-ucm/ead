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
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import es.eucm.ead.editor.control.commands.Command;

import java.util.HashMap;

public class DropdownOption<T> extends AbstractOption<T> {

	protected Item<T>[] items;
	protected HashMap<T, Item<T>> itemLookup = new HashMap<T, Item<T>>();

	protected SelectBox selectBox;

	protected static class Item<T> {
		public final String name;
		public final String tooltip;
		public final T value;
		public final int index;

		public Item(String name, String tooltip, T value, int index) {
			this.name = name;
			this.tooltip = tooltip;
			this.value = value;
			this.index = index;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	@SuppressWarnings("unchecked")
	private void initializeItems(T[] values, String[] names, String[] tooltips) {
		items = (Item<T>[]) new Item[values.length];
		for (int i = 0; i < values.length; i++) {
			String n = (names != null) ? names[i] : values[i].toString();
			String t = (tooltips != null) ? tooltips[i] : n;
			items[i] = new Item<T>(n, t, values[i], i);
			itemLookup.put(values[i], items[i]);
		}
	}

	/**
	 * A number option for integers from min (included) to max (excluded)
	 * 
	 * @param title
	 * @param toolTipText
	 */
	public DropdownOption(String title, String toolTipText) {
		super(title, toolTipText);
	}

	public DropdownOption items(T[] choices) {
		initializeItems(choices, null, null);
		return this;
	}

	/**
	 * Initializes available options
	 * 
	 * @param choices
	 *            to choose among (mandatory)
	 * @param names
	 *            (may be null; if so, toString() is used on each choice)
	 * @param tooltips
	 *            (may be null; if so, names are used)
	 * @return
	 */
	public DropdownOption items(T[] choices, String[] names, String[] tooltips) {
		initializeItems(choices, names, tooltips);
		return this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T getControlValue() {
		return items[selectBox.getSelectionIndex()].value;
	}

	@Override
	public void setControlValue(T newValue) {
		selectBox.setSelection(itemLookup.get(newValue).name);
	}

	@Override
	public Actor createControl() {
		selectBox = new SelectBox(items, skin);
		selectBox.addListener(new InputListener() {

			@Override
			public boolean handle(Event e) {
				if (changeConsideredRelevant(null, getControlValue())) {
					update();
					return true;
				}
				return false;
			}
		});
		return selectBox;
	}

	@Override
	protected Command createUpdateCommand() {
		return null; // new ChangeFieldCommand<T>(getControlValue(), accessor,
						// changed);
	}
}
