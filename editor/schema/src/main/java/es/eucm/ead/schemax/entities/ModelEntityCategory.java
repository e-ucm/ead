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
package es.eucm.ead.schemax.entities;

import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.GameStructure;

/**
 * Utility to identify different types of {@link ModelEntity} categories. For
 * finding the category of an entity, or if an entity belongs to a given
 * category. See:
 * <ul>
 * <li>{@link #isOfCategory(String)}</li>
 * <li>{@link #getCategoryOf(String)}</li>
 * </ul>
 * 
 * Normally, ids will be project relative paths, pointing to where entities are
 * stored. {@link #getNamePrefix()} returns a hint to create new ids of this
 * category. This prefix appended to category prefix can be used as starter to
 * create new ids, although is not mandatory. The id only requirement to belong
 * to a category is that it starts with the category prefix.
 */
public enum ModelEntityCategory implements GameStructure {

	HUD(HUDS_PATH, "hud"),

	SCENE(SCENES_PATH, "scene"),

	GAME(GAME_FILE, null);

	private String categoryPrefix;

	private String namePrefix;

	private ModelEntityCategory(String categoryPrefix, String namePrefix) {
		this.categoryPrefix = categoryPrefix;
		this.namePrefix = namePrefix;
	}

	@Override
	public String toString() {
		return getCategoryPrefix();
	}

	/**
	 * Returns the prefix for this category (e.g. "game.json" or "scenes/"). The
	 * id of all entities belonging to this category will start by this
	 * categoryPrefix (e.g. "scenes/scene0.json").
	 */
	public String getCategoryPrefix() {
		return categoryPrefix;
	}

	/**
	 * @return a hint to create new ids of this category. This prefix appended
	 *         to category prefix can be used as starter to create new ids,
	 *         although is not mandatory. The id only requirement to belong to a
	 *         category is that it starts with the category prefix
	 */
	public String getNamePrefix() {
		return namePrefix;
	}

	/**
	 * Determines if the given {@code id} is of the entity type represented by
	 * this {@link ModelEntityCategory}. Examples:
	 * 
	 * <table>
	 * <tr>
	 * <th>Example</th>
	 * <th>Returns</th>
	 * </tr>
	 * <tr>
	 * <td>
	 * ModelEntityCategory.SCENE.isOfCategory("scenes/scene3.json");</td>
	 * <td>
	 * true</td>
	 * </tr>
	 * <tr>
	 * <td>
	 * ModelEntityCategory.SCENE.isOfCategory("s3");</td>
	 * <td>
	 * false</td>
	 * </tr>
	 * <tr>
	 * <td>
	 * ModelEntityCategory.SCENE.isOfCategory("scenes/scene3.JSON");</td>
	 * <td>
	 * true</td>
	 * </tr>
	 * <tr>
	 * <td>
	 * ModelEntityCategory.GAME.isOfCategory("game.json");</td>
	 * <td>
	 * true</td>
	 * </tr>
	 * </table>
	 * 
	 * @param id
	 *            id to test. Can not be null
	 * @return {@code true} if the given {@code id} is of this type of entity,
	 *         false otherwise. The test is non-case sensitive
	 */
	public boolean isOfCategory(String id) {
		return id.startsWith(categoryPrefix);
	}

	/**
	 * Finds the category the given {@code id} belongs to.
	 * 
	 * @param id
	 *            The id that is being searched (e.g. "scenes/scene0.json",
	 *            "game.json", etc.).
	 * @return The matching category, or {@code null} if the id does not match
	 *         any {@link ModelEntityCategory}.
	 */
	public static ModelEntityCategory getCategoryOf(String id) {
		for (ModelEntityCategory category : ModelEntityCategory.values()) {
			if (category.isOfCategory(id)) {
				return category;
			}
		}
		return null;
	}
}
