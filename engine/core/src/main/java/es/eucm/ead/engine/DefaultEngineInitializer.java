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

import ashley.core.Entity;
import ashley.core.Family;
import es.eucm.ead.engine.assets.GameAssets;
import es.eucm.ead.engine.components.I18nTextComponent;
import es.eucm.ead.engine.processors.CamerasProcessor;
import es.eucm.ead.engine.processors.PathProcessor;
import es.eucm.ead.engine.processors.RefProcessor;
import es.eucm.ead.engine.processors.TagsProcessor;
import es.eucm.ead.engine.processors.VisibilityProcessor;
import es.eucm.ead.engine.processors.assets.SoundProcessor;
import es.eucm.ead.engine.processors.behaviors.BehaviorsProcessor;
import es.eucm.ead.engine.processors.controls.ButtonProcessor;
import es.eucm.ead.engine.processors.controls.ImageButtonProcessor;
import es.eucm.ead.engine.processors.controls.LabelProcessor;
import es.eucm.ead.engine.processors.controls.TextButtonProcessor;
import es.eucm.ead.engine.processors.physics.VelocityProcessor;
import es.eucm.ead.engine.processors.renderers.EmptyRendererProcessor;
import es.eucm.ead.engine.processors.renderers.FramesProcessor;
import es.eucm.ead.engine.processors.renderers.ImageProcessor;
import es.eucm.ead.engine.processors.renderers.ShapeRendererProcessor;
import es.eucm.ead.engine.processors.renderers.StatesProcessor;
import es.eucm.ead.engine.processors.tweens.TweensProcessor;
import es.eucm.ead.engine.systems.EffectsSystem;
import es.eucm.ead.engine.systems.KeyPressedSystem;
import es.eucm.ead.engine.systems.PathSystem;
import es.eucm.ead.engine.systems.RemoveEntitiesSystem;
import es.eucm.ead.engine.systems.SoundSystem;
import es.eucm.ead.engine.systems.TouchedSystem;
import es.eucm.ead.engine.systems.VelocitySystem;
import es.eucm.ead.engine.systems.VisibilitySystem;
import es.eucm.ead.engine.systems.behaviors.KeyBehaviorSystem;
import es.eucm.ead.engine.systems.behaviors.TimersSystem;
import es.eucm.ead.engine.systems.behaviors.TouchBehaviorSystem;
import es.eucm.ead.engine.systems.effects.AddAnimationExecutor;
import es.eucm.ead.engine.systems.effects.AddComponentExecutor;
import es.eucm.ead.engine.systems.effects.AddEntityExecutor;
import es.eucm.ead.engine.systems.effects.ChangeEntityPropertyExecutor;
import es.eucm.ead.engine.systems.effects.ChangeVarExecutor;
import es.eucm.ead.engine.systems.effects.EndGameExecutor;
import es.eucm.ead.engine.systems.effects.GoSceneExecutor;
import es.eucm.ead.engine.systems.effects.GoToExecutor;
import es.eucm.ead.engine.systems.effects.RemoveComponentExecutor;
import es.eucm.ead.engine.systems.effects.RemoveEntityExecutor;
import es.eucm.ead.engine.systems.effects.SetCameraExecutor;
import es.eucm.ead.engine.systems.effects.SetViewportExecutor;
import es.eucm.ead.engine.systems.effects.controlstructures.ForEachExecutor;
import es.eucm.ead.engine.systems.effects.controlstructures.IfExecutor;
import es.eucm.ead.engine.systems.effects.controlstructures.IfThenElseIfExecutor;
import es.eucm.ead.engine.systems.effects.controlstructures.RepeatExecutor;
import es.eucm.ead.engine.systems.effects.controlstructures.ScriptCallExecutor;
import es.eucm.ead.engine.systems.effects.controlstructures.WhileExecutor;
import es.eucm.ead.engine.systems.tweens.TweenSystem;
import es.eucm.ead.engine.systems.tweens.tweencreators.AlphaTweenCreator;
import es.eucm.ead.engine.systems.tweens.tweencreators.FieldTweenCreator;
import es.eucm.ead.engine.systems.tweens.tweencreators.MoveTweenCreator;
import es.eucm.ead.engine.systems.tweens.tweencreators.RotateTweenCreator;
import es.eucm.ead.engine.systems.tweens.tweencreators.ScaleTweenCreator;
import es.eucm.ead.engine.systems.tweens.tweencreators.TimelineCreator;
import es.eucm.ead.engine.variables.VariablesManager;
import es.eucm.ead.engine.variables.VarsContext;
import es.eucm.ead.schema.assets.Sound;
import es.eucm.ead.schema.components.PathBoundary;
import es.eucm.ead.schema.components.RefComponent;
import es.eucm.ead.schema.components.Tags;
import es.eucm.ead.schema.components.Visibility;
import es.eucm.ead.schema.components.behaviors.Behavior;
import es.eucm.ead.schema.components.cameras.Cameras;
import es.eucm.ead.schema.components.controls.Button;
import es.eucm.ead.schema.components.controls.ImageButton;
import es.eucm.ead.schema.components.controls.Label;
import es.eucm.ead.schema.components.controls.TextButton;
import es.eucm.ead.schema.components.physics.Velocity;
import es.eucm.ead.schema.components.renderers.RefRenderer;
import es.eucm.ead.schema.components.tweens.AlphaTween;
import es.eucm.ead.schema.components.tweens.FieldTween;
import es.eucm.ead.schema.components.tweens.MoveTween;
import es.eucm.ead.schema.components.tweens.RotateTween;
import es.eucm.ead.schema.components.tweens.ScaleTween;
import es.eucm.ead.schema.components.tweens.Timeline;
import es.eucm.ead.schema.effects.AddAnimation;
import es.eucm.ead.schema.effects.AddComponent;
import es.eucm.ead.schema.effects.AddEntity;
import es.eucm.ead.schema.effects.ChangeEntityProperty;
import es.eucm.ead.schema.effects.ChangeVar;
import es.eucm.ead.schema.effects.EndGame;
import es.eucm.ead.schema.effects.GoScene;
import es.eucm.ead.schema.effects.GoTo;
import es.eucm.ead.schema.effects.RemoveComponent;
import es.eucm.ead.schema.effects.RemoveEntity;
import es.eucm.ead.schema.effects.SetCamera;
import es.eucm.ead.schema.effects.SetViewport;
import es.eucm.ead.schema.effects.controlstructures.ForEach;
import es.eucm.ead.schema.effects.controlstructures.If;
import es.eucm.ead.schema.effects.controlstructures.IfThenElseIf;
import es.eucm.ead.schema.effects.controlstructures.Repeat;
import es.eucm.ead.schema.effects.controlstructures.ScriptCall;
import es.eucm.ead.schema.effects.controlstructures.While;
import es.eucm.ead.schema.renderers.EmptyRenderer;
import es.eucm.ead.schema.renderers.Frames;
import es.eucm.ead.schema.renderers.Image;
import es.eucm.ead.schema.renderers.ShapeRenderer;
import es.eucm.ead.schema.renderers.States;

