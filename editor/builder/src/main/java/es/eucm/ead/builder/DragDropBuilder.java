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
package es.eucm.ead.builder;

import com.badlogic.gdx.utils.Array;
import es.eucm.ead.schema.components.behaviors.Behavior;
import es.eucm.ead.schema.components.behaviors.events.Move;
import es.eucm.ead.schema.components.behaviors.events.Touch;
import es.eucm.ead.schema.components.drag.Sticky;
import es.eucm.ead.schema.components.tweens.MoveTween;
import es.eucm.ead.schema.components.tweens.Tween;
import es.eucm.ead.schema.effects.*;
import es.eucm.ead.schema.entities.ModelEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Utility that helps build Drag & Drop interactions and scenes. See
 * DragDropDemo for more details. Created by jtorrente on 22/11/2015.
 */
public class DragDropBuilder {

	private DemoBuilder b;

	private HashMap<ModelEntity, EntityVars> entityVars;
	public static final String INTERSECTED_ENTITY_VAR = "IntersectedEntityVar";

	public DragDropBuilder(DemoBuilder b) {
		this.b = b;
		entityVars = new HashMap<ModelEntity, EntityVars>();
	}

	/**
	 * Creates an effect that serves to provide clues in drag&drop games. Works
	 * as follows: 1. If the object represented by "this" intersects any element
	 * tagged with the correctEntityTag, then the effect correctMatchEffect is
	 * triggered. 2. If not, then "this" will be checked for intersections with
	 * entities that have the dropableEntitiesTag. If any matches are found, the
	 * wrongMatchEffect will be triggered. If not, restorEffect is triggered
	 * 
	 * This way, it is possible to tag all the entities that represent place
	 * holders (i.e. dropable objects where pieces of puzzles or other draggable
	 * objects can be dropped upon) with the same tag. Then tag the one (or
	 * several) correct placeholder with the correctEntityTag.
	 * 
	 * correctMatchEffect can be used to highlight the draggable object in green
	 * or provide a tick or a sound or any other form of feedback used to mark
	 * the interaction is correct.
	 * 
	 * wrongMatchEffect can be used as the opposite, to provide a red highlight
	 * or any other form of incorrect action feedback.
	 * 
	 * And restoreEffect can be used to implement any default option when
	 * there's no interaction, like moving back all draggable objects to the
	 * original position.
	 * 
	 * @param correctEntityTag
	 * @param dropableEntitiesTag
	 * @param correctMatchEffect
	 * @param wrongMatchEffect
	 * @param restoreEffect
	 * @return
	 */
	public Effect makeCluesEffect(String correctEntityTag,
			String dropableEntitiesTag, Effect correctMatchEffect,
			Effect wrongMatchEffect, Effect restoreEffect) {
		Move move = new Move();
		move.setType(Move.Type.DRAG);
		Behavior moveB = b.makeBehavior(move, b.makeIfElse(b.eb
				.intersectsThisByTag(correctEntityTag), correctMatchEffect, b
				.makeIfElse(b.eb.intersectsThisAnyByTag(dropableEntitiesTag),
						wrongMatchEffect, restoreEffect)));
		Effect colorCluesEffect = b.makeAddComponent(moveB);
		return colorCluesEffect;
	}

	/**
	 * Simplified version of
	 * {@link #dragDropBehaviors(ModelEntity, boolean, Effect, boolean, boolean, String, Effect, Effect, Effect)}
	 * . Just adds the possibility to drag& drop this entity around.
	 * 
	 * @param entity
	 *            The entity to make draggable.
	 * @param centerOnTouchDown
	 *            If true, the entity will be centered-aligned with the mouse
	 *            pointer or the finger upon mouse pressed or touch down events.
	 */
	public void dragDropBehaviors(ModelEntity entity, boolean centerOnTouchDown) {
		dragDropBehaviors(entity, centerOnTouchDown, null, false, false, null,
				null, null, null);
	}

