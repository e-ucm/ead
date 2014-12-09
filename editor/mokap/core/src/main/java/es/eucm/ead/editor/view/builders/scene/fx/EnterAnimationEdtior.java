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
package es.eucm.ead.editor.view.builders.scene.fx;

import java.util.HashMap;

import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.model.MultiplyTimeline;
import es.eucm.ead.editor.control.actions.model.generic.RemoveFromArray;
import es.eucm.ead.editor.control.actions.model.generic.SetField;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.builders.scene.interaction.ComponentEditor;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.editor.view.widgets.modelwidgets.ModelCheckBox;
import es.eucm.ead.editor.view.widgets.modelwidgets.ModelCheckBox.DataType;
import es.eucm.ead.editor.view.widgets.modelwidgets.ModelSelectBox;
import es.eucm.ead.schema.components.ModelComponent;
import es.eucm.ead.schema.components.tweens.AlphaTween;
import es.eucm.ead.schema.components.tweens.MoveTween;
import es.eucm.ead.schema.components.tweens.RotateTween;
import es.eucm.ead.schema.components.tweens.Timeline;
import es.eucm.ead.schema.components.tweens.Timeline.Mode;
import es.eucm.ead.schema.editor.components.GameData;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.ComponentIds;
import es.eucm.ead.schemax.FieldName;

public class EnterAnimationEdtior extends ComponentEditor<Timeline> {

	private HashMap<String, Float> baseTweenSpeed;

	private HashMap<String, Float> baseTweenAmount;

	private BaseTweenDropdownPane blink;

	private BaseTweenDropdownPane jump;

	private BaseTweenDropdownPane crossX;

	private BaseTweenDropdownPane crossY;

	private BaseTweenDropdownPane rotate;

	private Array<String> amountArray;

	private Array<String> speedArray;

	public EnterAnimationEdtior(Controller controller) {
		super(SkinConstants.IC_BLUR_LINEAR, controller.getApplicationAssets()
				.getI18N().m("animation.in"), ComponentIds.ANIMATION_IN,
				controller);
	}

	@Override
	protected void buildContent() {

		amountArray = new Array<String>();
		amountArray.addAll(i18N.m("little"), i18N.m("middle"), i18N.m("much"));

		speedArray = new Array<String>();
		speedArray.addAll(i18N.m("fast"), i18N.m("normal"), i18N.m("slow"));

		baseTweenSpeed = new HashMap<String, Float>();
		baseTweenAmount = new HashMap<String, Float>();

		list.defaultWidgetsMargin(WidgetBuilder.dpToPixels(8));
		buildBlinkPane();
		list.add(blink).expandX();
		buildJumpPane();
		list.add(jump).expandX();
		buildCrossXPane();
		list.add(crossX).expandX();
		buildCrossYPane();
		list.add(crossY).expandX();
		buildRotatePane();
		list.add(rotate).expandX();

	}

	@Override
	protected void read(Timeline component) {
		ModelEntity sceneElement = (ModelEntity) controller.getModel()
				.getSelection().getSingle(Selection.SCENE_ELEMENT);

		readAndLoadComponent(blink, sceneElement, ComponentIds.BLINK);
		readAndLoadComponent(jump, sceneElement, ComponentIds.JUMP);
		readAndLoadComponent(crossX, sceneElement, ComponentIds.CROSS_X);
		readAndLoadComponent(crossY, sceneElement, ComponentIds.CROSS_Y);
		readAndLoadComponent(rotate, sceneElement, ComponentIds.ROTATE);

	}

	private void readAndLoadComponent(BaseTweenDropdownPane dropDown,
			ModelEntity sceneElement, String id) {
		Timeline timeline = (Timeline) Q.getComponentById(sceneElement, id);

		boolean stateOn = true;
		if (timeline == null) {
			timeline = createDefaultAnimation(id, sceneElement);
			stateOn = false;
		}

		if (!baseTweenSpeed.containsKey(id) && !baseTweenAmount.containsKey(id)) {
			createDefaultAnimation(id, sceneElement);
		}
		dropDown.loadComponent(timeline, baseTweenSpeed.get(id),
				baseTweenAmount.get(id), stateOn);
	}

	@Override
	public String getTooltip() {
		return controller.getApplicationAssets().getI18N().m("animation.in");
	}

	@Override
	protected Timeline buildNewComponent() {
		return new Timeline();
	}

	@Override
	protected void removeComponent(ModelEntity modelEntity) {
		removeComponent(modelEntity, ComponentIds.BLINK);
		removeComponent(modelEntity, ComponentIds.JUMP);
		removeComponent(modelEntity, ComponentIds.CROSS_X);
		removeComponent(modelEntity, ComponentIds.CROSS_Y);
		removeComponent(modelEntity, ComponentIds.ROTATE);
		super.removeComponent(modelEntity);
	}

