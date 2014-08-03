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
package es.eucm.ead.editor.ui.scenes.ribbon.animation;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.model.AddAnimation;
import es.eucm.ead.editor.control.actions.model.AddTimedEffectInTrack;
import es.eucm.ead.editor.control.actions.model.AddTrackEffect;
import es.eucm.ead.editor.indexes.EffectsIndex;
import es.eucm.ead.editor.indexes.FuzzyIndex.Term;
import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.Model.SelectionListener;
import es.eucm.ead.editor.model.events.ListEvent;
import es.eucm.ead.editor.model.events.SelectionEvent;
import es.eucm.ead.editor.view.widgets.EditTweensBar;
import es.eucm.ead.editor.view.widgets.IconButton;
import es.eucm.ead.editor.view.widgets.IconTextButton;
import es.eucm.ead.editor.view.widgets.IconTextButton.Position;
import es.eucm.ead.editor.view.widgets.Separator;
import es.eucm.ead.editor.view.widgets.TimelineWidget;
import es.eucm.ead.editor.view.widgets.dragndrop.DraggableScrollPane;
import es.eucm.ead.editor.view.widgets.layouts.TrackLayout;
import es.eucm.ead.editor.view.widgets.timeline.GradualTrackEffectButton;
import es.eucm.ead.editor.view.widgets.timeline.InstantTrackEffectButton;
import es.eucm.ead.editor.view.widgets.timeline.TrackEffectLayout;
import es.eucm.ead.schema.components.Animation;
import es.eucm.ead.schema.components.ModelComponent;
import es.eucm.ead.schema.effects.AnimationEffect;
import es.eucm.ead.schema.effects.Effect;
import es.eucm.ead.schema.effects.TimedEffect;
import es.eucm.ead.schema.effects.TrackEffect;
import es.eucm.ead.schema.entities.ModelEntity;

/**
 * 
 * Widget for edit {@link Animation}s
 * 
 */
public class AnimationEdition extends Table {

	private static final float PIXEL_PER_SECOND = 10;

	private Skin skin;

	private Map<IconTextButton, Class<Effect>> buttonReg;

	private Map<Class<Effect>, Drawable> drawableReg;

	private Drawable backgroundTweens;

	public AnimationEdition(Controller controller) {
		super();
		skin = controller.getApplicationAssets().getSkin();

		align(Align.top);

		DragAndDrop dragAndDrop = new DragAndDrop();

		buttonReg = new HashMap<IconTextButton, Class<Effect>>();
		drawableReg = new HashMap<Class<Effect>, Drawable>();

		backgroundTweens = skin.getDrawable("bg-dark");

		add(new TweensTypeTab(dragAndDrop, controller)).left();
		row();
		AnimationTimeline timeline = new AnimationTimeline(dragAndDrop,
				controller);
		add(timeline).expandX().fill();
		row();
		add(new Separator(true, skin));
		row();
		add(timeline.getAddButton()).fill().expandX();
		row();
		add(new Separator(true, skin));
	}

	/**
	 * 
	 * The top of {@link AnimationEdition} widget. Shows all types of effects
	 * that can be used in an animation.
	 * 
	 */
	private class TweensTypeTab extends EditTweensBar {

		private static final String ICON_SIZE = "48x48";

		private Controller controller;

		public TweensTypeTab(DragAndDrop dragAndDrop, Controller controller) {
			super(skin.getDrawable("blank"), skin.getDrawable("bg-dark"),
					dragAndDrop, controller);
			this.controller = controller;
			initialize(controller);
		}

