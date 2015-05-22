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
package es.eucm.ead.engine;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import es.eucm.ead.engine.assets.GameAssets;
import es.eucm.ead.engine.components.I18nTextComponent;
import es.eucm.ead.engine.processors.*;
import es.eucm.ead.engine.processors.assets.ReferenceProcessor;
import es.eucm.ead.engine.processors.assets.SoundProcessor;
import es.eucm.ead.engine.processors.behaviors.BehaviorsProcessor;
import es.eucm.ead.engine.processors.controls.ButtonProcessor;
import es.eucm.ead.engine.processors.controls.ImageButtonProcessor;
import es.eucm.ead.engine.processors.controls.LabelProcessor;
import es.eucm.ead.engine.processors.controls.TextButtonProcessor;
import es.eucm.ead.engine.processors.controls.layouts.VerticalLayoutProcessor;
import es.eucm.ead.engine.processors.physics.*;
import es.eucm.ead.engine.processors.positiontracking.ChaseEntityProcessor;
import es.eucm.ead.engine.processors.positiontracking.MoveByEntityProcessor;
import es.eucm.ead.engine.processors.positiontracking.ParallaxProcessor;
import es.eucm.ead.engine.processors.renderers.*;
import es.eucm.ead.engine.processors.tweens.TweensProcessor;
import es.eucm.ead.engine.systems.*;
import es.eucm.ead.engine.systems.behaviors.KeyBehaviorSystem;
import es.eucm.ead.engine.systems.behaviors.TimersSystem;
import es.eucm.ead.engine.systems.behaviors.TouchBehaviorSystem;
import es.eucm.ead.engine.systems.conversations.*;
import es.eucm.ead.engine.systems.effects.*;
import es.eucm.ead.engine.systems.effects.controlstructures.*;
import es.eucm.ead.engine.systems.effects.effecttotween.AlphaEffectToTween;
import es.eucm.ead.engine.systems.effects.effecttotween.MoveEffectToTween;
import es.eucm.ead.engine.systems.effects.effecttotween.RotateEffectToTween;
import es.eucm.ead.engine.systems.effects.effecttotween.ScaleEffectToTween;
import es.eucm.ead.engine.systems.positiontracking.ChaseEntitySystem;
import es.eucm.ead.engine.systems.positiontracking.MoveByEntitySystem;
import es.eucm.ead.engine.systems.tweens.TweenSystem;
import es.eucm.ead.engine.systems.tweens.tweencreators.*;
import es.eucm.ead.engine.variables.VariablesManager;
import es.eucm.ead.engine.variables.VarsContext;
import es.eucm.ead.schema.assets.Sound;
import es.eucm.ead.schema.components.*;
import es.eucm.ead.schema.components.behaviors.Behavior;
import es.eucm.ead.schema.components.cameras.Cameras;
import es.eucm.ead.schema.components.controls.Button;
import es.eucm.ead.schema.components.controls.ImageButton;
import es.eucm.ead.schema.components.controls.Label;
import es.eucm.ead.schema.components.controls.TextButton;
import es.eucm.ead.schema.components.controls.layouts.VerticalLayout;
import es.eucm.ead.schema.components.conversation.*;
import es.eucm.ead.schema.components.physics.*;
import es.eucm.ead.schema.components.positiontracking.ChaseEntity;
import es.eucm.ead.schema.components.positiontracking.MoveByEntity;
import es.eucm.ead.schema.components.positiontracking.Parallax;
import es.eucm.ead.schema.components.tweens.*;
import es.eucm.ead.schema.effects.*;
import es.eucm.ead.schema.effects.controlstructures.*;
import es.eucm.ead.schema.renderers.*;

/**
 * Default initializer. Used by default engine and editor.
 * 
 * Created by angel on 10/04/14.
 */
public class DefaultEngineInitializer implements EngineInitializer {

	@Override
	public void init(GameAssets assets, GameLoop gameLoop,
			EntitiesLoader entitiesLoader, GameView gameView,
			VariablesManager variablesManager) {

		registerComponents(entitiesLoader.getComponentLoader(), assets,
				gameLoop, variablesManager, entitiesLoader);
		registerSystems(assets, gameLoop, entitiesLoader, gameView,
				variablesManager);
	}

