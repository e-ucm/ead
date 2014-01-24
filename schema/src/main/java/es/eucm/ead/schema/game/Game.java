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
package es.eucm.ead.schema.game;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import es.eucm.ead.schema.components.VariableDef;

/**
 * An eAdventure game. A game has a width a height that sets how much space the
 * camera shows, and the name of the first scene to be loaded once the game is
 * launched
 * 
 */
@Generated("org.jsonschema2pojo")
public class Game {

	/**
	 * Game title
	 * 
	 */
	private String title;
	/**
	 * Name of the initial scene of the game
	 * 
	 */
	private String initialScene;
	/**
	 * Game's width (in game units). This height sets how much horizontal space
	 * the camera shows
	 * 
	 */
	private int width;
	/**
	 * Game's height (in game units). This height sets how much vertical space
	 * the camera shows
	 * 
	 */
	private int height;
	/**
	 * Variables in the game.
	 * 
	 */
	private List<VariableDef> variables = new ArrayList<VariableDef>();

	/**
	 * Game title
	 * 
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Game title
	 * 
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Name of the initial scene of the game
	 * 
	 */
	public String getInitialScene() {
		return initialScene;
	}

	/**
	 * Name of the initial scene of the game
	 * 
	 */
	public void setInitialScene(String initialScene) {
		this.initialScene = initialScene;
	}

	/**
	 * Game's width (in game units). This height sets how much horizontal space
	 * the camera shows
	 * 
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Game's width (in game units). This height sets how much horizontal space
	 * the camera shows
	 * 
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * Game's height (in game units). This height sets how much vertical space
	 * the camera shows
	 * 
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Game's height (in game units). This height sets how much vertical space
	 * the camera shows
	 * 
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * Variables in the game.
	 * 
	 */
	public List<VariableDef> getVariables() {
		return variables;
	}

	/**
	 * Variables in the game.
	 * 
	 */
	public void setVariables(List<VariableDef> variables) {
		this.variables = variables;
	}

}