		public void initialize(Controller controller) {
			EffectsIndex events = controller.getIndex(EffectsIndex.class);
			for (Term term : events.getAnimationTypeEffects()) {
				Class eventClass = (Class) term.getData();
				String eventPrefix = eventClass.getSimpleName().toLowerCase();

				IconTextButton iconButton;
				iconButton = new IconTextButton(term.getTermString(), skin,
						skin.getDrawable(eventPrefix + ICON_SIZE),
						Position.BOTTOM);
				iconButton.setName(eventPrefix);
				buttonReg.put(iconButton, (Class) term.getData());
				drawableReg.put((Class) term.getData(),
						skin.getDrawable(eventPrefix + ICON_SIZE));
				addGradual(iconButton);
			}

			for (Term term : events.getInstantTypeEffects()) {
				Class eventClass = (Class) term.getData();
				String eventPrefix = eventClass.getSimpleName().toLowerCase();
				IconTextButton iconButton;
				try { // try catch necessary because not all effects have its
						// icon.
					iconButton = new IconTextButton(term.getTermString(), skin,
							skin.getDrawable(eventPrefix + ICON_SIZE),
							Position.BOTTOM);
					drawableReg.put((Class) term.getData(),
							skin.getDrawable(eventPrefix + ICON_SIZE));
				} catch (Exception e) {
					iconButton = new IconTextButton(term.getTermString(), skin,
							skin.getDrawable("camera48x48"), Position.BOTTOM);
					drawableReg.put((Class) term.getData(),
							skin.getDrawable("camera48x48"));
				}
				iconButton.setName(eventPrefix);
				buttonReg.put(iconButton, (Class) term.getData());
				addInstant(iconButton);
			}
		}

		@Override
		protected Source createDefaultGradualSource(final IconTextButton actor) {
			return new Source(actor) {

				@Override
				public Payload dragStart(InputEvent event, float x, float y,
						int pointer) {

					gradualsTweens.cancelScrollFocus(false);

					Payload payload = new Payload();

					IconButton icon = new IconButton(actor.getDrawableImage(),
							skin);

					TimedEffect effect = new TimedEffect();
					try {
						effect.setEffect(buttonReg.get(actor).newInstance());
					} catch (Exception e) {
						Gdx.app.error("Exception", "Unexpected exception", e);
					}
					GradualTrackEffectButton gradual = new GradualTrackEffectButton(
							icon, actor.getDrawableImage().getMinWidth(),
							backgroundTweens, controller, effect,
							PIXEL_PER_SECOND);

					payload.setDragActor(gradual);
					return payload;
				}

				@Override
				public void dragStop(InputEvent event, float x, float y,
						int pointer, Payload payload, Target target) {
					if (target != null) {
						TrackEffect track = ((TrackEffectLayout) target
								.getActor()).getTrackEffect();
						TimedEffect effect = ((GradualTrackEffectButton) payload
								.getDragActor()).getEffect();
						effect.setTime(((TrackLayout) target.getActor())
								.getLeftMargin(payload.getDragActor())
								/ PIXEL_PER_SECOND);

						controller.action(AddTimedEffectInTrack.class, track,
								effect);
					}
					gradualsTweens.cancelScrollFocus(true);
				}

			};
		}

		@Override
		protected Source createDefaultInstantSource(final IconTextButton actor) {
			return new Source(actor) {

				@Override
				public Payload dragStart(InputEvent event, float x, float y,
						int pointer) {

					instantTweens.cancelScrollFocus(false);

					Payload payload = new Payload();

					TimedEffect effect = new TimedEffect();
					try {
						effect.setEffect(buttonReg.get(actor).newInstance());
					} catch (Exception e) {
						Gdx.app.error("Exception", "Unexpected exception", e);
					}
					InstantTrackEffectButton instant = new InstantTrackEffectButton(
							actor.getDrawableImage(), actor.getDrawableImage(),
							controller, effect, PIXEL_PER_SECOND);
					payload.setDragActor(instant);
					return payload;
				}

				@Override
				public void dragStop(InputEvent event, float x, float y,
						int pointer, Payload payload, Target target) {
					if (target != null) {
						TrackEffect track = ((TrackEffectLayout) target
								.getActor()).getTrackEffect();
						TimedEffect effect = ((InstantTrackEffectButton) payload
								.getDragActor()).getEffect();
						effect.setTime(((TrackLayout) target.getActor())
								.getLeftMargin(payload.getDragActor())
								/ PIXEL_PER_SECOND);
						controller.action(AddTimedEffectInTrack.class, track,
								effect);
					}
					instantTweens.cancelScrollFocus(true);
				}
			};
		}
	}

	/**
	 * 
	 * The bottom of {@link AnimationEdition} widget. Shows all tracks of
	 * timeline.
	 * 
	 */
	private class AnimationTimeline extends Table implements SelectionListener {

		private Array<TrackEffectLayout> trackLayouts;

		private Animation animation;

		private ScrollPane scroll;

		private DragAndDrop dragNDrop;

		private Container addNewTrack;

