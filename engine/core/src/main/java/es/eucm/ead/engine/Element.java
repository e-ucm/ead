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
 * Elements all aways created in a factory
 * 
 * @param <T>
 */
public interface Element<T> {

	/**
	 * This method should be called right after the element is created. Usually,
	 * it only sets the element attribute.
	 * 
	 * @param element
	 *            the element
	 */
	void setElement(T element);

	/**
	 * @return the element
	 */
	T getElement();

	/**
	 * Initialize the element, reading the given element. This method is called
	 * when all necessary resources to the initialization are loaded
	 * 
	 * @param element
	 */
	void initialize(T element);

	/**
	 * Frees the resources of the elements. It usually returns to the factory
	 * all owned poolable instances and itself
	 */
	void free();
}
