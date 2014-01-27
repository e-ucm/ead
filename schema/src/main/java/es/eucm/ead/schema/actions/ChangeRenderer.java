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
package es.eucm.ead.schema.actions;

import es.eucm.ead.schema.renderers.Renderer;

import javax.annotation.Generated;
import java.util.ArrayList;
import java.util.List;

@Generated("org.jsonschema2pojo")
public class ChangeRenderer extends Action {

	/**
	 * True if the initial renderer of the sceneelement must be restored, false
	 * if #newRenderer must be applied.
	 */
	private boolean setInitialRenderer;

	/**
	 * New renderer to be applied. Used only if #setInitialRenderer is false
	 * 
	 */
	private Renderer newRenderer;

	/**
	 * Returns the newRenderer
	 * 
	 */
	public Renderer getNewRenderer() {
		return newRenderer;
	}

	/**
	 * Sets a new renderer
	 */
	public void setNewRenderer(Renderer newRenderer) {
		this.newRenderer = newRenderer;
	}

	/**
	 * Returns true if the initial renderer of the sceneelement must be
	 * restored, false if newRenderer must be applied
	 */
	public boolean isSetInitialRenderer() {
		return setInitialRenderer;
	}

	/**
	 * Sets the setInitialRendererProperty.
	 * 
	 * @param setInitialRenderer
	 *            True if the initial renderer must be applied, false if the new
	 *            one (newRenderer) must be applied instead True is the default
	 *            value
	 */
	public void setSetInitialRenderer(boolean setInitialRenderer) {
		this.setInitialRenderer = setInitialRenderer;
	}
}