		private static final float DEFAULT_TRACK_HEIGHT = 50;

		private Controller controller;

		private ModelListener<ListEvent> animationListener;

		private ModelListener<ListEvent> trackListener;

		public AnimationTimeline(DragAndDrop dragAndDrop,
				final Controller controller) {

			align(Align.top);

			dragNDrop = dragAndDrop;
			Table tracks = new Table();
			scroll = new DraggableScrollPane(tracks, dragNDrop, 20, 50);

			trackLayouts = new Array<TrackEffectLayout>();

			add(scroll).fill().expand().left();

			this.controller = controller;

			addNewTrack = new Container(new IconButton(
					skin.getDrawable("plus24x24"), skin));
			addNewTrack.setBackground(skin.getDrawable("blank"));
			addNewTrack.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					controller.action(AddTrackEffect.class, animation,
							new TrackEffect());
				}
			});

			animation = new Animation();

			controller.getModel().addSelectionListener(this);
			animationListener = createListListener();
			trackListener = createTrackEffectListener();
			controller.getModel().addListListener(animation.getEffects(),
					animationListener);
		}

		public void refreshTimeline() {
			Table aux = ((Table) scroll.getWidget());
			trackLayouts.clear();

			aux.clear();

			aux.add(new Separator(true, skin));
			aux.row();
			aux.add(new TimelineWidget(skin, DEFAULT_TRACK_HEIGHT / 3,
					PIXEL_PER_SECOND, 10)).expandX();
			TrackEffectLayout track = null;
			for (TrackEffect trackEffect : animation.getEffects()) {
				track = new TrackEffectLayout(skin.getTiledDrawable("line"),
						dragNDrop) {
					@Override
					public float getPrefHeight() {
						return Math.max(super.getPrefHeight(),
								DEFAULT_TRACK_HEIGHT);
					}
				};
				track.setTrackEffect(trackEffect);
				trackLayouts.add(track);
				for (TimedEffect timedEffect : trackEffect.getEffects()) {
					Actor actor = null;
					if (timedEffect.getEffect() instanceof AnimationEffect) {
						IconButton icon = new IconButton(
								drawableReg.get(timedEffect.getEffect()
										.getClass()), skin);
						actor = new GradualTrackEffectButton(icon, drawableReg
								.get(timedEffect.getEffect().getClass())
								.getMinHeight(), backgroundTweens, controller,
								timedEffect, PIXEL_PER_SECOND);
					} else {
						actor = new InstantTrackEffectButton(
								drawableReg.get(timedEffect.getEffect()
										.getClass()),
								drawableReg.get(timedEffect.getEffect()
										.getClass()), controller, timedEffect,
								PIXEL_PER_SECOND);
					}
					track.add(actor, timedEffect.getTime() * PIXEL_PER_SECOND);
				}
				aux.row();
				track.setInScroll(scroll);
				controller.getModel().addListListener(trackEffect.getEffects(),
						trackListener);
			}
		}

		public Container getAddButton() {
			return addNewTrack;
		}

		public void changeAnimation() {
			ModelEntity modelEntity = (ModelEntity) controller.getModel()
					.getSelection().getSingle(Selection.SCENE);
			Animation aux = animation;
			for (ModelComponent component : modelEntity.getComponents()) {
				if (component instanceof Animation) {
					animation = (Animation) component;
					controller.getModel().retargetListener(aux.getEffects(),
							animation.getEffects(), animationListener);
					refreshTimeline();
					return;
				}
			}

			animation = new Animation();
			controller.action(AddAnimation.class, modelEntity, animation);
			controller.getModel().retargetListener(aux.getEffects(),
					animation.getEffects(), animationListener);
			refreshTimeline();
		}

		@Override
		public void modelChanged(SelectionEvent event) {
			changeAnimation();
		}

		@Override
		public boolean listenToContext(String contextId) {
			return Selection.SCENE.equals(contextId);
		}

		private ModelListener<ListEvent> createListListener() {
			return new ModelListener<ListEvent>() {
				@Override
				public void modelChanged(ListEvent event) {
					refreshTimeline();
				}
			};
		}

		private ModelListener<ListEvent> createTrackEffectListener() {
			return new ModelListener<ListEvent>() {
				@Override
				public void modelChanged(ListEvent event) {
					refreshTimeline();
				}
			};
		}
	}
}
