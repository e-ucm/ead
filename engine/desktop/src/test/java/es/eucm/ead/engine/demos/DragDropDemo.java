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
package es.eucm.ead.engine.demos;

import es.eucm.ead.builder.DragDropBuilder;
import es.eucm.ead.engine.demobuilder.ExecutableDemoBuilder;
import es.eucm.ead.engine.variables.ReservedVariableNames;
import es.eucm.ead.schema.components.controls.Label;
import es.eucm.ead.schema.components.tweens.EffectTween;
import es.eucm.ead.schema.components.tweens.MoveTween;
import es.eucm.ead.schema.components.tweens.Timeline;
import es.eucm.ead.schema.components.tweens.Tween;
import es.eucm.ead.schema.effects.*;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schema.renderers.Frames;

/**
 * Demoes drag&drop interactions with the classic shape-matching drag&drop game
 * for kids. Created by jtorrente on 19/11/2015.
 */
public class DragDropDemo extends ExecutableDemoBuilder {
	private DragDropBuilder dd = new DragDropBuilder(this) {
		public Effect makeResetPositionEffect(ModelEntity entity) {
			return makeIfElse(eb.variableEqualsTo(RETURN_HOME_ON_VAR, true),
					super.makeResetPositionEffect(entity), null);
		}

		protected Effect makeStickToDroppableEffect() {
			Tween setPosition = makeTween(MoveTween.class, 0F, 0, 0F, false,
					0.1F, false, Tween.EaseEquation.EXPO, Tween.EaseType.IN,
					1F, 2F, null, null);
			parameter(setPosition, "x",
					eb.sum(eb.entityVarX(INTERSECTED_ENTITY_VAR), 2));
			parameter(setPosition, "y",
					eb.sum(eb.entityVarY(INTERSECTED_ENTITY_VAR), 2));
			return makeAddComponent(setPosition);
		}
	};

	public DragDropDemo() {
		super("dragdrop");
	}

	private static final String RETURN_HOME_ON_VAR = "ReturnHomeOnVar";

	private static final String CLUES_ON_VAR = "CluesOnVar";

	@Override
	protected void doBuild() {
		game(958, 797);
		tickAnimation();
		ModelEntity scene = scene("AnimalShapesBackground.jpg").getLastScene();
		entity(scene, "AnimalShapesBackground.jpg", 0, 0).tags("default");
		ModelEntity shadows = entity(scene, 0, 0).getLastEntity();
		ModelEntity animals = entity(scene, 0, 0).getLastEntity();
		animal(animals, shadows, "monkey", 22, 41, 186, 505);
		animal(animals, shadows, "lion", 161, 28, 577, 246);
		animal(animals, shadows, "buffalo", 360, 24, 202, 320);
		animal(animals, shadows, "snake", 596, 49, 354, 240);
		animal(animals, shadows, "giraffe", 778, 249, 435, 350);
		animal(animals, shadows, "tiger", 789, 538, 43, 235);

		checkbox(scene, RETURN_HOME_ON_VAR, "purple", "Return home", 170, 755);
		checkbox(scene, CLUES_ON_VAR, "red", "Color clues", 470, 755);

		// entity(scene, 50, 50).rectangle(100, 100);
		centerOrigin();
		color(1, 0, 1, 1);
		dd.dragDropBehaviors(getLastEntity(), true);
	}

	private void animal(ModelEntity animalContainer,
			ModelEntity shadowContainer, String animal, float x, float y,
			float tx, float ty) {
		String img = animal + ".png";
		String timg = animal + "-shadow.png";

		entity(shadowContainer, timg, tx, ty)
				.tags(animal + "_shadow", "shadow");
		centerOrigin();

		entity(animalContainer, img, x, y).tags(animal, "animal", "default");
		centerOrigin();

		addDraggable(getLastEntity(), animal);
	}

