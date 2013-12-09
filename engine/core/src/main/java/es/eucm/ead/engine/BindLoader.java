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
package es.eucm.ead.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;

public class BindLoader {

	private Array<BindListener> bindListeners;

	public BindLoader() {
		bindListeners = new Array<BindListener>();
	}

	public void addBindListener(BindListener bindListener) {
		bindListeners.add(bindListener);
	}

	@SuppressWarnings("all")
	/**
	 * Loads binds stored in the file
	 * @param bindsFile file storing the binds
	 * @return if binds were correct   
	 */
	public boolean load(FileHandle bindsFile) {
		Json json = new Json();
		Array<Array<String>> binds = json.fromJson(Array.class, bindsFile);
		String schemaPackage = "";
		String corePackage = "";
		for (Array<String> entry : binds) {
			if (entry.get(0).contains(".")) {
				schemaPackage = entry.get(0);
				corePackage = entry.size == 1 ? null : entry.get(1);
			} else {
				try {
					Class schemaClass = ClassReflection.forName(schemaPackage
							+ "." + entry.get(0));
					Class coreClass = corePackage == null ? null
							: ClassReflection.forName(corePackage + "."
									+ entry.get(1));
					bind(entry.get(0).toLowerCase(), schemaClass, coreClass);
				} catch (ReflectionException e) {
					Gdx.app.error("LoadBinds", "Error loading binds", e);
					return false;
				}
			}
		}
		return true;
	}

	/** Call bind listeners **/
	private void bind(String alias, Class schemaClass, Class coreClass) {
		for (BindListener bindListener : bindListeners) {
			bindListener.bind(alias, schemaClass, coreClass);
		}
	}

	public interface BindListener {
		void bind(String alias, Class schemaClass, Class coreClass);
	}
}
