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
package es.eucm.ead.editor.control.commands;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import es.eucm.ead.editor.model.events.FieldEvent;
import es.eucm.ead.editor.model.events.ModelEvent;

/**
 * A command that changes a field-value. The most common case of command.
 */
public class FieldCommand extends Command {

	protected Object oldValue;

	protected Object newValue;

	private String fieldName;

	private Object target;

	private boolean combine;

	private Field field;

	/**
	 * Simplified constructor
	 * 
	 * @param newValue
	 *            new value (T)
	 * @param target
	 *            where the value should be set
	 * @param fieldName
	 *            name of writable attribute in target
	 */
	public FieldCommand(Object target, String fieldName, Object newValue,
			boolean combine) {
		this.newValue = newValue;
		this.fieldName = fieldName;
		this.target = target;
		this.combine = combine;
		this.field = getField(target, fieldName);
	}

	public String getFieldName() {
		return fieldName;
	}

	@Override
	public ModelEvent doCommand() {
		if (field == null) {
			return null;
		}

		try {
			oldValue = field.get(target);
		} catch (ReflectionException e) {
			Gdx.app.error("FieldCommand", "Error reading field " + fieldName
					+ " in " + target, e);
			return null;
		}

		return setValue(newValue);
	}

	/**
	 * Sets the value and returns a mode-event describing what nodes have
	 * changed. Called by both undo() and redo().
	 * 
	 * @param value
	 * @return
	 */
	protected ModelEvent setValue(Object value) {
		if (field == null) {
			return null;
		}

		try {
			field.set(target, value);
		} catch (ReflectionException e) {
			Gdx.app.error("FieldCommand", "Error setting field " + fieldName
					+ " in " + target, e);
			return null;
		}
		return new FieldEvent(target, fieldName, value);
	}

	@Override
	public boolean canUndo() {
		return true;
	}

	@Override
	public ModelEvent undoCommand() {
		return setValue(oldValue);
	}

	@Override
	public boolean combine(Command other) {
		if (other instanceof FieldCommand) {
			FieldCommand o = (FieldCommand) other;
			if (this.combine && o.target == this.target
					&& o.fieldName.equals(this.fieldName)) {
				newValue = o.newValue;
				this.combine = o.combine;
				return true;
			}
		}
		return false;
	}

	private Field getField(Object target, String fieldName) {
		Field field = null;
		Class<?> clazz = target.getClass();
		while (clazz != null) {
			try {
				field = ClassReflection.getDeclaredField(clazz, fieldName);
			} catch (ReflectionException e) {
			}
			// getSuperclass is supported by GWT
			clazz = clazz.getSuperclass();
		}
		if (field != null) {
			field.setAccessible(true);
		}
		return field;
	}

}
