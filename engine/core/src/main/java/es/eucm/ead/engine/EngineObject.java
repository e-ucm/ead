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

/**
 * Engine objects are elements directly manipulated by the engine, and are
 * always created by {@link Assets}. Each engine object wraps a schema object.
 * 
 * @param <T>
 *            the schema class wrapped by this engine object
 */
public interface EngineObject<T> {

	/**
	 * Sets the schema object to be represented by this engine object. This
	 * method is called right after the engine object is created. Usually, it
	 * only sets the schema object attribute.
	 * 
	 * @param schemaObject
	 *            the element
	 */
	void setSchema(T schemaObject);

	/**
	 * @return the piece of schema represented by this engine object
	 */
	T getSchema();

	/**
	 * Initializes the engine object, reading the wrapped schema object. This
	 * method is called when all necessary resources for initialization are
	 * loaded
	 * 
	 * @param schemaObject
	 *            the same schema object set by
	 *            {@link EngineObject#setSchema(Object)}
	 */
	void initialize(T schemaObject);

	/**
	 * Frees the resources used by this engine object. It usually returns all
	 * poolable instances and itself to {@link Assets}
	 */
	void dispose();

	GameLoop getGameLoop();

	void setGameLoop(GameLoop gameLoop);

}