	protected void registerSystems(final GameAssets gameAssets,
			final GameLoop gameLoop, final EntitiesLoader entitiesLoader,
			final GameView gameView, final VariablesManager variablesManager) {

		final ComponentLoader componentLoader = entitiesLoader
				.getComponentLoader();

		TweenSystem tweenSystem = new TweenSystem();

		gameLoop.addSystem(new TouchBehaviorSystem(gameLoop, variablesManager));
		gameLoop.addSystem(new TimersSystem(gameLoop, variablesManager));
		gameLoop.addSystem(new KeyBehaviorSystem(gameLoop, variablesManager));
		MassSystem massSystem = new MassSystem(gameLoop);
		GravitySystem gravitySystem = new GravitySystem(gameLoop, massSystem);
		gameLoop.addSystem(gravitySystem);
		gameLoop.addSystem(new AccelerationSystem(gameLoop));
		gameLoop.addSystem(massSystem);
		gameLoop.addSystem(new VelocitySystem());
		gameLoop.addSystem(tweenSystem);
		gameLoop.addSystem(new VisibilitySystem(gameLoop, variablesManager));
		gameLoop.addSystem(new TouchabilitySystem(gameLoop, variablesManager));
		gameLoop.addSystem(new PathSystem());
		gameLoop.addSystem(new RemoveEntitiesSystem(gameLoop, variablesManager));
		gameLoop.addSystem(new TouchedSystem());
		gameLoop.addSystem(new KeyPressedSystem());
		gameLoop.addSystem(new SoundSystem(variablesManager));
		gameLoop.addSystem(new MainSystem(variablesManager));
		gameLoop.addSystem(new ChaseEntitySystem(gameLoop, variablesManager));
		gameLoop.addSystem(new MoveByEntitySystem(gameLoop, variablesManager));

		// Register nodes
		NodeSystem nodeSystem = new NodeSystem(gameLoop);
		nodeSystem.registerNodeClass(WaitNode.class, WaitRuntimeNode.class);
		nodeSystem.registerNodeClass(LineNode.class, LineRuntimeNode.class);
		nodeSystem.registerNodeClass(EffectsNode.class,
				EffectsRuntimeNode.class);
		nodeSystem.registerNodeClass(ConditionedNode.class,
				ConditionedRuntimeNode.class);
		nodeSystem.registerNodeClass(OptionNode.class, OptionRuntimeNode.class);
		gameLoop.addSystem(nodeSystem);
		gameLoop.addSystem(new LineSystem(gameAssets, entitiesLoader, gameView));
		gameLoop.addSystem(new OptionsSystem(gameAssets, entitiesLoader,
				gameView));

		// Register effects
		EffectsSystem effectsSystem = new EffectsSystem(gameLoop,
				variablesManager, gameAssets);
		gameLoop.addSystem(effectsSystem);

		effectsSystem.registerEffectExecutor(GoScene.class,
				new GoSceneExecutor(entitiesLoader, gameView, gameAssets));
		effectsSystem.registerEffectExecutor(EndGame.class,
				new EndGameExecutor());
		effectsSystem.registerEffectExecutor(ChangeVar.class,
				new ChangeVarExecutor(variablesManager));
		effectsSystem.registerEffectExecutor(AddComponent.class,
				new AddComponentExecutor(componentLoader));
		effectsSystem.registerEffectExecutor(GoTo.class, new GoToExecutor());
		effectsSystem.registerEffectExecutor(RemoveComponent.class,
				new RemoveComponentExecutor(componentLoader, tweenSystem));
		effectsSystem.registerEffectExecutor(RemoveEntity.class,
				new RemoveEntityExecutor());
		effectsSystem.registerEffectExecutor(ChangeEntityProperty.class,
				new ChangeEntityPropertyExecutor(variablesManager));
		effectsSystem.registerEffectExecutor(ScriptCall.class,
				new ScriptCallExecutor(effectsSystem, variablesManager));
		effectsSystem.registerEffectExecutor(AddEntity.class,
				new AddEntityExecutor(entitiesLoader, variablesManager));
		effectsSystem.registerEffectExecutor(SetCamera.class,
				new SetCameraExecutor(gameView, variablesManager));
		effectsSystem.registerEffectExecutor(PlaySound.class,
				new PlaySoundExecutor(effectsSystem));

		TrackEffectExecutor timelineExecutor = new TrackEffectExecutor(
				effectsSystem);
		timelineExecutor.registerTween(MoveEffect.class,
				new MoveEffectToTween());
		timelineExecutor.registerTween(ScaleEffect.class,
				new ScaleEffectToTween());
		timelineExecutor.registerTween(AlphaEffect.class,
				new AlphaEffectToTween());
		timelineExecutor.registerTween(RotateEffect.class,
				new RotateEffectToTween());
		effectsSystem.registerEffectExecutor(TrackEffect.class,
				timelineExecutor);

		// Control structures
		effectsSystem.registerEffectExecutor(ScriptCall.class,
				new ScriptCallExecutor(effectsSystem, variablesManager));
		effectsSystem.registerEffectExecutor(IfThenElseIf.class,
				new IfThenElseIfExecutor(effectsSystem, variablesManager));
		effectsSystem.registerEffectExecutor(If.class, new IfExecutor(
				effectsSystem, variablesManager));
		effectsSystem.registerEffectExecutor(While.class, new WhileExecutor(
				effectsSystem, variablesManager));
		effectsSystem.registerEffectExecutor(Repeat.class, new RepeatExecutor(
				effectsSystem, variablesManager));
		effectsSystem.registerEffectExecutor(ForEach.class,
				new ForEachExecutor(effectsSystem, variablesManager));
		effectsSystem.registerEffectExecutor(SetViewport.class,
				new SetViewportExecutor(gameView, variablesManager));
		effectsSystem.registerEffectExecutor(TriggerConversation.class,
				new TriggerConversationExecutor());
		effectsSystem.registerEffectExecutor(ChangeState.class,
				new ChangeStateExecutor());

		// Register tweens
		tweenSystem.registerBaseTweenCreator(MoveTween.class,
				new MoveTweenCreator());
		tweenSystem.registerBaseTweenCreator(RotateTween.class,
				new RotateTweenCreator());
		tweenSystem.registerBaseTweenCreator(ScaleTween.class,
				new ScaleTweenCreator());
		tweenSystem.registerBaseTweenCreator(FieldTween.class,
				new FieldTweenCreator(componentLoader));
		tweenSystem.registerBaseTweenCreator(AlphaTween.class,
				new AlphaTweenCreator());
		tweenSystem.registerBaseTweenCreator(EffectTween.class,
				new EffectTweenCreator(gameLoop, effectsSystem));
		tweenSystem.registerBaseTweenCreator(Timeline.class,
				new TimelineCreator(tweenSystem.getBaseTweenCreators()));

		// Variables listeners
		variablesManager.addListener(new LanguageVariableListener(gameLoop,
				gameAssets));
	}

