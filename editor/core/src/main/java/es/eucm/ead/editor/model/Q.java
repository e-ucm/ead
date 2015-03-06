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
package es.eucm.ead.editor.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.engine.assets.Assets.AssetLoadedCallback;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.schema.components.ModelComponent;
import es.eucm.ead.schema.components.tweens.AlphaTween;
import es.eucm.ead.schema.components.tweens.BaseTween;
import es.eucm.ead.schema.components.tweens.MoveTween;
import es.eucm.ead.schema.components.tweens.RotateTween;
import es.eucm.ead.schema.components.tweens.ScaleTween;
import es.eucm.ead.schema.components.tweens.Timeline;
import es.eucm.ead.schema.components.tweens.Timeline.Mode;
import es.eucm.ead.schema.components.tweens.Tween;
import es.eucm.ead.schema.editor.components.Date;
import es.eucm.ead.schema.editor.components.Documentation;
import es.eucm.ead.schema.editor.components.GameData;
import es.eucm.ead.schema.editor.components.Parent;
import es.eucm.ead.schema.editor.components.SceneMap;
import es.eucm.ead.schema.editor.components.Thumbnail;
import es.eucm.ead.schema.editor.components.repo.RepoElement;
import es.eucm.ead.schema.editor.data.Cell;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schema.renderers.Image;
import es.eucm.ead.schemax.ModelStructure;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class with statics methods to query parts of the model
 */
public class Q {

	private static Controller controller;

	private static final Color color = new Color();

	private static I18N i18N;

	private static Pattern pattern;

	public static void setController(Controller controller) {
		Q.controller = controller;
		Q.i18N = controller.getApplicationAssets().getI18N();
	}

	/**
	 * Returns the component for the class. If the element has no component of
	 * the given type, is automatically created and added to it.
	 * 
	 * @param element
	 *            the element with the component
	 * @param componentClass
	 *            the component class
	 * @return the component inside the element
	 */
	@SuppressWarnings("unchecked")
	public static <T extends ModelComponent> T getComponent(
			ModelEntity element, Class<T> componentClass) {
		if (element == null) {
			return null;
		}
		for (ModelComponent component : element.getComponents()) {
			if (component.getClass() == componentClass) {
				return (T) component;
			}
		}
		try {
			ModelComponent component = ClassReflection
					.newInstance(componentClass);
			element.getComponents().add(component);
			return (T) component;
		} catch (ReflectionException e) {
			Gdx.app.error("Model",
					"Error creating component " + componentClass, e);
		}
		return null;
	}

	/**
	 * Iterates through the model entity hierarchy to recover its root ancestor
	 */
	public static ModelEntity getRootAncestor(ModelEntity modelEntity) {
		if (modelEntity == null) {
			return null;
		}

		ModelEntity rootEntity = modelEntity;
		while (true) {
			Parent parent = getComponent(rootEntity, Parent.class);
			if (parent != null && parent.getParent() != null) {
				rootEntity = parent.getParent();
			} else {
				break;
			}
		}
		return rootEntity;
	}

	/**
	 * Returns the {@link Documentation#name} attribute of the model entity. Can
	 * be null
	 */
	public static String getTitle(ModelEntity modelEntity) {
		return getTitle(modelEntity, null);
	}

	/**
	 * Returns the {@link Documentation#name} attribute of the model entity. If
	 * null, default value is returned
	 */
	public static String getTitle(ModelEntity modelEntity, String defaultValue) {
		if (modelEntity == null) {
			return defaultValue;
		}
		String title = getComponent(modelEntity, Documentation.class).getName();
		return title == null || title.isEmpty() ? defaultValue : title;
	}

