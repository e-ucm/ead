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

import java.util.Iterator;
import java.util.regex.Pattern;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.GameStructure;

/**
 * Utility to identify different types of {@link ModelEntity} categories. Also
 * provides convenient object and class methods related to entity categories:
 * 
 * <ul>
 * <li>For getting relative paths for entities of a given category. See:
 * <ul>
 * <li>{@link #getRelativePath()}</li>
 * <li>{@link #getRelativeEntityPath(String)}</li>
 * <li>{@link #getRelativeEntityPath()}</li>
 * <li>{@link #getRelativePathOf(String)}</li>
 * </ul>
 * </li>
 * <li>For finding the category of an entity, or if an entity belongs to a given
 * category. See:
 * <ul>
 * <li>{@link #isOfCategory(String)}</li>
 * <li>{@link #getCategoryOf(String)}</li>
 * </ul>
 * </li>
 * <li>For building new Ids for a given {@code ModelEntityCategory}. See
 * {@link #getIdIterator()}</li>
 * </ul>
 * 
 * Each {@code ModelEntityCategory} has also the next properties that can be
 * accessed (read-only):
 * <ul>
 * <li>{@link #getCategoryName()}. The name of the category (e.g. "game" or
 * "scene"), which is used to define valid ids for entities of this type.</li>
 * <li>{@link #getGreedy()}. The number of entities of this category that a game
 * can have. It can take values {@link #GREEDY_ONE} (e.g. "game") or
 * {@link #GREEDY_MANY} (e.g. "scene").</li>
 * <li>{@link #getRelativePath()}. The relative path for the directory that
 * stores all the entities of this type in the game directory. Examples: "" for
 * {@link #GAME}, "scenes/" for {@link #SCENE}.</li>
 * </ul>
 */
public enum ModelEntityCategory implements GameStructure {

	SCENE("scene", SCENES_PATH, getGreedyMany()),

	GAME("game", "", getGreedyOne());

	/**
	 * Indicates that this entity type can appear only once in the game project.
	 */
	public static final int GREEDY_ONE = 1;

	/**
	 * This entity type can appear one or more times in the game project.
	 */
	public static final int GREEDY_MANY = Integer.MAX_VALUE;

	private String categoryName;
	private Pattern regex;
	private String relativePath;
	private int greedy;

	private ModelEntityCategory(String categoryName, String relativePath,
			int greedy) {
		this.categoryName = categoryName;
		this.relativePath = toCanonicalPath(relativePath);
		this.greedy = greedy;
		buildRegex();
	}

	private String toCanonicalPath(String path) {
		return path == null ? null : path.replaceAll("\\\\", "/");
	}

	/*
	 * Builds the regex for the category. GREEDY_ONE -> categoryName(.json)?
	 * GREEDY_MANY -> categoryNameXXX(.json)? where XXX is an integer
	 */
	private void buildRegex() {
		String jsonRegexString = getJsonRegexString();
		String greedyPart = "";
		if (greedy == GREEDY_MANY) {
			greedyPart = "\\d+";
		}
		String optionalJsonPart = "(" + jsonRegexString + ")?";
		String regexString = categoryName + greedyPart + optionalJsonPart;
		this.regex = Pattern.compile(regexString);
	}

	@Override
	public String toString() {
		return getCategoryName();
	}

	/**
	 * Returns the name for this category (e.g. "game" or "scene"). The id of
	 * all entities belonging to this category will start by this categoryName
	 * (e.g. "game", "scene0").
	 */
	public String getCategoryName() {
		return categoryName;
	}

	/**
	 * Returns the number of entities of this category a game can have. Possible
	 * values: {@link #GREEDY_ONE}: appears only once. Example: {@link #GAME}
	 * {@link #GREEDY_MANY}: appears once or many times. Example: {@link #SCENE}
	 */
	public int getGreedy() {
		return greedy;
	}

	/**
	 * Returns the relative path for the main directory that stores all entities
	 * of this category in the game project folder. Examples:
	 * 
	 * "" for {@link #GAME} (root dir) "scenes/" for {@link #SCENE}
	 */
	public String getRelativePath() {
		return relativePath;
	}

