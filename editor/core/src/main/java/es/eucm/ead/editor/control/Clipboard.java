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

import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.control.Selection.Context;
import es.eucm.ead.editor.control.actions.model.SetSelection;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.engine.assets.Assets;

import java.util.HashMap;
import java.util.Map;

/**
 * Editor clipboard. Wraps the system clipboard.
 */
public class Clipboard {

	private com.badlogic.gdx.utils.Clipboard clipboard;

	private Controller controller;

	private Model model;

	private Assets assets;

	private Map<Class<?>, CopyListener> copyListeners;

	private Array<ClipboardListener> clipboardListeners;

	public Clipboard(com.badlogic.gdx.utils.Clipboard clipboard,
			Controller controller, Assets assets) {
		this.clipboard = clipboard;
		this.controller = controller;
		this.model = controller.getModel();
		this.assets = assets;
		this.copyListeners = new HashMap<Class<?>, CopyListener>();
		this.clipboardListeners = new Array<ClipboardListener>();
	}

	/**
	 * Adds a clipboard listener. Listener will be notified when the clipboard
	 * state changes
	 * 
	 * @param listener
	 *            the listener
	 */
	public void addClipboardListener(ClipboardListener listener) {
		clipboardListeners.add(listener);
	}

	/**
	 * Register a paste listener for a concrete class of the schema. If any
	 * other {@link es.eucm.ead.editor.control.Clipboard.CopyListener} is
	 * registered for this class, it will be replace by the passed copyListener.
	 * When an object with the given class is pasted, copyListener is invoked
	 * 
	 * @param clazz
	 *            the schema class
	 * 
	 * @param copyListener
	 *            the paste listener
	 */
	public void registerCopyListener(Class<?> clazz, CopyListener copyListener) {
		copyListeners.put(clazz, copyListener);
	}

	/**
	 * Execute a copy (or cut) operation over the current view with keyboard
	 * focus
	 * 
	 * @param cut
	 *            if it is a cut operation
	 */
	public void copy(boolean cut) {
		Object[] selection = model.getSelection().getCurrent();
		if (selection != null && selection.length > 0) {
			if (cut) {
				for (Object object : selection) {
					CopyListener copyListener = copyListeners.get(object
							.getClass());
					if (copyListener != null) {
						copyListener.cut(object);
					}
				}
			}

			clipboard.setContents(assets.toJson(selection));

			if (cut) {
				// Clear the selection
				Context currentContext = model.getSelection()
						.getCurrentContext();
				controller.action(SetSelection.class,
						currentContext.getParentId(), currentContext.getId());
			}
			for (ClipboardListener listener : clipboardListeners) {
				listener.clipboardChanged(getContents());
			}
		}
	}

	/**
	 * Executes a paste operation
	 */
	public void paste() {
		paste(clipboard.getContents());
	}

	private void paste(String content) {
		Array contents = assets.fromJson(Array.class, content);
		for (Object o : contents) {
			CopyListener copyListener = copyListeners.get(o.getClass());
			if (copyListener != null) {
				copyListener.paste(o);
			}
		}
	}

	/**
	 * @return clipboard contents
	 */
	public String getContents() {
		return clipboard.getContents();
	}

	public interface CopyListener<T> {

		/**
		 * The given object has been cut
		 */
		void cut(T object);

		/**
		 * The given object has been pasted
		 */
		void paste(T object);
	}

	public interface ClipboardListener {

		/**
		 * Clipboard content changed
		 */
		void clipboardChanged(String clibpoardContent);

	}
}
