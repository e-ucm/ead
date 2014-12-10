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

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
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
import es.eucm.ead.editor.view.widgets.modelwidgets.ModelSelectBox;
import es.eucm.ead.editor.view.widgets.modelwidgets.ModelTextField;
import es.eucm.ead.editor.view.widgets.modelwidgets.ModelTextField.DataType;
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

	private BaseTweenDropdownPane passX;

	private BaseTweenDropdownPane passY;

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
		buildPassXPane();
		list.add(passX).expandX();
		buildPassYPane();
		list.add(passY).expandX();
		buildRotatePane();
		list.add(rotate).expandX();

	}

	@Override
	protected void read(Timeline component) {
		ModelEntity sceneElement = (ModelEntity) controller.getModel()
				.getSelection().getSingle(Selection.SCENE_ELEMENT);

		readAndLoadComponent(blink, sceneElement, ComponentIds.BLINK);
		readAndLoadComponent(jump, sceneElement, ComponentIds.JUMP);
		readAndLoadComponent(passX, sceneElement, ComponentIds.PASS_X);
		readAndLoadComponent(passY, sceneElement, ComponentIds.PASS_Y);
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
		removeComponent(modelEntity, ComponentIds.PASS_X);
		removeComponent(modelEntity, ComponentIds.PASS_Y);
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
		} else if (id.equals(ComponentIds.PASS_X)) {
			timeline = createHorizontalPass(game, id, sceneElment);
		} else if (id.equals(ComponentIds.PASS_Y)) {
			timeline = createVerticalPass(game, id, sceneElment);
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

	private Timeline createHorizontalPass(GameData game, String id,
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

	private Timeline createVerticalPass(GameData game, String id,
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

	private Table buildCheckBoxRepeatsGroup() {
		Table layout = new Table();
		layout.left();

		final CheckBox loop = new CheckBox(i18N.m("loop"), skin,
				SkinConstants.STYLE_CONTEXT_RADIO);
		final CheckBox repeats = new CheckBox(i18N.m("repeats"), skin,
				SkinConstants.STYLE_CONTEXT_RADIO);

		final ModelTextField text = new ModelTextField(skin,
				SkinConstants.STYLE_GRAY, SkinConstants.COLOR_GRAY,
				DataType.INT) {
			@Override
			public void loadComponent(ModelComponent component, Object val) {
				super.loadComponent(component, val);
				if (((Timeline) component).getRepeat() >= 0) {
					repeats.setChecked(true);
				} else {
					loop.setChecked(true);
				}
			}
		};
		text.setColor(skin.getColor(SkinConstants.COLOR_GRAY));

		loop.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (loop.isChecked() && text.getComponent() != null) {
					controller.action(SetField.class, text.getComponent(),
							FieldName.REPEAT, -1);
				}
			}
		});

		repeats.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (repeats.isChecked() && text.getComponent() != null) {
					controller.action(SetField.class, text.getComponent(),
							FieldName.REPEAT, Integer.valueOf(text.getText()));
				}
			}
		});

		text.addListener(new InputListener() {

			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				return super.keyDown(event, keycode);
			}

			@Override
			public boolean keyTyped(InputEvent event, char character) {
				if (repeats.isChecked() && text.getComponent() != null) {
					try {
						text.updateCurrentValue();
						controller.action(SetField.class, text.getComponent(),
								FieldName.REPEAT,
								Integer.valueOf(text.getText()));
					} catch (NumberFormatException e) {
					}
				}
				return super.keyTyped(event, character);
			}
		});

		Table table = new Table();
		table.add(repeats);
		table.add(text).padLeft(WidgetBuilder.dpToPixels(16))
				.padRight(WidgetBuilder.dpToPixels(16));

		layout.add(loop).expandX().left();
		layout.row();
		layout.add(table).expandX();

		ButtonGroup group = new ButtonGroup();
		group.add(loop);
		group.add(repeats);

		return layout;
	}

	private ModelSelectBox buildSpeedTimelineSelectBox(String name,
			final String idTween) {
		final ModelSelectBox selectBox = new ModelSelectBox(skin, name,
				speedArray, SkinConstants.STYLE_EDITION,
				SkinConstants.STYLE_CONTEXT) {

			@Override
			public void loadComponent(ModelComponent component) {
				ModelEntity sceneElement = (ModelEntity) controller.getModel()
						.getSelection().getSingle(Selection.SCENE_ELEMENT);
				setBaseValue(Q
						.calculateTimelineDuration(createDefaultAnimation(
								idTween, sceneElement)));
				setInitValue(Q.calculateTimelineDuration((Timeline) component));
				super.loadComponent(component);
			}

		};

		selectBox.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				selectBox.calculateFactor(Q
						.calculateTimelineDuration((Timeline) selectBox
								.getComponent()));
				controller.action(MultiplyTimeline.class,
						selectBox.getComponent(), selectBox.getFactor(), true);
			}
		});

		return selectBox;
	}

	private ModelSelectBox buildAmountTimelineSelectBox(String name,
			final String idTween) {
		final ModelSelectBox selectBox = new ModelSelectBox(skin, name,
				amountArray, SkinConstants.STYLE_EDITION,
				SkinConstants.STYLE_CONTEXT) {

			@Override
			public void loadComponent(ModelComponent component) {
				ModelEntity sceneElement = (ModelEntity) controller.getModel()
						.getSelection().getSingle(Selection.SCENE_ELEMENT);
				setBaseValue(Q.calculateTimelineAmount(createDefaultAnimation(
						idTween, sceneElement)));
				setInitValue(Q.calculateTimelineAmount((Timeline) component));
				super.loadComponent(component);
			}

		};

		selectBox.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				selectBox.calculateFactor(Q
						.calculateTimelineAmount((Timeline) selectBox
								.getComponent()));
				controller.action(MultiplyTimeline.class,
						selectBox.getComponent(), selectBox.getFactor(), false);
			}
		});

		return selectBox;
	}

	private void buildBlinkPane() {
		blink = new BaseTweenDropdownPane(controller, i18N.m("blink"), skin);

		ModelSelectBox speed = buildSpeedTimelineSelectBox(i18N.m("speed"),
				ComponentIds.BLINK);
		Table repeats = buildCheckBoxRepeatsGroup();

		blink.addToBody(speed);
		blink.addToBody(repeats);

	}

	private void buildJumpPane() {
		jump = new BaseTweenDropdownPane(controller, i18N.m("jump"), skin);

		ModelSelectBox amount = buildAmountTimelineSelectBox(i18N.m("amount"),
				ComponentIds.JUMP);
		ModelSelectBox speed = buildSpeedTimelineSelectBox(i18N.m("speed"),
				ComponentIds.JUMP);
		Table repeats = buildCheckBoxRepeatsGroup();

		jump.addToBody(amount);
		jump.addToBody(speed);
		jump.addToBody(repeats);
	}

	private void buildPassXPane() {
		passX = new BaseTweenDropdownPane(controller,
				i18N.m("pass.horizontal"), skin);

		ModelSelectBox speed = buildSpeedTimelineSelectBox(i18N.m("speed"),
				ComponentIds.PASS_X);
		Table repeats = buildCheckBoxRepeatsGroup();

		passX.addToBody(speed);
		passX.addToBody(repeats);

	}

	private void buildPassYPane() {
		passY = new BaseTweenDropdownPane(controller, i18N.m("pass.vertical"),
				skin);

		ModelSelectBox speed = buildSpeedTimelineSelectBox(i18N.m("speed"),
				ComponentIds.PASS_Y);
		Table repeats = buildCheckBoxRepeatsGroup();

		passY.addToBody(speed);
		passY.addToBody(repeats);

	}

	private void buildRotatePane() {
		rotate = new BaseTweenDropdownPane(controller, i18N.m("rotate"), skin);

		ModelSelectBox speed = buildSpeedTimelineSelectBox(i18N.m("speed"),
				ComponentIds.ROTATE);

		rotate.addToBody(speed);
	}

}