	private void addDraggable(ModelEntity entity, String animal) {
		String correctDroppableTag = animal + "_shadow";
		String allDroppablesTag = "shadow";

		/*
		 * TouchDown behaviour: adds color clues through move events, remove
		 * tick (if present),
		 */
		RemoveEntity removeTick = new RemoveEntity();
		removeTick.setTarget(eb.entityWithTag("tick_" + animal));
		Effect touchDownEffects = makeEffectsGroup(
				removeTick,
				makeIfElse(
						eb.variableEqualsTo(CLUES_ON_VAR, true),
						dd.makeCluesEffect(
								correctDroppableTag,
								allDroppablesTag,
								makeChangeColorEffect(animal, 0F, 1F, 0F, 0.8F),
								makeChangeColorEffect(animal, 1F, 0F, 0F, 0.8F),
								makeChangeColorEffect(animal, 1F, 1F, 1F, 1.0F)),
						null));

		/*
		 * Touch up effects
		 */
		// 1) Reset color (regardless of success/mistake)
		Effect touchUpEffect = makeChangeColorEffect(animal, 1F, 1F, 1F, 1F);
		// 2.A) If correct, add the green tick animation and play cheerful sound
		Effect toucUpIfCorrectEffect = makeEffectsGroup(
				makeAddTickEffect(animal), makePlaySound("magical_1.mp3"));
		// 2.B) If wrong, play "gong" sound
		Effect touchUpIfWrongEffect = makeIfElse(
				eb.intersectsThisAnyByTag(allDroppablesTag),
				makePlaySound("gong.wav"), null);

		/*
		 * Create the drag & drop effect (creates touchDown and touchUp
		 * behaviors)
		 */
		dd.dragDropBehaviors(entity, false, touchDownEffects, true, true,
				correctDroppableTag, touchUpEffect, toucUpIfCorrectEffect,
				touchUpIfWrongEffect);
	}

	private AddEntity makeAddTickEffect(String animal) {
		AddEntity addTick = makeAddEntity("tick.json");
		Timeline inTimeline = new Timeline();
		EffectTween effectTween = new EffectTween();
		AddTag addTag = new AddTag();
		addTag.setTarget(eb
				.varReference(ReservedVariableNames.RESERVED_NEWEST_ENTITY_VAR));
		addTag.setTag("tick_" + animal);
		effectTween.getEffects().add(addTag);
		inTimeline.getChildren().add(effectTween);
		addTick.setAnimationIn(inTimeline);
		return addTick;
	}

	private String tickAnimation() {
		String entityUri = "tick.json";
		reusableEntity(entityUri, null, 0, 0).tags("tick")
				.frames(0.05F, "tick-", ".png", 1, 8, 2)
				.getLastComponent(Frames.class)
				.setSequence(Frames.Sequence.LASTFRAME);
		return entityUri;
	}

	private void checkbox(ModelEntity container, String var, String color,
			String text, float x, float y) {
		ModelEntity checkbox = entity(container, x, y).getLastEntity();
		ModelEntity box = entity(checkbox, 0, 0).getLastEntity();
		entity(box, "toggle-" + color + "-off.png", 0, 0).visibility(
				eb.variableEqualsTo(var, false));
		entity(box, "toggle-" + color + "-on.png", 0, 0).visibility(
				eb.variableEqualsTo(var, true));
		ModelEntity textEnt = entity(checkbox, 65, -3).getLastEntity();
		scale(0.3F);
		Label label = new Label();
		label.setText(text);
		label.setColor(makeColor(1, 1, 1, 1));
		textEnt.getComponents().add(label);
		touchBehavior(
				checkbox,
				makeChangeVar(var, eb.not(eb.varReference(var)),
						ChangeVar.Context.GLOBAL));
		initBehavior(checkbox,
				makeChangeVar(var, eb.bool(false), ChangeVar.Context.GLOBAL));
	}

	public static void main(String[] args) {
		new DragDropDemo().run();
	}
}