	/**
	 * Returns the relative path for the file that stores the entity with the
	 * given {@code entityId} in the game project folder, including json
	 * extension.
	 * 
	 * Note: this method does not check that the provided {@code entityId} is
	 * valid, just appends it.
	 * 
	 * @param entityId
	 *            The id of the entity which relative path is being resolved
	 * @return The relative path including json extension. Examples.
	 *         "game.json", "scenes/scene0.json"
	 */
	public String getRelativeEntityPath(String entityId) {
		String entityIdRelativePath;
		entityId = toCanonicalPath(entityId);
		if (entityId.startsWith("/"))
			entityId = entityId.substring(1, entityId.length());

		entityIdRelativePath = relativePath + entityId;
		if (!entityIdRelativePath.toLowerCase().endsWith(
				JSON_EXTENSION.toLowerCase())) {
			entityIdRelativePath += JSON_EXTENSION;
		}
		return entityIdRelativePath;
	}

	/**
	 * Returns the relative path of the single entity of this category. Can only
	 * be invoked in categories where {@link #getGreedy()} is
	 * {@link #GREEDY_ONE}.
	 * 
	 * @return The relative path for the single entity. Example: "game.json"
	 * @throws java.lang.UnsupportedOperationException
	 *             if this category may contain more than one entity.
	 */
	public String getRelativeEntityPath() {
		if (greedy != GREEDY_ONE) {
			throw new UnsupportedOperationException(
					"The method getRelativeEntityPath() is only available for types of entities that can appear only once.");
		}
		return getRelativeEntityPath(categoryName);
	}

	/**
	 * Determines if the given {@code id} is of the entity type represented by
	 * this {@link ModelEntityCategory}.
	 * 
	 * Internally uses a regex that is associated to each
	 * {@link ModelEntityCategory}.
	 * 
	 * Examples:
	 * 
	 * <table>
	 * <tr>
	 * <th>Example</th>
	 * <th>Returns</th>
	 * </tr>
	 * <tr>
	 * <td>
	 * ModelEntityCategory.SCENE.isOfCategory("scene3");</td>
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
	 * ModelEntityCategory.SCENE.isOfCategory("scene3.JSON");</td>
	 * <td>
	 * true</td>
	 * </tr>
	 * <tr>
	 * <td>
	 * ModelEntityCategory.GAME.isOfCategory("game");</td>
	 * <td>
	 * true</td>
	 * </tr>
	 * </table>
	 * 
	 * @return {@code true} if the given {@code id} is of this type of entity,
	 *         false otherwise. The test is non-case sensitive
	 * @throws NullPointerException
	 *             if {@code id} is null
	 */
	public boolean isOfCategory(String id) {
		if (id == null) {
			throw new NullPointerException(
					"ModelEntityCategory.isOfCategory(String) does not accept nulls");
		}
		return regex.matcher(id.toLowerCase()).matches();
	}

	/**
	 * Returns an {@link Iterator} for getting valid ids for entities of this
	 * category. Convenient for creating new entities without manipulating ids.
	 */
	public Iterator<String> getIdIterator() {
		return new IdIterator();
	}

	/**
	 * Resolves the relative path in the game folder for the entity with the
	 * given {@code id}. First, it determines which category this id belongs to.
	 * For example, if {@code id} is "scene0" this method will assume it is a
	 * scene and returns "scenes/scene0.json".
	 * 
	 * @param id
	 *            The id of the {@link ModelEntity} whose relative path is to be
	 *            found (e.g. "scene0"). Note: the validity of the id given is
	 *            not checked.
	 * @return The relative path for the given {@code id} (e.g.
	 *         scenes/scene0.json). Returns {@code null} if the id does not
	 *         correspond to any known {@code ModelEntity}.
	 */
	public static String getRelativePathOf(String id) {
		for (ModelEntityCategory category : ModelEntityCategory.values()) {
			if (category.isOfCategory(id)) {
				return category.getRelativeEntityPath(id);
			}
		}
		return null;
	}

	/**
	 * Finds the category the given {@code id} belongs to.
	 * 
	 * @param id
	 *            The id that is being searched (e.g. "scene0", "game.json",
	 *            etc.).
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

	private static int getGreedyOne() {
		return GREEDY_ONE;
	}

	private static int getGreedyMany() {
		return GREEDY_ONE;
	}

	private static String getJsonRegexString() {
		return Pattern.quote(GameStructure.JSON_EXTENSION);
	}

	public class IdIterator implements Iterator<String> {

		private int counter = 0;

		@Override
		public boolean hasNext() {
			return counter < greedy;
		}

		@Override
		public String next() {
			String nextId = null;
			if (greedy == GREEDY_ONE) {
				nextId = categoryName;
			} else if (greedy == GREEDY_MANY) {
				nextId = categoryName + counter;
			}
			counter++;
			return nextId;
		}

		@Override
		public void remove() {
		}
	};

}
