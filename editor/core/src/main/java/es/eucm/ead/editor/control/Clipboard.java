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

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.engine.Assets;

import java.util.HashMap;
import java.util.Map;

/**
 * Editor clipboard. Wraps the system clipboard.
 */
public class Clipboard {

	private com.badlogic.gdx.utils.Clipboard clipboard;

	private Views views;

	private Assets assets;

	private Class<?> contentClazz;

	private Map<Class<?>, PasteListener> pasteListeners;

	private Array<ClipboardListener> clipboardListeners;

	public Clipboard(com.badlogic.gdx.utils.Clipboard clipboard, Views views,
			Assets assets) {
		this.clipboard = clipboard;
		this.views = views;
		this.assets = assets;
		this.pasteListeners = new HashMap<Class<?>, PasteListener>();
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
	 * other {@link PasteListener} is registered for this class, it will replace
	 * by the passed pasteListener. When an object with the given class is
	 * pasted, pasteListener is invoked
	 * 
	 * @param clazz
	 *            the schema class
	 * 
	 * @param pasteListener
	 *            the paste listener
	 */
	public void registerPasteListener(Class<?> clazz,
			PasteListener pasteListener) {
		pasteListeners.put(clazz, pasteListener);
	}

	/**
	 * Execute a copy (or cut) operation over the current view with keyboard
	 * focus
	 * 
	 * @param cut
	 *            if it is a cut operation
	 */
	public void copy(boolean cut) {
		Actor a = views.getKeyboardFocus();
		if (a instanceof CopyListener) {
			Object content = ((CopyListener) a).copy(cut);
			if (content != null) {
				// Notify listeners
				for (ClipboardListener listener : clipboardListeners) {
					listener.copied(content);
				}
				clipboard.setContents(assets.toJson(content));
				contentClazz = content.getClass();
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
		if (contentClazz == null) {
			return;
		}

		Object o;
		if (contentClazz == String.class) {
			o = content;
		} else {
			o = assets.fromJson(contentClazz, content);
		}

		PasteListener pasteListener = pasteListeners.get(o.getClass());
		if (pasteListener != null) {
			pasteListener.paste(o);
		}
	}

	public interface PasteListener<T> {
		/**
		 * Executed after a paste option
		 * 
		 * @param object
		 *            the object pasted
		 */
		void paste(T object);
	}

	public interface CopyListener {
		/**
		 * The clipboard asks for a schema object to copy
		 * 
		 * @param cut
		 *            if the object should be eliminated once copied
		 * 
		 * @return the schema object
		 */
		Object copy(boolean cut);
	}

	public interface ClipboardListener {

		void copied(Object o);

	}
}
