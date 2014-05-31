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
package es.eucm.ead.editor.control;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.utils.UIUtils;
import es.eucm.ead.editor.control.actions.Action;

import java.util.HashMap;
import java.util.Map;

/**
 * Manage keystroke binding to editor's functionality.
 */
public class ShortcutsMap {

	private Controller controller;

	private Map<Shortcut, ShortcutAction> shortcuts;

	private Shortcut tempShortcut = new Shortcut(false, false, false, 0);

	public ShortcutsMap(Controller controller) {
		this.controller = controller;
		shortcuts = new HashMap<Shortcut, ShortcutAction>();
	}

	/**
	 * Registers a shortcut that is fired when ctrl/command and the given
	 * keycode are pressed
	 */
	public <T extends Action> void registerShortcutCtrl(int keycode,
			Class<T> actionClass, Object... args) {
		registerShortcut(true, false, false, keycode, actionClass, args);
	}

	/**
	 * Registers a shortcut that is fired when alt and the given keycode are
	 * pressed
	 */
	public <T extends Action> void registerShortcutAlt(int keycode,
			Class<T> actionClass, Object... args) {
		registerShortcut(false, true, false, keycode, actionClass, args);
	}

	/**
	 * Registers a shortcut that is fired when the given keycode is pressed
	 */
	public <T extends Action> void registerShortcutKey(int keycode,
			Class<T> actionClass, Object... args) {
		registerShortcut(false, false, false, keycode, actionClass, args);
	}

	/**
	 * Registers a shortcut
	 * 
	 * @param ctrl
	 *            if ctrl/command must be pressed
	 * @param alt
	 *            if alt must be pressed
	 * @param shift
	 *            if shift must be pressed
	 * @param keycode
	 *            if keycode must be pressed. Keycode is a constant in
	 *            {@link com.badlogic.gdx.Input.Keys}.
	 * @param actionClass
	 *            the action to launch when the shortcut is fired
	 * @param args
	 *            the arguments for the action
	 */
	public <T extends Action> void registerShortcut(boolean ctrl, boolean alt,
			boolean shift, int keycode, Class<T> actionClass, Object... args) {
		shortcuts.put(new Shortcut(ctrl, alt, shift, keycode),
				new ShortcutAction(actionClass, args));
	}

	/**
	 * Fire the shortcut of the given code. Ctrl/Command, alt and shift are
	 * automatically added by the method.
	 * 
	 * @return if a shortcutr was fired
	 */
	public boolean shortcut(int keycode) {
		tempShortcut.ctrl = UIUtils.ctrl();
		tempShortcut.alt = UIUtils.alt();
		tempShortcut.shift = UIUtils.shift();
		tempShortcut.keycode = keycode;

		Gdx.app.debug("ShortcutsMap", "Shortcut: " + tempShortcut);

		ShortcutAction a = shortcuts.get(tempShortcut);
		if (a != null) {
			controller.action(a.action, a.args);
			return true;
		}
		return false;
	}

	private String toString(boolean ctrl, boolean alt, boolean shift,
			int keycode) {
		String result = "";
		if (ctrl) {
			result += "c+";
		}

		if (alt) {
			result += "a+";
		}

		if (shift) {
			result += "s+";
		}

		result += keycode;
		return result;
	}

	/**
	 * Represents a keyboard shortcut
	 */
	public class Shortcut {

		private boolean ctrl;

		private boolean alt;

		private boolean shift;

		private int keycode;

		public Shortcut(boolean ctrl, boolean alt, boolean shift, int keycode) {
			this.ctrl = ctrl;
			this.alt = alt;
			this.shift = shift;
			this.keycode = keycode;
		}

		public boolean isAlt() {
			return alt;
		}

		public boolean isCtrl() {
			return ctrl;
		}

		public int getKeycode() {
			return keycode;
		}

		public boolean isShift() {
			return shift;
		}

		public String toString() {
			return ShortcutsMap.this.toString(ctrl, alt, shift, keycode);
		}

		public int hashCode() {
			return toString().hashCode();
		}

		public boolean equals(Object o) {
			return toString().equals(o.toString());
		}
	}

	private static class ShortcutAction {

		Class action;
		Object[] args;

		private ShortcutAction(Class action, Object[] args) {
			this.action = action;
			this.args = args;
		}
	}

}
