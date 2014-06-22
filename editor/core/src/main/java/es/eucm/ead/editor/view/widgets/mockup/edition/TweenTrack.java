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
package es.eucm.ead.editor.view.widgets.mockup.edition;

import java.util.List;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;

import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.editor.view.widgets.mockup.buttons.TweenButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.TweenDragButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.TweenDragButton.TweenType;
import es.eucm.ead.editor.view.widgets.mockup.panels.HiddenPanel;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.components.tweens.BaseTween;
import es.eucm.ead.schema.components.tweens.Timeline;
import es.eucm.ead.schema.components.tweens.Tween;

/**
 * Horizontal LinearLayout for TweenButton, has a track name. If you drag a
 * TweenDragButton on it, a TweenButton is created. Lets sort the TweenButton by
 * dragging.
 */
public class TweenTrack extends LinearLayout {

	private TimelineConfig config;
	private Target target;
	private Skin skin;
	private ClickListener clickTweenButton;
	private TextButton label;
	private DragAndDrop tweensButtons;
	private ScrollPane scroll;
	private LinearLayout dummy;

	/**
	 * Creates a TweenTrack with name of track. Receives the DragAndDrop and
	 * ClickListener used for TweenButton. Also receives the ScrollPane where is
	 * the Track
	 * 
	 * @param skin
	 * @param name
	 * @param tweensButtons
	 * @param clickTweenButton
	 * @param scroll
	 */
	public TweenTrack(final Skin skin, String name, I18N i18n,
			final DragAndDrop tweensButtons,
			final ClickListener clickTweenButton, final ScrollPane scroll) {
		super(true);

		this.skin = skin;
		this.clickTweenButton = clickTweenButton;
		label = new TextButton(name, skin);
		label.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				config.show();
			}
		});
		this.add(label);
		config = new TimelineConfig(skin, i18n);

		this.scroll = scroll;
		// The dummy LinearLayout is necessary for drag the TweenButtons at the
		// end of track.
		dummy = new LinearLayout(true);
		this.add(dummy).expand(true, true);
		this.tweensButtons = tweensButtons;

		target = new Target(this) {
			@Override
			public boolean drag(Source source, Payload payload, float x,
					float y, int pointer) {

				if (source.getActor() instanceof TweenDragButton) {
					return true;
				} else if (source.getActor().getParent() != TweenTrack.this) {
					return true;
				} else {
					return false;
				}
			}

			@Override
			public void drop(Source source, Payload payload, float x, float y,
					int pointer) {
				final TweenButton newTween;

				if (source.getActor() instanceof TweenDragButton) {
					String icon = ((TweenDragButton) source.getActor())
							.getIcon();
					TweenType type = ((TweenDragButton) source.getActor())
							.getType();

					newTween = new TweenButton(skin, icon + "_on",
							TweenTrack.this, type, clickTweenButton);
					newTween.setScroll(scroll);

					TweenTrack.this
							.add(TweenTrack.this.getSize() - 1, newTween);

					TweenTrack.this.getChildren().swap(
							TweenTrack.this.getSize() - 2,
							TweenTrack.this.getSize() - 1);

					tweensButtons.addSource(newTween.getSource());
					tweensButtons.addTarget(newTween.getTarget());

					newTween.setVisible(true);
				}
			}
		};
	}

	private int getSize() {
		return this.getChildren().size;
	}

	public Target getTarget() {
		return this.target;
	}

	private Target newTarget(Actor actor, final boolean first) {
		return new Target(actor) {
			@Override
			public boolean drag(Source source, Payload payload, float x,
					float y, int pointer) {
				return true;
			}

			@Override
			public void drop(Source source, Payload payload, float x, float y,
					int pointer) {
				TweenButton newTween = (TweenButton) source.getActor();
				newTween.remove();
				int num;
				if (first) {
					num = 1;
				} else {
					num = TweenTrack.this.getSize() - 1;
				}
				TweenTrack.this.add(num, newTween);

				for (int i = TweenTrack.this.getChildren().size - 1; i > num; i--) {
					TweenTrack.this.getChildren().swap(i, i - 1);
				}

				newTween.setParentTrack(TweenTrack.this);
				newTween.setVisible(true);
			}
		};
	}

	/**
	 * Creates a {@link Timeline} from the current state of this
	 * {@link TweenTrack}.
	 * 
	 * @return
	 */
	public Timeline buildTimeline() {
		Timeline timeline = new Timeline();
		List<BaseTween> baseTweens = timeline.getChildren();
		for (Actor actor : getChildren()) {
			if (actor instanceof TweenButton) {
				baseTweens.add(((TweenButton) actor).getTween());
			}
		}
		timeline.setRepeat(config.getRepeat());
		timeline.setYoyo(config.isYoyo());
		return timeline;
	}

	/**
	 * Initializes this widget with the {@link BaseTween base tweens} from the
	 * {@link Timeline}.
	 * 
	 * @param timeline
	 */
	public void init(Timeline timeline) {
		super.clear();
		add(this.label);
		tweensButtons.addTarget(newTarget(label, true));
		List<BaseTween> tweens = timeline.getChildren();
		for (int i = 0; i < tweens.size(); ++i) {
			BaseTween currTween = tweens.get(i);
			if (currTween instanceof Tween) {
				TweenButton newTween = new TweenButton(skin, this,
						(Tween) currTween, clickTweenButton);
				tweensButtons.addSource(newTween.getSource());
				tweensButtons.addTarget(newTween.getTarget());
				newTween.setScroll(scroll);
				add(newTween);
			}
		}
		config.setYoyo(timeline.isYoyo());
		config.setRepeat(timeline.getRepeat());
		add(dummy).expand(true, true);
		tweensButtons.addTarget(newTarget(dummy, false));
	}

	@Override
	public void clear() {
		super.clear();
		add(this.label);
		add(dummy).expand(true, true);
		config.restart();
		tweensButtons.addTarget(newTarget(label, true));
		tweensButtons.addTarget(newTarget(dummy, false));
	}

	private class TimelineConfig extends HiddenPanel {
		private static final float DEFAULT_PAD = 40;
		private TextField reps;
		private CheckBox yoyo;

		public TimelineConfig(Skin skin, I18N i18n) {
			super(skin);

			reps = new TextField("", skin);
			reps.setMessageText(i18n.m("general.repeats"));
			yoyo = new CheckBox(i18n.m("yo-yo"), skin);
			add(reps);
			add(yoyo);
			row();

			Button accept = new TextButton(i18n.m("general.accept"), skin);
			Button cancel = new TextButton(i18n.m("general.cancel"), skin);
			ClickListener hideListener = new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					hide();
				}
			};
			accept.addListener(hideListener);
			cancel.addListener(hideListener);
			add(accept);
			add(cancel);
		}

		public void restart() {
			yoyo.setChecked(false);
			reps.setText("");
		}

		public boolean isYoyo() {
			return yoyo.isChecked();
		}

		public void setYoyo(boolean yoyo) {
			this.yoyo.setChecked(yoyo);
		}

		public void setRepeat(int repeat) {
			this.reps.setText("" + repeat);
		}

		public int getRepeat() {
			try {
				return Integer.valueOf(reps.getText());
			} catch (NumberFormatException nfe) {
				return 0;
			}
		}

		@Override
		public void show() {
			centerPos(label.getStage(), this);
			super.show();
		}

		@Override
		protected void onFadedOut() {
			remove();
		}

		private void centerPos(Stage stage, WidgetGroup actor) {
			stage.addActor(actor);
			actor.pack();
			actor.setPosition(
					Math.round((stage.getWidth() - actor.getWidth()) / 2f),
					Math.round((stage.getHeight() - actor.getHeight())
							- DEFAULT_PAD));
		}
	}

}