	/**
	 * Adds a drag&drop behavior to the given entity
	 * 
	 * @param entity
	 *            Entity the behavior will be added to
	 * @param centerOnTouchDown
	 *            If true, the entity will be centered-aligned with the mouse
	 *            pointer or the finger upon mouse pressed or touch down events.
	 * @param touchDownEffect
	 *            Optional effect to be launched when the pressed event is
	 *            triggered
	 * @param stickIfCorrect
	 *            If true, upon release the entity will be left-down-aligned
	 *            with the entity it was dropped upon. Otherwise the entity will
	 *            be left in whatever position it's dropped
	 * @param resetPositionIfWrong
	 *            If true, if the entity is dropped upon the wrong position
	 *            (defined by the correctDropTargetTag), it will be moved back
	 *            to the original position.
	 * @param correctDropTargetTag
	 *            Tag that determines the entities that will correspond to valid
	 *            drop positions
	 * @param touchUpEffect
	 *            Optional effect. Triggered upon release event.
	 * @param touchUpEffectIfCorrect
	 *            Optional effect. Triggered upon release event, if the target
	 *            entity is a correct one as defined by correctDropTargetTag
	 * @param touchUpEffectIfWrong
	 *            Optional effect. Triggered upon release event if the above
	 *            condition doesn't match.
	 */
	public void dragDropBehaviors(ModelEntity entity,
			boolean centerOnTouchDown, Effect touchDownEffect,
			boolean stickIfCorrect, boolean resetPositionIfWrong,
			String correctDropTargetTag, Effect touchUpEffect,
			Effect touchUpEffectIfCorrect, Effect touchUpEffectIfWrong) {
		makeTouchDownBehavior(entity, centerOnTouchDown, touchDownEffect);
		makeTouchUpBehavior(entity, stickIfCorrect, resetPositionIfWrong,
				correctDropTargetTag, touchUpEffect, touchUpEffectIfCorrect,
				touchUpEffectIfWrong);
	}

	/**
	 * Creates the touch-down behavior associated to a drag&drop behavior. It
	 * will basically add a Sticky component to the given entity upon a mouse
	 * pressed (touch) event, making it follow the cursor (or the finger on
	 * mobile devices).
	 * 
	 * @param entity
	 *            The entity that will hold the touch down behavior
	 * @param center
	 *            If true, will make sure the entity will be centered on the
	 *            mouse pointer / finger.
	 * @param additionalEffect
	 *            Additional effects to be triggered upon the press event.
	 * @return The touchDown behavior added to the entity
	 */
	public Behavior makeTouchDownBehavior(ModelEntity entity, boolean center,
			Effect additionalEffect) {
		Sticky stickyComp = new Sticky();
		stickyComp.setCenter(center);

		Behavior touchDown = additionalEffect != null ? b.makeTouchBehavior(
				b.makeAddComponent(stickyComp), additionalEffect) : b
				.makeTouchBehavior(b.makeAddComponent(stickyComp));
		Touch touchDownEvent = (Touch) touchDown.getEvent();
		touchDownEvent.setType(Touch.Type.PRESS);
		entity.getComponents().add(touchDown);
		return touchDown;
	}

	/**
	 * Creates the touch-up behavior associated to a drag&drop behavior. Upon
	 * mose release (or touch up), the next effects will be triggered: 1. Any
	 * sticky component in the entity will be removed, so it stops tracking the
	 * mouse pointer or the user's finger. 2. Removes an color clues effect 3.
	 * Launches any touchUpEffect provided. If null, wont trigger any effect 4.
	 * If the entity is dropped upon any entity marked with the
	 * correctDropTargetTag, touchUpEffectIfCorrect will be triggered (if
	 * provided). Also, if stickIfCorrect is set to true, the entity's position
	 * will be modified to match the position of the entity it was dropped upon.
	 * 5. Otherwise, touchUpEffectIfWrong is triggered (if provided). And if
	 * resetPositionIfWrong is true, the entity will be moved back to its
	 * original position
	 * 
	 * @return The behavior created and added to the entity
	 */
	public Behavior makeTouchUpBehavior(ModelEntity entity,
			boolean stickIfCorrect, boolean resetPositionIfWrong,
			String correctDropTargetTag, Effect touchUpEffect,
			Effect touchUpEffectIfCorrect, Effect touchUpEffectIfWrong) {

		Array<Effect> rightTouchUpEffects = new Array<Effect>();

		if (correctDropTargetTag != null) {
			rightTouchUpEffects
					.add(makeCalculateIntersectionEffect(correctDropTargetTag));
		}
		if (stickIfCorrect) {
			rightTouchUpEffects.add(makeStickToDroppableEffect());
		}
		if (touchUpEffectIfCorrect != null) {
			rightTouchUpEffects.add(touchUpEffectIfCorrect);
		}

		Array<Effect> wrongTouchUpEffects = new Array<Effect>();
		if (resetPositionIfWrong) {
			wrongTouchUpEffects.add(makeResetPositionEffect(entity));
		}
		if (touchUpEffectIfWrong != null) {
			wrongTouchUpEffects.add(touchUpEffectIfWrong);
		}

		RemoveComponent removeColorCluesEffect = b
				.makeRemoveComponent(Move.class);
		List<Effect> touchUpEffectsLists = new ArrayList<Effect>();
		RemoveComponent removeSticky = new RemoveComponent();
		removeSticky.setComponent("sticky");
		touchUpEffectsLists.add(removeColorCluesEffect);
		touchUpEffectsLists.add(removeSticky);
		if (touchUpEffect != null) {
			touchUpEffectsLists.add(touchUpEffect);
		}
		if (correctDropTargetTag != null) {
			touchUpEffectsLists.add(b.makeIfElse(
					b.eb.intersectsThisByTag(correctDropTargetTag),
					rightTouchUpEffects, wrongTouchUpEffects));
		}
		Behavior touchUp = b.makeTouchBehavior(touchUpEffectsLists
				.toArray(new Effect[] {}));

		Touch touchUpEvent = (Touch) touchUp.getEvent();
		touchUpEvent.setType(Touch.Type.CLICK);
		entity.getComponents().add(touchUp);
		return touchUp;
	}