/**
 * Created by angel on 10/04/14.
 */
public class DefaultEngineInitializer implements EngineInitializer {

	@Override
	public void init(GameAssets assets, GameLoop gameLoop,
			EntitiesLoader entitiesLoader, GameView gameView,
			VariablesManager variablesManager) {
		registerComponents(entitiesLoader.getComponentLoader(), assets,
				gameLoop, variablesManager);
		registerSystems(assets, gameLoop, entitiesLoader, gameView,
				variablesManager);
	}

	private void registerSystems(final GameAssets gameAssets,
			final GameLoop gameLoop, final EntitiesLoader entitiesLoader,
			final GameView gameView, final VariablesManager variablesManager) {

		final ComponentLoader componentLoader = entitiesLoader
				.getComponentLoader();

		TweenSystem tweenSystem = new TweenSystem();

		gameLoop.addSystem(new TouchBehaviorSystem(gameLoop, variablesManager));
		gameLoop.addSystem(new TimersSystem(gameLoop, variablesManager));
		gameLoop.addSystem(new KeyBehaviorSystem(gameLoop, variablesManager));
		gameLoop.addSystem(new VelocitySystem());
		gameLoop.addSystem(tweenSystem);
		gameLoop.addSystem(new VisibilitySystem(gameLoop, variablesManager));
		gameLoop.addSystem(new PathSystem());
		gameLoop.addSystem(new RemoveEntitiesSystem(gameLoop, variablesManager));
		gameLoop.addSystem(new TouchedSystem());
		gameLoop.addSystem(new KeyPressedSystem());
		gameLoop.addSystem(new SoundSystem(variablesManager));

		// Register effects
		EffectsSystem effectsSystem = new EffectsSystem(gameLoop,
				variablesManager);
		gameLoop.addSystem(effectsSystem);

		effectsSystem.registerEffectExecutor(GoScene.class,
				new GoSceneExecutor(entitiesLoader, gameView));
		effectsSystem.registerEffectExecutor(EndGame.class,
				new EndGameExecutor());
		effectsSystem.registerEffectExecutor(ChangeVar.class,
				new ChangeVarExecutor(variablesManager));
		effectsSystem.registerEffectExecutor(AddComponent.class,
				new AddComponentExecutor(componentLoader));
		effectsSystem.registerEffectExecutor(GoTo.class, new GoToExecutor());
		effectsSystem.registerEffectExecutor(RemoveComponent.class,
				new RemoveComponentExecutor(componentLoader));
		effectsSystem.registerEffectExecutor(RemoveEntity.class,
				new RemoveEntityExecutor());
		effectsSystem.registerEffectExecutor(ChangeEntityProperty.class,
				new ChangeEntityPropertyExecutor(variablesManager));
		effectsSystem.registerEffectExecutor(AddAnimation.class,
				new AddAnimationExecutor());
		effectsSystem.registerEffectExecutor(AddEntity.class,
				new AddEntityExecutor(entitiesLoader, variablesManager));
		effectsSystem.registerEffectExecutor(SetCamera.class,
				new SetCameraExecutor(gameView, variablesManager));
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
		tweenSystem.registerBaseTweenCreator(Timeline.class,
				new TimelineCreator(tweenSystem.getBaseTweenCreators()));

		// Variables listeners
		variablesManager.addListener(new LanguageVariableListener(gameLoop,
				gameAssets));
	}

