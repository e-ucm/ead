package es.eucm.ead.editor.control.commands;

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

import es.eucm.ead.editor.model.events.MapEvent;
import es.eucm.ead.editor.model.events.MapEvent.Type;
import es.eucm.ead.editor.model.events.ModelEvent;

import java.util.Map;

/**
 * Contains subclasses for adding to and removing from maps
 * 
 */
public abstract class MapCommand extends Command {

	/**
	 * The map in which the elements will be placed.
	 */
	protected Map elementMap;

	protected Object newKey;
	protected Object oldKey;
	protected Object newValue;
	protected Object oldValue;

	/**
	 * Constructor for the ChangeMap class.
	 * 
	 * @param map
	 * @param value
	 * @param key
	 */
	protected MapCommand(Map<String, ?> map, Object key, Object value) {
		this.elementMap = map;
		this.newKey = key;
		this.newValue = value;
	}

	@Override
	public ModelEvent doCommand() {
		if (newValue == null) {
			// If no new value, remove
			oldKey = newKey;
			oldValue = elementMap.remove(oldKey);
			return new MapEvent(Type.ENTRY_REMOVED, elementMap, oldKey,
					oldValue);
		} else {
			// If new value, add or substitute
			oldValue = elementMap.get(newKey);
			elementMap.put(newKey, newValue);
			if (oldValue == null) {
				return new MapEvent(Type.ENTRY_ADDED, elementMap, newKey,
						newValue);
			} else {
				return new MapEvent(Type.VALUE_CHANGED, elementMap, newKey,
						newValue);
			}
		}
	}

	@Override
	public ModelEvent undoCommand() {
		if (newValue == null) {
			// It was a remove
			elementMap.put(oldKey, oldValue);
			return new MapEvent(Type.ENTRY_ADDED, elementMap, oldKey, oldValue);
		} else {
			// It was an put
			if (oldValue == null) {
				// It was a new entry, remove
				elementMap.remove(newKey);
				return new MapEvent(Type.ENTRY_REMOVED, elementMap, newKey,
						newValue);
			} else {
				// It was a substitution, recover previous value
				elementMap.put(newKey, oldValue);
				return new MapEvent(Type.VALUE_CHANGED, elementMap, newKey,
						oldValue);
			}
		}
	}

	@Override
	public boolean canUndo() {
		return true;
	}

	@Override
	public boolean combine(Command other) {
		return false;
	}

	public static class PutToMapCommand extends MapCommand {

		/**
		 * Put a key-value in the map
		 * 
		 * @param map
		 *            the value
		 * @param key
		 *            the key
		 * @param value
		 *            the value
		 */
		public PutToMapCommand(Map<String, ?> map, Object key, Object value) {
			super(map, key, value);
		}
	}

	public static class RemoveFromMapCommand extends MapCommand {

		/**
		 * Removed an ent
		 * 
		 * @param map
		 *            the map
		 * @param key
		 *            the key from the entry to remove
		 */
		public RemoveFromMapCommand(Map<String, ?> map, Object key) {
			super(map, key, null);
		}
	}

}