	protected void registerComponents(ComponentLoader componentLoader,
			GameAssets gameAssets, GameLoop gameLoop,
			VariablesManager variablesManager, EntitiesLoader entitiesLoader) {
		// Components
		componentLoader.registerComponentProcessor(Tags.class,
				new TagsProcessor(gameLoop));
		componentLoader.registerComponentProcessor(Image.class,
				new ImageProcessor(gameLoop, gameAssets));
		componentLoader.registerComponentProcessor(Frames.class,
				new FramesProcessor(gameLoop, gameAssets, componentLoader));
		componentLoader.registerComponentProcessor(ShapeRenderer.class,
				new ShapeRendererProcessor(gameLoop));
		componentLoader.registerComponentProcessor(EmptyRenderer.class,
				new EmptyRendererProcessor(gameLoop, gameAssets));
		componentLoader.registerComponentProcessor(Velocity.class,
				new VelocityProcessor(gameLoop));
		componentLoader.registerComponentProcessor(Acceleration.class,
				new AccelerationProcessor(gameLoop));
		componentLoader.registerComponentProcessor(Gravity.class,
				new GravityProcessor(gameLoop));
		componentLoader.registerComponentProcessor(Mass.class,
				new MassProcessor(gameLoop));
		componentLoader.registerComponentProcessor(Button.class,
				new ButtonProcessor(gameLoop, gameAssets));
		componentLoader.registerComponentProcessor(VerticalLayout.class,
				new VerticalLayoutProcessor(gameLoop, componentLoader));
		componentLoader
				.registerComponentProcessor(TextButton.class,
						new TextButtonProcessor(gameLoop, gameAssets,
								variablesManager));
		componentLoader.registerComponentProcessor(ImageButton.class,
				new ImageButtonProcessor(gameLoop, gameAssets));
		componentLoader.registerComponentProcessor(Label.class,
				new LabelProcessor(gameLoop, gameAssets, variablesManager));
		componentLoader.registerComponentProcessor(Behavior.class,
				new BehaviorsProcessor(gameLoop));

		componentLoader.registerComponentProcessor(States.class,
				new StatesProcessor(gameLoop, gameAssets, componentLoader));

		componentLoader.registerComponentProcessor(Cameras.class,
				new CamerasProcessor(gameLoop));

		TweensProcessor tweensProcessor = new TweensProcessor(gameLoop);
		componentLoader.registerComponentProcessor(AlphaTween.class,
				tweensProcessor);
		componentLoader.registerComponentProcessor(FieldTween.class,
				tweensProcessor);
		componentLoader.registerComponentProcessor(MoveTween.class,
				tweensProcessor);
		componentLoader.registerComponentProcessor(RotateTween.class,
				tweensProcessor);
		componentLoader.registerComponentProcessor(ScaleTween.class,
				tweensProcessor);
		componentLoader.registerComponentProcessor(Timeline.class,
				tweensProcessor);

		componentLoader.registerComponentProcessor(Animation.class,
				new AnimationProcessor(gameLoop));

		componentLoader.registerComponentProcessor(Visibility.class,
				new VisibilityProcessor(gameLoop));
		componentLoader.registerComponentProcessor(Touchability.class,
				new TouchabilityProcessor(gameLoop));
		componentLoader.registerComponentProcessor(PathBoundary.class,
				new PathProcessor(gameLoop));
		componentLoader.registerComponentProcessor(Cameras.class,
				new CamerasProcessor(gameLoop));
		componentLoader.registerComponentProcessor(Sound.class,
				new SoundProcessor(gameLoop, gameAssets));
		componentLoader.registerComponentProcessor(BoundingArea.class,
				new BoundingAreaProcessor(gameLoop));
		componentLoader.registerComponentProcessor(MoveByEntity.class,
				new MoveByEntityProcessor(gameLoop));
		componentLoader.registerComponentProcessor(ChaseEntity.class,
				new ChaseEntityProcessor(gameLoop));
		componentLoader.registerComponentProcessor(Parallax.class,
				new ParallaxProcessor(gameLoop));

		componentLoader.registerComponentProcessor(Conversation.class,
				new ConversationProcessor(gameLoop));
		componentLoader.registerComponentProcessor(SpineAnimation.class,
				new SpineProcessor(gameLoop, gameAssets));
		componentLoader.registerComponentProcessor(Reference.class,
				new ReferenceProcessor(gameLoop, gameAssets, entitiesLoader));
		componentLoader.registerComponentProcessor(Shader.class,
				new ShaderProcessor(gameLoop, gameAssets, variablesManager));
		componentLoader.registerComponentProcessor(Background.class,
				new BackgroundProcessor(gameLoop, entitiesLoader));
	}

	private static class LanguageVariableListener implements
			VariablesManager.VariableListener {

		private GameLoop gameLoop;

		private GameAssets gameAssets;

		private LanguageVariableListener(GameLoop gameLoop,
				GameAssets gameAssets) {
			this.gameLoop = gameLoop;
			this.gameAssets = gameAssets;
		}

		@Override
		public boolean listensTo(String variableName) {
			return VarsContext.LANGUAGE_VAR.equals(variableName);
		}

		@Override
		public void variableChanged(String variableName, Object value) {
			gameAssets.getI18N().setLang(value + "");
			for (Entity entity : gameLoop.getEntitiesFor(Family.all(
					I18nTextComponent.class).get())) {
				I18nTextComponent text = entity
						.getComponent(I18nTextComponent.class);
				text.getText().setText(
						gameAssets.getI18N().m(text.getI18nKey()));
			}
		}
	}
}
