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
import es.eucm.ead.editor.control.Command;
import es.eucm.ead.editor.model.EditorModel;
import es.eucm.ead.editor.model.ModelEvent;
import es.eucm.ead.editor.model.DependencyNode;
import es.eucm.ead.editor.view.accessors.Accessor;
import es.eucm.ead.editor.view.accessors.IntrospectingAccessor;

/**
 * A command that changes a field-value. The most common case of command.
 */
public class ChangeFieldCommand<T> extends Command {

	public static final String ChangeField = "FieldValue";

	protected String commandName;

	/**
	 * The old value (T) to be changed.
	 */
	protected T oldValue;

	/**
	 * The new value (T) to change.
	 */
	protected T newValue;

	/**
	 * Read/write access to field to change
	 */
	protected Accessor<T> fieldDescriptor;

	/**
	 * Node that will be passed in model-events returned by this command
	 */
	protected DependencyNode[] changed;

	/**
	 * Simplified constructor
	 * 
	 * @param newValue
	 *            new value (T)
	 * @param target
	 *            where the value should be set
	 * @param fieldName
	 *            name of writable attribute in target
	 * @param changed
	 *            nodes that change when this command is done or undone
	 */
	public ChangeFieldCommand(T newValue, Object target, String fieldName,
			DependencyNode... changed) {
		this(newValue, new IntrospectingAccessor<T>(target, fieldName), changed);
	}

	/**
	 * General constructor
	 * 
	 * @param newValue
	 *            new value (T)
	 * @param fieldDescriptor
	 *            that can write values to target field
	 * @param changed
	 *            nodes that change when this command is done or undone
	 */
	public ChangeFieldCommand(T newValue, Accessor<T> fieldDescriptor,
			DependencyNode... changed) {
		this.oldValue = fieldDescriptor.read();
		this.newValue = newValue;
		this.fieldDescriptor = fieldDescriptor;
		this.commandName = ChangeField;
		this.changed = changed;
	}

	/**
	 * Method to perform a changing values command. Not having any changes is
	 * wasteful, but hardly an error.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ModelEvent performCommand(EditorModel em) {
		oldValue = fieldDescriptor.read();
		if (!isChange(oldValue, newValue)) {
			Gdx.app.error("ChangeFieldCommand", "Fix me!",
					new IllegalArgumentException(
							"They tried to set me TO THE SAME VALUE!"));
			Gdx.app.error("ChangeFieldCommand", "Redundant change: "
					+ commandName + " " + oldValue + " -> " + newValue
					+ " short-circuited");
		}
		return setValue(newValue);
	}

	/**
	 * @param one
	 * @param another
	 * @return true if any change from one to another
	 */
	public static <T> boolean defaultIsChange(T one, T another) {
		boolean validOne = (one != null);
		boolean validAnother = (another != null);

		Gdx.app.error("ChangeFieldCommand", one + " vs " + another);
		return (validOne != validAnother)
				|| (validOne && validAnother && (!one.equals(another)));
	}

	protected boolean isChange(T one, T another) {
		return defaultIsChange(one, another);
	}

	/**
	 * Sets the value and returns a mode-event describing what nodes have
	 * changed. Called by both undo() and redo().
	 * 
	 * @param value
	 * @return
	 */
	protected ModelEvent setValue(T value) {
		fieldDescriptor.write(value);
		return new ModelEvent(this, null, null, changed);
	}

	@Override
	public boolean canUndo() {
		return true;
	}

	@Override
	public boolean canRedo() {
		return true;
	}

	@Override
	public ModelEvent redoCommand(EditorModel em) {
		Gdx.app.debug("ChangeFieldCommand", "Redoing: setting value to '"
				+ newValue + "'");
		return setValue(newValue);
	}

	@Override
	public ModelEvent undoCommand(EditorModel em) {
		Gdx.app.debug("ChangeFieldCommand", "Undoing: setting value to '"
				+ newValue + "'");
		return setValue(oldValue);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public boolean combine(Command other) {
		if (other instanceof ChangeFieldCommand) {
			ChangeFieldCommand<T> o = (ChangeFieldCommand) other;
			if (fieldDescriptor.equals(o.fieldDescriptor)
					&& likesToCombine(o.newValue)) {
				newValue = o.newValue;
				timeStamp = o.timeStamp;
				Gdx.app.log("ChangeFieldCommand", "Combined command");
				return true;
			}
		} else if (other instanceof EmptyCommand) {
			// simply make it dissapear
			return true;
		}
		return false;
	}

	/**
	 * Hook for subclasses, so they can decide if they want to combine with
	 * next-in-line or not.
	 * 
	 * @param nextValue
	 *            value to combine to
	 * @return true if combination is good, false otherwise
	 */
	public boolean likesToCombine(T nextValue) {
		return true;
	}

	/**
	 * Returns the old value
	 */
	public T getOldValue() {
		return oldValue;
	}

	/**
	 * Returns the new value
	 */
	public T getNewValue() {
		return newValue;
	}

	@Override
	public String toString() {
		return commandName + ": from '" + oldValue + "' to '" + newValue
				+ "' in " + fieldDescriptor;
	}
}