	private void removeComponent(ModelEntity modelEntity, String id) {
		ModelComponent component = Q.getComponentById(modelEntity, id);
		if (component != null) {
			controller.action(RemoveFromArray.class, modelEntity,
					modelEntity.getComponents(), component);
		}
	}

	private Timeline createDefaultAnimation(String id, ModelEntity sceneElment) {
		GameData game = Q.getComponent(controller.getModel().getGame(),
				GameData.class);
		Timeline timeline = null;
		if (id.equals(ComponentIds.BLINK)) {
			timeline = createBlink(id);
		} else if (id.equals(ComponentIds.JUMP)) {
			timeline = createJump(game, id);
		} else if (id.equals(ComponentIds.CROSS_X)) {
			timeline = createHorizontalCross(game, id, sceneElment);
		} else if (id.equals(ComponentIds.CROSS_Y)) {
			timeline = createVerticalCross(game, id, sceneElment);
		} else if (id.equals(ComponentIds.ROTATE)) {
			timeline = createRotate(id);
		}

		if (!baseTweenSpeed.containsKey(id) && !baseTweenAmount.containsKey(id)) {
			baseTweenSpeed.put(id, Q.calculateTimelineDuration(timeline));
			baseTweenAmount.put(id, Q.calculateTimelineAmount(timeline));
		}

		return timeline;
	}

	private Timeline createBlink(String id) {
		Timeline timeline;

		AlphaTween alphaTween = new AlphaTween();
		alphaTween.setDuration(1.0f);
		alphaTween.setAlpha(0.0f);

		AlphaTween alphaTween2 = new AlphaTween();
		alphaTween2.setDuration(1.0f);
		alphaTween2.setAlpha(1.0f);

		timeline = new Timeline();
		timeline.setId(id);
		timeline.setMode(Mode.SEQUENCE);

		timeline.getChildren().add(alphaTween);
		timeline.getChildren().add(alphaTween2);

		timeline.setRepeat(-1);

		return timeline;
	}

	private Timeline createJump(GameData game, String id) {
		float heightJump = game.getHeight() * 0.4f;

		Timeline timeline;
		MoveTween moveTween = new MoveTween();
		moveTween.setDuration(1.0f);
		moveTween.setX(0.0f);
		moveTween.setY(heightJump);
		moveTween.setRelative(true);

		MoveTween moveTween2 = new MoveTween();
		moveTween2.setDuration(1.0f);
		moveTween2.setX(0.0f);
		moveTween2.setY(-heightJump);
		moveTween2.setRelative(true);

		timeline = new Timeline();
		timeline.setId(id);
		timeline.setMode(Mode.SEQUENCE);

		timeline.getChildren().add(moveTween);
		timeline.getChildren().add(moveTween2);
		timeline.setRepeat(1);

		return timeline;
	}

	private Timeline createHorizontalCross(GameData game, String id,
			ModelEntity sceneElement) {
		float originX = sceneElement.getX();
		float width = game.getWidth() * 1.2f;

		Timeline timeline;
		MoveTween moveTween = new MoveTween();
		moveTween.setDuration(3.0f);
		moveTween.setX(width);
		moveTween.setY(Float.NaN);

		MoveTween moveTween2 = new MoveTween();
		moveTween2.setDuration(0.0f);
		moveTween2.setX(originX);
		moveTween2.setY(Float.NaN);

		timeline = new Timeline();
		timeline.setId(id);
		timeline.setMode(Mode.SEQUENCE);

		timeline.getChildren().add(moveTween);
		timeline.getChildren().add(moveTween2);

		timeline.setRepeat(-1);

		return timeline;
	}

	private Timeline createVerticalCross(GameData game, String id,
			ModelEntity sceneElement) {
		float originY = sceneElement.getY();
		float height = game.getHeight() * 1.2f;

		Timeline timeline;
		MoveTween moveTween = new MoveTween();
		moveTween.setDuration(3.0f);
		moveTween.setX(Float.NaN);
		moveTween.setY(height);

		MoveTween moveTween2 = new MoveTween();
		moveTween2.setDuration(0.0f);
		moveTween2.setX(Float.NaN);
		moveTween2.setY(originY);

		timeline = new Timeline();
		timeline.setId(id);
		timeline.setMode(Mode.SEQUENCE);

		timeline.getChildren().add(moveTween);
		timeline.getChildren().add(moveTween2);

		timeline.setRepeat(-1);

		return timeline;
	}

	private Timeline createRotate(String id) {

		Timeline timeline;
		RotateTween rotateTween = new RotateTween();
		rotateTween.setDuration(3.0f);
		rotateTween.setRotation(360);
		rotateTween.setRelative(false);

		timeline = new Timeline();
		timeline.setId(id);
		timeline.setMode(Mode.SEQUENCE);

		timeline.getChildren().add(rotateTween);

		timeline.setRepeat(-1);

		return timeline;
	}