	/**
	 * Makes an effect that updates variable {@link #INTERSECTED_ENTITY_VAR}
	 * with the first entity that intersects the "this" entity having the
	 * correctDroppableTag.
	 * 
	 * @return The ChangeVar effect created
	 */
	protected Effect makeCalculateIntersectionEffect(String correctDroppableTag) {
		ChangeVar calculateIntersection = b.makeChangeVar(
				INTERSECTED_ENTITY_VAR,
				b.eb.firstToIntersectThisByTag(correctDroppableTag),
				ChangeVar.Context.GLOBAL);
		return calculateIntersection;
	}

	/**
	 * Makes an effect that will force the "this" entity move instantaneously to
	 * the position of the entity that intersected with it after running effect
	 * {@link #makeCalculateIntersectionEffect(String)}.
	 * 
	 * This is useful to make, for example, that once a puzzle piece is dropped
	 * onto its place holder, it fits the hole perfectly without needing to do
	 * the fine-tune positioning by hand.
	 * 
	 * @return The effect created
	 */
	protected Effect makeStickToDroppableEffect() {
		Tween setPosition = b.makeTween(MoveTween.class, 0F, 0, 0F, false,
				0.1F, false, Tween.EaseEquation.EXPO, Tween.EaseType.IN, 1F,
				2F, null, null);
		b.parameter(setPosition, "x", b.eb.entityVarX(INTERSECTED_ENTITY_VAR));
		b.parameter(setPosition, "y", b.eb.entityVarY(INTERSECTED_ENTITY_VAR));
		return b.makeAddComponent(setPosition);
	}

	/**
	 * Creates two variables to save the current (x,y) position of the given
	 * entity. These variables will be used later on, for example, by
	 * {@link #makeResetPositionEffect(ModelEntity)}
	 */
	protected void saveInitialPosition(ModelEntity entity) {
		checkAddEntityVars(entity);

		String initialXVar = entityVars.get(entity).initialX;
		String initialYVar = entityVars.get(entity).initialY;
		Effect saveInitialPos = b.makeEffectsGroup(
				b.makeChangeVar(initialXVar, "(prop " + b.eb.thisEntity()
						+ " sgroup.x)", ChangeVar.Context.GLOBAL),
				b.makeChangeVar(initialYVar, "(prop " + b.eb.thisEntity()
						+ " sgroup.y)", ChangeVar.Context.GLOBAL));
		b.initBehavior(entity, saveInitialPos);
	}

	/**
	 * Adds the classic effect in drag&drop games that makes draggable items
	 * come back to their original positions if they are not dropped on the
	 * correct place holder.
	 * 
	 * To be more specific, this is what this method does: 1. Checks if the
	 * original position of the entity has been previously recorded by invoking
	 * {@link #saveInitialPosition(ModelEntity)}. If not, it registers the
	 * current position of the entity as the original one 2. Makes an effect
	 * that will dynamically move the entity to the original position.
	 * 
	 * @return An effect that will add the tween to reset the entity to its
	 *         original position to the entity
	 */
	public Effect makeResetPositionEffect(ModelEntity entity) {
		saveInitialPosition(entity);
		String initialXVar = entityVars.get(entity).initialX;
		String initialYVar = entityVars.get(entity).initialY;

		Tween resetPosition = b.makeTween(MoveTween.class, 0F, 0, 0F, false,
				0.15F, false, Tween.EaseEquation.EXPO, Tween.EaseType.IN, 1F,
				2F, null, null);
		b.parameter(resetPosition, "x",
				b.eb.varReference(b.eb.varReference(initialXVar)));
		b.parameter(resetPosition, "y",
				b.eb.varReference(b.eb.varReference(initialYVar)));
		return b.makeAddComponent(resetPosition);
	}

	private void checkAddEntityVars(ModelEntity entity) {
		if (!entityVars.containsKey(entity)) {
			EntityVars entVars = new EntityVars();
			entVars.initialX = b.newAutoVar();
			entVars.initialY = b.newAutoVar();
			entityVars.put(entity, entVars);
		}
	}

	private static class EntityVars {
		String initialX;
		String initialY;
	}
}
