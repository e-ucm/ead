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
import com.badlogic.gdx.utils.SerializationException;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;

/**
 * <p>
 * Class in charge of reading binds configuration files. The format expected by
 * this loader is:
 * </p>
 * <code>
 * [package_schema_1, package_engine_1],
 * [SchemaClass1, EngineClass1],
 * [SchemaClass2, EngineClass2],
 *   
 * ...
 *     
 * [package_schema_2, package_engine_2],
 * [SchemaClass3, EngineClass3],
 * </code>
 * <p>
 * Example:
 * </p>
 * <code>
 * [es.eucm.ead.schema.actors, es.eucm.ead.engine.actors],
 * [SceneElement, SceneElementActor]
 * </code>
 * <p>
 * This produce a bind event (transmitted to all {@link BindListener}
 * registered) relating schema class
 * {@link es.eucm.ead.schema.actors.SceneElement} with engine class
 * {@link es.eucm.ead.engine.actors.SceneElementActor}, and the alias
 * "scenelement". <strong>Alias are always the name of the class in
 * lowercase</strong>.
 * </p>
 * 
 * <p>
 * In binds, the engine part is optional. If the engine part is not specified,
 * the bind event (method {@link BindListener#bind(String, Class, Class)} is
 * only called the alias and the class. Example:
 * </p>
 * <code>
 * [es.eucm.ead.schema.behaviors],
 * [Behavior]
 * </code> This produces a bind event relating the schema class
 * {@link es.eucm.ead.schema.behaviors.Behavior} to the alias "behavior"
 * 
 */
public class BindLoader {

	private Array<BindListener> bindListeners;

	private Json json;

	public BindLoader() {
		bindListeners = new Array<BindListener>();
		json = new Json();
	}

	public void addBindListener(BindListener bindListener) {
		bindListeners.add(bindListener);
	}

	@SuppressWarnings("all")
	/**
	 * Loads binds stored in the file
	 * @param bindsFile file storing the binds
	 * @return if the binds loading was completely correct. It might fail if the the file is not a valid or a non existing or invalid class is found
	 */
	public boolean load(FileHandle bindsFile) {
		try {
			Array<Array<String>> binds = json.fromJson(Array.class, bindsFile);
			load(binds);
		} catch (SerializationException e) {
			Gdx.app.error("BindLoader", bindsFile.path()
					+ " doesn't contain a valid binds file");
			return false;
		}
		return true;
	}

	@SuppressWarnings("all")
	/**
	 * Loads binds represented in the given string
	 * @param bindsString a string representing a json with the binds
	 * @return if the binds loading was completely correct. It might fail if the the file is not a valid or a non existing or invalid class is found
	 */
	public boolean load(String bindsString) {
		try {
			Array<Array<String>> binds = json
					.fromJson(Array.class, bindsString);
			load(binds);
		} catch (SerializationException e) {
			Gdx.app.error("BindLoader", bindsString
					+ " is not a valid binds string");
			return false;
		}
		return true;
	}

	private boolean load(Array<Array<String>> binds) {
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
					Class coreClass = null;
					if (entry.size == 2) {
						coreClass = corePackage == null ? null
								: ClassReflection.forName(corePackage + "."
										+ entry.get(1));
					}
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

	/**
	 * Removes the given bind listener
	 * 
	 * @param bindListener
	 *            the bind listener to remove
	 * @return true if in fact the bind listener was contained by the bind
	 *         listeners list
	 */
	public boolean removeBindListener(BindListener bindListener) {
		return bindListeners.removeValue(bindListener, true);
	}

	public interface BindListener {
		void bind(String alias, Class schemaClass, Class coreClass);
	}
}
