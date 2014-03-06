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
package es.eucm.ead.editor.control;

import com.badlogic.gdx.scenes.scene2d.Actor;
import es.eucm.ead.engine.Assets;

import java.util.HashMap;
import java.util.Map;

public class Clipboard {

	private com.badlogic.gdx.utils.Clipboard clipboard;

	private Views views;

	private Assets assets;

	private Class<?> contentClazz;

	private Map<Class<?>, PasteListener> pasteListeners;

	public Clipboard(com.badlogic.gdx.utils.Clipboard clipboard, Views views,
			Assets assets) {
		this.clipboard = clipboard;
		this.views = views;
		this.assets = assets;
		this.pasteListeners = new HashMap<Class<?>, PasteListener>();
	}

	public void registerPasteListener(Class<?> clazz,
			PasteListener pasteListener) {
		pasteListeners.put(clazz, pasteListener);
	}

	public void copy(boolean cut) {
		Actor a = views.getKeyboardFocus();
		if (a instanceof CopyListener) {
			Object content = cut ? ((CopyListener) a).cut()
					: ((CopyListener) a).copy();
			if (content != null) {
				clipboard.setContents(assets.toJson(content));
				contentClazz = content.getClass();
			}
		}
	}

	public void paste() {
		paste(clipboard.getContents());
	}

	private void paste(String content) {
		Object o = assets.fromJson(contentClazz, content);
		PasteListener pasteListener = pasteListeners.get(o.getClass());
		if (pasteListener != null) {
			pasteListener.paste(o);
		}
	}

	public interface PasteListener<T> {
		void paste(T object);
	}

	public interface CopyListener {
		Object copy();

		Object cut();
	}
}
