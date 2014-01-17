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
 * Class in charge of reading bindings configuration files (bindings.json). The
 * format expected by this loader is:
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
 * This produce a binding event (transmitted to all
 * {@link es.eucm.ead.engine.BindingsLoader.BindingListener} registered)
 * relating schema class {@link es.eucm.ead.schema.actors.SceneElement} with
 * engine class {@link es.eucm.ead.engine.actors.SceneElementActor}, and the
 * alias "sceneelement". <strong>Alias are always the name of the class in
 * lowercase</strong>.
 * </p>
 * 
 * <p>
 * In bindings, the engine class/package is optional. If the engine class is not
 * specified, the binding event (method
 * {@link es.eucm.ead.engine.BindingsLoader.BindingListener#bind(String, Class, Class)}
 * is only called with the alias and the class. Example:
 * </p>
 * <code>
 * [es.eucm.ead.schema.behaviors],
 * [Behavior]
 * </code> This produces a binding event relating the schema class
 * {@link es.eucm.ead.schema.behaviors.Behavior} to the alias "behavior"
 * 
 */
public class BindingsLoader {

	private Array<BindingListener> bindingListeners;

	private Json json;

	public BindingsLoader() {
		bindingListeners = new Array<BindingListener>();
		json = new Json();
	}

	public void addBindingListener(BindingListener bindingListener) {
		bindingListeners.add(bindingListener);
	}

	@SuppressWarnings("all")
	/**
	 * Loads bindings stored in the file
	 * @param bindingsFile file storing the bindings
	 * @return if the bindings loading was completely correct. It might fail if the the file is not a valid or a non existing or invalid class is found
	 */
	public boolean load(FileHandle bindingsFile) {
		try {
			Array<Array<String>> bindings = json.fromJson(Array.class,
					bindingsFile);
			load(bindings);
		} catch (SerializationException e) {
			Gdx.app.error("BindingsLoader", bindingsFile.path()
					+ " does not contain a valid bindings file", e);
			return false;
		}
		return true;
	}

	@SuppressWarnings("all")
	/**
	 * Loads bindings represented in the given string
	 * @param bindingsString a string representing a json with bindings
	 * @return if the bindings load was completely correct. It might fail if the the file is invalid JSON or contains an invalid class
	 */
	public boolean load(String bindingsString) {
		try {
			Array<Array<String>> bindings = json.fromJson(Array.class,
					bindingsString);
			load(bindings);
		} catch (SerializationException e) {
			Gdx.app.error("BindingsLoader", bindingsString
					+ " is not a valid string representing bindings.");
			return false;
		}
		return true;
	}

	private boolean load(Array<Array<String>> bindings) {
		String schemaPackage = "";
		String corePackage = "";
		for (Array<String> entry : bindings) {
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
					Gdx.app
							.error("BindingsLoader", "Error loading bindings",
									e);
					return false;
				}
			}
		}
		return true;
	}

	/** Call binding listeners **/
	private void bind(String alias, Class schemaClass, Class coreClass) {
		for (BindingListener bindingListener : bindingListeners) {
			bindingListener.bind(alias, schemaClass, coreClass);
		}
	}

	/**
	 * Removes the given binding listener
	 * 
	 * @param bindingListener
	 *            the bind listener to remove
	 * @return true if in fact the binding listener was contained by the binding
	 *         loader
	 */
	public boolean removeBindingListener(BindingListener bindingListener) {
		return bindingListeners.removeValue(bindingListener, true);
	}

	public interface BindingListener {
		/**
		 * Emits a binding event
		 * 
		 * @param alias
		 *            the alias of the schema class (usually the name of the
		 *            schema class in lowercase)
		 * @param schemaClass
		 *            the schema class
		 * @param engineClass
		 *            the engine class that must wrap the schema class
		 */
		void bind(String alias, Class schemaClass, Class engineClass);
	}
}
