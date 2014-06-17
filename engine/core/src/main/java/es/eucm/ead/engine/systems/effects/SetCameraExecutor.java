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
package es.eucm.ead.engine.systems.effects;

import ashley.core.Entity;
import ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.IntMap;
import es.eucm.ead.engine.GameView;
import es.eucm.ead.engine.components.StaticCamerasComponent;
import es.eucm.ead.engine.components.TweensComponent;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.engine.variables.VariablesManager;
import es.eucm.ead.engine.variables.VarsContext;
import es.eucm.ead.schema.components.cameras.Camera;
import es.eucm.ead.schema.components.tweens.MoveTween;
import es.eucm.ead.schema.components.tweens.RotateTween;
import es.eucm.ead.schema.components.tweens.ScaleTween;
import es.eucm.ead.schema.effects.SetCamera;
import es.eucm.ead.schemax.Layer;

/**
 * 
 * Camera effects. The tricky part is the code that adjusts x and y to align
 * camera's bottom-left vertex with the bottom-left corner of the screen.
 * Calculus is described below.
 * 
 * <pre>
 * 
 *     LibGDX calculates transformation matrix through the next equation:
 * 
 *     M = [O · S · (-O)] + T
 * 
 *     where:
 *     M is the resultant transformation matrix,
 *     O is the transformation that translates to the origin:
 *         | 1  0  originX |
 *         | 0  1  originY |
 *         | 0  0     1    |
 *     S is the scale matrix:
 *         | scaleX    0     0 |
 *         |   0    scaleY   0 |
 *         |   0       0     1 |
 *     -O is a transformation that undoes part of the origin transformation:
 *         | 1  0  -originX |
 *         | 0  1  -originY |
 *         | 0  0      1    |
 *     T is a matrix that adds a translation component to (x,y)
 *         | 0  0  x |
 *         | 0  0  y |
 *         | 0  0  1 |
 * 
 *     The resulting M is as follows:
 *     |  sx   0    -sx·ox+ox+x  |
 *     |  0    sy   -sy·oy+oy+y  |
 *     |  0    0         1       |
 * 
 *     We want M · ( cameraX  cameraY  1) = ( 0  0  1). Therefore:
 *     -sx·ox+ox+x = -cX·sx
 *     -sy·oy+oy+y = -cY·sy
 * 
 *     So:
 * 
 *     x = -cX·sx - ox·(1-sx)
 *     y = -cY·sy - oy·(1-sy)
 * </pre>
 */
public class SetCameraExecutor extends EffectExecutor<SetCamera> {

	public static final String LOG_TAG = "SetCamera";

	private GameView gameView;

	private VariablesManager variablesManager;

	public SetCameraExecutor(GameView gameView,
			VariablesManager variablesManager) {
		this.gameView = gameView;
		this.variablesManager = variablesManager;
	}

	@Override
	public void execute(Entity target, SetCamera effect) {
		// Get camera
		EngineEntity cameraEntity = gameView.getLayer(Layer.CAMERA);
		IntMap<Entity> entitiesWithCameras = gameLoop.getEntitiesFor(Family
				.getFamilyFor(StaticCamerasComponent.class));
		EngineEntity sceneEntity = null;
		if ((sceneEntity = (EngineEntity) entitiesWithCameras.values().next()) == null) {
			Gdx.app.debug(LOG_TAG,
					"There are no static cameras available. Effect will be skipped.");
			return;
		}
		Camera camera = sceneEntity.getComponent(StaticCamerasComponent.class)
				.getCamera(effect.getCameraId());
		if (camera == null) {
			Gdx.app.debug(LOG_TAG,
					"No static camera with id " + effect.getCameraId()
							+ " is available. Effect will be skipped.");
			return;
		}

		// Calculate new coordinates
		Float viewportWidth = (Float) variablesManager
				.getValue(VarsContext.RESERVED_VIEWPORT_WIDTH_VAR);
		Float viewportHeight = (Float) variablesManager
				.getValue(VarsContext.RESERVED_VIEWPORT_HEIGHT_VAR);

		// Camera width and height
		float w = camera.getWidth();
		float h = camera.getHeight();

		// X and Y
		// After the matrix transformation is applied, we want that M * (x y 1)
		// = (0 0 1).
		// The goal is to get the left-bottom vertex of the camera aligned with
		// the left-bottom vertex of the screen.
		float x = camera.getX();
		float y = camera.getY();

		// Calculate the origin. Must be the center of the camera in scene
		// coordinates.
		// There is no need to adjust it as the origin translation is the first
		// step
		// in Group.computeTransform()
		float newOriginX = x + w / 2.0F;
		float newOriginY = y + h / 2.0F;
		cameraEntity.getGroup().setOrigin(newOriginX, newOriginY);

		// Calculate scale. If the camera's width is smaller than the viewport,
		// zoom-in will be performed.
		// Otherwise zoom out will apply.
		float newScaleX = viewportWidth / w;
		float newScaleY = viewportHeight / h;

		// Calculate the x and y coordinates for the camera entity as to get
		// screen's bottom-left corner
		// aligned with camera's bottom left corner. It's a bit tricky since
		// Group.computeTransform()
		// translates to the origin, then scales and "undoes" the origin
		// translation, but since the
		// matrix is at that point scaled, not all the origin is removed.
		// Calculus is described in class javadoc
		float newX = -x * newScaleX - newOriginX * (1 - newScaleX);
		float newY = -y * newScaleY - newOriginY * (1 - newScaleY);

		// Instantaneous effect: just apply transformation
		if (effect.getAnimationTime() == 0F) {
			cameraEntity.getGroup().setWidth(camera.getWidth());
			cameraEntity.getGroup().setHeight(camera.getHeight());
			// Translate
			cameraEntity.getGroup().setX(newX);
			cameraEntity.getGroup().setY(newY);
			// Scale
			cameraEntity.getGroup().setScaleX(newScaleX);
			cameraEntity.getGroup().setScaleY(newScaleY);

			// Rotate
			cameraEntity.getGroup().setRotation(camera.getRotation());
		} else {
			// Animated effect: create and add tweens
			TweensComponent tweensComponent = gameLoop.addAndGetComponent(
					cameraEntity, TweensComponent.class);
			RotateTween rotateTween = new RotateTween();
			rotateTween.setRotation(camera.getRotation());
			rotateTween.setDuration(effect.getAnimationTime());
			rotateTween.setRelative(false);
			ScaleTween scaleTween = new ScaleTween();
			scaleTween.setScaleX(newScaleX);
			scaleTween.setScaleY(newScaleY);
			scaleTween.setDuration(effect.getAnimationTime());
			MoveTween moveTween = new MoveTween();
			moveTween.setX(newX);
			moveTween.setY(newY);
			moveTween.setDuration(effect.getAnimationTime());
			tweensComponent.addTween(moveTween);
			tweensComponent.addTween(scaleTween);
			tweensComponent.addTween(rotateTween);
		}
	}
}