	private void registerComponents(ComponentLoader componentLoader,
			GameAssets gameAssets, GameLoop gameLoop,
			VariablesManager variablesManager) {
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
				new EmptyRendererProcessor(gameLoop));
		componentLoader.registerComponentProcessor(Velocity.class,
				new VelocityProcessor(gameLoop));
		componentLoader.registerComponentProcessor(Button.class,
				new ButtonProcessor(gameLoop, gameAssets));
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

		componentLoader.registerComponentProcessor(Visibility.class,
				new VisibilityProcessor(gameLoop));
		componentLoader.registerComponentProcessor(PathBoundary.class,
				new PathProcessor(gameLoop));
		componentLoader.registerComponentProcessor(Cameras.class,
				new CamerasProcessor(gameLoop));
		componentLoader.registerComponentProcessor(RefComponent.class,
				new RefProcessor<RefComponent>(gameLoop, gameAssets,
						componentLoader));
		componentLoader.registerComponentProcessor(Sound.class,
				new SoundProcessor(gameLoop, gameAssets));
		componentLoader.registerComponentProcessor(RefRenderer.class,
				new RefProcessor<RefRenderer>(gameLoop, gameAssets,
						componentLoader));
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
			for (Entity entity : gameLoop.getEntitiesFor(
					Family.getFamilyFor(I18nTextComponent.class)).values()) {
				I18nTextComponent text = entity
						.getComponent(I18nTextComponent.class);
				text.getText().setText(
						gameAssets.getI18N().m(text.getI18nKey()));
			}
		}
	}
}