	/**
	 * @return whether the given element contains a component with the given
	 *         class
	 */
	public static <T extends ModelComponent> boolean hasComponent(
			ModelEntity element, Class<T> componentClass) {
		if (element == null) {
			return false;
		}

		for (ModelComponent component : element.getComponents()) {
			if (component.getClass() == componentClass) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @return whether the given element contains a component that is an
	 *         {@link Image} or not.
	 */
	public static boolean hasImage(ModelEntity element) {
		return hasComponent(element, Image.class);
	}

	/**
	 * @return the model entity associated to the given actor. Returns
	 *         {@code null} if no model entity is associated to the actor
	 */
	public static ModelEntity getModelEntity(Actor actor) {
		Object o = actor.getUserObject();
		if (o instanceof EngineEntity) {
			return ((EngineEntity) o).getModelEntity();
		}
		return null;
	}

	/**
	 * @return returns the first object of given class in the iterable
	 */
	public static <T> T getObjectOfClass(Iterable iterable, Class<T> clazz) {
		for (Object o : iterable) {
			if (o.getClass() == clazz) {
				return (T) o;
			}
		}
		return null;
	}

	/**
	 * Creates a cell with the given id in the first empty space found in the
	 * map and returns the cell. If there is no empty space a null will be
	 * returned. Note that the cell is not added to the map. It's responsibility
	 * of the developer to add the returned cell to the map if it isn't null.
	 * 
	 * @param id
	 * @param sceneMap
	 * @return the new {@link Cell} or null if no empty space was available.
	 */
	public static Cell createCell(String id, SceneMap sceneMap) {

		Array<Cell> cells = sceneMap.getCells();
		int columns = sceneMap.getColumns();
		int rows = sceneMap.getRows();

		for (int i = 0; i < rows; ++i) {
			for (int j = 0; j < columns; ++j) {
				if (Q.cellInCoords(i, j, cells) == null) {
					Cell cell = new Cell();
					cell.setSceneId(id);
					cell.setColumn(j);
					cell.setRow(i);
					return cell;
				}
			}
		}

		return null;
	}

	/**
	 * @param row
	 * @param column
	 * @param cells
	 * @return the {@link Cell} with the given coordinates or null if there is
	 *         none.
	 */
	public static Cell cellInCoords(int row, int column, Array<Cell> cells) {
		for (Cell cell : cells) {
			if (cell.getRow() == row && cell.getColumn() == column) {
				return cell;
			}
		}
		return null;
	}

	/**
	 * @param sceneId
	 * @param cells
	 * @return the {@link Cell} with the given sceneId or null if there is none.
	 */
	public static Cell getCellFromId(String sceneId, Array<Cell> cells) {
		for (int i = 0; i < cells.size; ++i) {
			Cell cell = cells.get(i);
			if (sceneId.equals(cell.getSceneId())) {
				return cell;
			}
		}
		return null;
	}

	public static void getThumbnailTexture(ModelEntity entity,
			AssetLoadedCallback<Texture> callback) {
		if (hasComponent(entity, Thumbnail.class)) {
			String path = getComponent(entity, Thumbnail.class).getPath();
			if (path != null) {
				controller.getEditorGameAssets().get(path, Texture.class,
						callback);
			}
		}
	}

	public static String getThumbnailPath(ModelEntity scene) {
		return Q.getComponent(scene, Thumbnail.class).getPath();
	}

	/**
	 * Returns a new Entity positioned in the center of the device
	 */
	public static ModelEntity createCenteredEntity(float originX,
			float originY, ModelComponent component) {
		GameData gameData = getComponent(controller.getModel().getGame(),
				GameData.class);

		ModelEntity entity = controller.getTemplates().createSceneElement();

		entity.setX(entity.getX() + gameData.getWidth() * .5f - originX);
		entity.setY(entity.getY() + gameData.getHeight() * .5f - originY);
		entity.setOriginX(originX);
		entity.setOriginY(originY);

		entity.getComponents().add(component);

		return entity;
	}

	/**
	 * Create an scene with the current date
	 */
	public static ModelEntity createScene() {
		ModelEntity scene = new ModelEntity();
		Q.getComponent(scene, Date.class).setDate(
				System.currentTimeMillis() + "");
		return scene;
	}

	public static ModelComponent getComponentById(ModelEntity sceneElement,
			String componentId) {
		for (ModelComponent component : sceneElement.getComponents()) {
			if (componentId.equals(component.getId())) {
				return component;
			}
		}
		return null;
	}

	public static String getDate(ModelEntity entity) {
		Date date = Q.getComponent(entity, Date.class);
		if (date != null) {
			return date.getDate();
		}
		return null;
	}

	public static String getRepoElementName(RepoElement elem) {
		I18N i18N = controller.getApplicationAssets().getI18N();
		Array<String> nameI18nList = elem.getNameI18nList();
		int langIdx = nameI18nList.indexOf(i18N.getLang(), false);
		if (langIdx < 0) {
			langIdx = 0;
		}

		String name = null;
		Array<String> nameList = elem.getNameList();
		if (nameList.size > langIdx) {
			name = nameList.get(langIdx);
		} else if (nameList.size > 0) {
			name = nameList.first();
		}
		if (name == null || name.trim().isEmpty()) {
			name = i18N.m("untitled");
		}
		return name;
	}

	public static String getRepoElementThumbnailUrl(RepoElement elem) {
		return elem.getThumbnailUrl();
	}

	public static Color toLibgdxColor(es.eucm.ead.schema.data.Color modelColor) {
		color.a = modelColor.getA();
		color.r = modelColor.getR();
		color.g = modelColor.getG();
		color.b = modelColor.getB();

		return color;
	}

	public static boolean isGroup(ModelEntity sceneElement) {
		return sceneElement != null && sceneElement.getChildren().size > 0;
	}

	public static float calculateTimelineDuration(Timeline timeline) {
		float time = 0;
		if (timeline.getMode().equals(Mode.PARALLEL)) {
			for (BaseTween tween : timeline.getChildren()) {
				if (tween instanceof Tween) {
					time = Math.max(time, ((Tween) tween).getDuration());
				} else {
					time = Math.max(time,
							calculateTimelineDuration((Timeline) tween));
				}
			}
		} else {
			for (BaseTween tween : timeline.getChildren()) {
				if (tween instanceof Tween) {
					time += ((Tween) tween).getDuration();
				} else {
					time += calculateTimelineDuration((Timeline) tween);
				}
			}
		}
		return time;
	}

	public static String getThumbnailPath(String resourceId) {
		return ModelStructure.THUMBNAILS_PATH + resourceId + ".png";
	}

	public static float calculateTimelineAmount(BaseTween baseTween) {
		float amount = 0;
		if (baseTween instanceof Timeline) {
			for (BaseTween tween : ((Timeline) baseTween).getChildren()) {
				amount += calculateTimelineAmount(tween);
			}
		} else {
			if (baseTween instanceof AlphaTween) {
				amount += Math.abs(((AlphaTween) baseTween).getAlpha());
			} else if (baseTween instanceof MoveTween) {
				amount += Math.abs(((MoveTween) baseTween).getX());
				amount += Math.abs(((MoveTween) baseTween).getY());
			} else if (baseTween instanceof ScaleTween) {
				amount += Math.abs(((ScaleTween) baseTween).getScaleX());
				amount += Math.abs(((ScaleTween) baseTween).getScaleY());
			} else if (baseTween instanceof RotateTween) {
				amount += Math.abs(((RotateTween) baseTween).getRotation());
			}
		}
		return amount;
	}

	public static void setTitle(ModelEntity entity, String title) {
		Q.getComponent(entity, Documentation.class).setName(title);
	}

	public static float getGameHeight() {
		return Q.getComponent(controller.getModel().getGame(), GameData.class)
				.getHeight();
	}

	public static float getGameWidth() {
		return Q.getComponent(controller.getModel().getGame(), GameData.class)
				.getWidth();
	}

	/**
	 * Builds a copy name from the given name, that is not present the passed
	 * list.
	 * 
	 * @return null if name is null or the empty string.
	 */
	public static String buildCopyName(String name, Array<String> existingNames) {
		if (name != null && !name.isEmpty()) {
			String copySuffix = i18N.m("copy_suffix");

			if (pattern == null) {
				String regex = ".*\\(" + copySuffix + "\\s?(\\d*)\\)$";
				pattern = Pattern.compile(regex);
			}

			String prefix;
			int count;
			Matcher matcher = pattern.matcher(name);
			if (matcher.find()) {
				int start = matcher.start(1);
				String number = matcher.group(1);
				count = number.isEmpty() ? 2 : (Integer.parseInt(number));
				prefix = name.substring(0, start);
			} else {
				prefix = name + " (" + copySuffix + " ";
				count = 1;
			}

			String copyName;
			do {
				copyName = prefix + count++ + ")";
			} while (existingNames.contains(copyName, false));
			return copyName;
		}
		return null;
	}
}