	private ModelCheckBox buildModelCheckBox(String name, DataType type,
			int value) {
		return new ModelCheckBox(skin, name, type, value,
				SkinConstants.STYLE_GRAY, SkinConstants.STYLE_CONTEXT_RADIO) {
			@Override
			public void doAction() {
				super.doAction();
				controller.action(SetField.class, getComponent(),
						FieldName.REPEAT, getCurrentValue());
			}
		};
	}

	private ModelSelectBox buildTimelineSelectBox(String name,
			final Array<String> items, final String idTween) {
		return new ModelSelectBox(skin, name, items,
				SkinConstants.STYLE_EDITION, SkinConstants.STYLE_CONTEXT) {
			@Override
			public void doAction() {
				super.doAction();
				controller.action(MultiplyTimeline.class, getComponent(),
						getFactor(), items == speedArray);
			}

			@Override
			protected float calculateValue() {
				if (items == speedArray) {
					return Q.calculateTimelineDuration((Timeline) getComponent());
				} else {
					return Q.calculateTimelineAmount((Timeline) getComponent());
				}
			}

			@Override
			public void loadComponent(ModelComponent component) {
				ModelEntity sceneElement = (ModelEntity) controller.getModel()
						.getSelection().getSingle(Selection.SCENE_ELEMENT);
				if (items == speedArray) {
					base = Q.calculateTimelineDuration(createDefaultAnimation(
							idTween, sceneElement));
				} else {
					base = Q.calculateTimelineAmount(createDefaultAnimation(
							idTween, sceneElement));
				}
				super.loadComponent(component);
			}

		};
	}

	private void buildBlinkPane() {
		blink = new BaseTweenDropdownPane(controller, i18N.m("blink"), skin);

		ModelSelectBox speed = buildTimelineSelectBox(i18N.m("speed"),
				speedArray, ComponentIds.BLINK);
		ModelCheckBox loop = buildModelCheckBox(i18N.m("loop"), DataType.NONE,
				-1);
		ModelCheckBox repeats = buildModelCheckBox(i18N.m("repeats"),
				DataType.INT, 0);

		ButtonGroup group = new ButtonGroup();
		group.add(loop.getCheckBox());
		group.add(repeats.getCheckBox());

		blink.addToBody(speed);
		blink.addToBody(loop);
		blink.addToBody(repeats);
	}

	private void buildJumpPane() {
		jump = new BaseTweenDropdownPane(controller, i18N.m("jump"), skin);

		ModelSelectBox amount = buildTimelineSelectBox(i18N.m("amount"),
				amountArray, ComponentIds.JUMP);
		ModelSelectBox speed = buildTimelineSelectBox(i18N.m("speed"),
				speedArray, ComponentIds.JUMP);
		ModelCheckBox loop = buildModelCheckBox(i18N.m("loop"), DataType.NONE,
				-1);
		ModelCheckBox repeats = buildModelCheckBox(i18N.m("repeats"),
				DataType.INT, 0);

		ButtonGroup group = new ButtonGroup();
		group.add(loop.getCheckBox());
		group.add(repeats.getCheckBox());

		jump.addToBody(amount);
		jump.addToBody(speed);
		jump.addToBody(loop);
		jump.addToBody(repeats);
	}

	private void buildCrossXPane() {
		crossX = new BaseTweenDropdownPane(controller,
				i18N.m("cross.horizontal"), skin);

		ModelSelectBox speed = buildTimelineSelectBox(i18N.m("speed"),
				speedArray, ComponentIds.CROSS_X);
		ModelCheckBox loop = buildModelCheckBox(i18N.m("loop"), DataType.NONE,
				-1);
		ModelCheckBox repeats = buildModelCheckBox(i18N.m("repeats"),
				DataType.INT, 0);

		ButtonGroup group = new ButtonGroup();
		group.add(loop.getCheckBox());
		group.add(repeats.getCheckBox());

		crossX.addToBody(speed);
		crossX.addToBody(loop);
		crossX.addToBody(repeats);

	}

	private void buildCrossYPane() {
		crossY = new BaseTweenDropdownPane(controller,
				i18N.m("cross.vertical"), skin);

		ModelSelectBox speed = buildTimelineSelectBox(i18N.m("speed"),
				speedArray, ComponentIds.CROSS_Y);
		ModelCheckBox loop = buildModelCheckBox(i18N.m("loop"), DataType.NONE,
				-1);
		ModelCheckBox repeats = buildModelCheckBox(i18N.m("repeats"),
				DataType.INT, 0);

		ButtonGroup group = new ButtonGroup();
		group.add(loop.getCheckBox());
		group.add(repeats.getCheckBox());

		crossY.addToBody(speed);
		crossY.addToBody(loop);
		crossY.addToBody(repeats);

	}

	private void buildRotatePane() {
		rotate = new BaseTweenDropdownPane(controller, i18N.m("rotate"), skin);

		ModelSelectBox speed = buildTimelineSelectBox(i18N.m("speed"),
				speedArray, ComponentIds.ROTATE);

		rotate.addToBody(speed);
	}

}
