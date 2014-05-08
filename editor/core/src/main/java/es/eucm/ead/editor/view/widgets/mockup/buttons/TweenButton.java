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
package es.eucm.ead.editor.view.widgets.mockup.buttons;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;

import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.editor.view.widgets.mockup.buttons.TweenDragButton.TweenType;
import es.eucm.ead.editor.view.widgets.mockup.edition.TweenTrack;
import es.eucm.ead.schema.components.tweens.AlphaTween;
import es.eucm.ead.schema.components.tweens.MoveTween;
import es.eucm.ead.schema.components.tweens.RotateTween;
import es.eucm.ead.schema.components.tweens.ScaleTween;
import es.eucm.ead.schema.components.tweens.Tween;

public class TweenButton extends LinearLayout {

	private static float SIZE_PER_SECOND = 40;

	private TweenType type;

	private Tween tween;

	private Source source;

	private Target target;

	private String icon;

	private ScrollPane scroll;

	private TweenTrack parentTrack;

	private ImageButton image;

	private Label label;

	public TweenButton(final Skin skin, String icon, final TweenTrack parent,
			final TweenType type, ClickListener listener) {
		super(false);

		this.image = new ImageButton(skin, icon);
		this.label = new Label("", skin);
		this.label.setFontScale(0.5f);

		this.add(this.label);
		this.add(this.image).expandX();

		this.parentTrack = parent;
		this.scroll = null;
		this.addListener(listener);
		this.type = type;
		this.icon = icon;
		if (type == TweenType.MOVE) {
			this.tween = new MoveTween();
		} else if (type == TweenType.SCALE) {
			this.tween = new ScaleTween();
		} else if (type == TweenType.ROTATE) {
			this.tween = new RotateTween();
		} else if (type == TweenType.ALPHA) {
			this.tween = new AlphaTween();
		}

		tween.setDuration(2);
		this.label.setText("2s");

		source = new Source(this) {

			@Override
			public Payload dragStart(InputEvent event, float x, float y,
					int pointer) {
				if (scroll != null) {
					scroll.setCancelTouchFocus(false);
				}
				Payload payload = new Payload();

				TweenButton.this.setVisible(false);

				payload.setDragActor(new ImageButton(skin,
						TweenButton.this.icon));

				return payload;
			}

			@Override
			public void dragStop(InputEvent event, float x, float y,
					int pointer, Payload payload, Target target) {
				if (scroll != null) {
					scroll.setCancelTouchFocus(true);
				}
				try {
					target.getClass();
				} catch (NullPointerException e) {
					TweenButton.this.setVisible(true);
				}
				super.dragStop(event, x, y, pointer, payload, target);
			}
		};

		target = new Target(this) {

			@Override
			public boolean drag(Source source, Payload payload, float x,
					float y, int pointer) {
				return true;
			}

			@Override
			public void drop(Source source, Payload payload, float x, float y,
					int pointer) {

				if (source.getActor() instanceof TweenButton
						&& getActor() instanceof TweenButton) {

					TweenButton thisAux = (TweenButton) getActor();
					TweenButton dragActor = (TweenButton) source.getActor();

					dragActor.remove();

					TweenTrack track = (TweenTrack) thisAux.getParentTrack();
					int num = track.getChildren().indexOf(thisAux, true);

					track.add(num, dragActor);

					for (int i = track.getChildren().size - 1; i > num; i--) {
						System.out.println(i + "swap" + (i - 1));
						track.getChildren().swap(i, i - 1);
					}

					dragActor.setParentTrack(thisAux.getParentTrack());
					dragActor.setVisible(true);
				}
			}

		};
	}

	public TweenType getType() {
		return type;
	}

	public TweenTrack getParentTrack() {
		return this.parentTrack;
	}

	public void setParentTrack(TweenTrack parent) {
		this.parentTrack = parent;
	}

	public Tween getTween() {
		return tween;
	}

	public String getIcon() {
		return this.icon;
	}

	public Source getSource() {
		return this.source;
	}

	public Target getTarget() {
		return this.target;
	}

	public void hasScroll(ScrollPane scroll) {
		this.scroll = scroll;
	}

	@Override
	public float getPrefWidth() {
		if (this.tween == null) {
			return SIZE_PER_SECOND;
		} else {
			return SIZE_PER_SECOND * this.tween.getDuration();
		}
	}

	public void setDuration(String duration) {
		this.label.setText(duration + "s");
	}
}
