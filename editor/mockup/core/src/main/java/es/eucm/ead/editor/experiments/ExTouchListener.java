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
package es.eucm.ead.editor.experiments;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.vividsolutions.jts.math.MathUtil;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.engine.assets.Assets.AssetLoadedCallback;

public class ExTouchListener extends InputListener {

	private static final Color SELECTION_COLOR = Color.valueOf("5677fc44");

	private DrawableActor selected;

	private Sound select;

	public ExTouchListener(Controller controller) {
		Drawable selectDrawable = controller.getApplicationAssets().getSkin()
				.getDrawable("blank");
		selected = new DrawableActor(selectDrawable, SELECTION_COLOR);
		controller.getApplicationAssets().get("sounds/click.wav", Sound.class,
				new AssetLoadedCallback<Sound>() {
					@Override
					public void loaded(String fileName, Sound asset) {
						select = asset;
					}
				});
	}

	@Override
	public boolean touchDown(InputEvent event, float x, float y, int pointer,
							 int button) {
		return true;
	}

	@Override
	public void touchUp(InputEvent event, float x, float y, int pointer,
						int button) {
		Actor target = event.getTarget();
		selected.setFollow(target);
		if (target != selected) {
			int zIndex = target.getZIndex();
			int i = 0;
			for (Actor a : target.getParent().getChildren()) {
				a.addAction(Actions.alpha(1.0f, 0.1f));
				if (i > zIndex) {
					a.addAction(Actions.alpha(0.25f, 0.1f));
				}
				i++;
			}
			target.getParent().addActorAfter(target, selected);
		} else {
			for (Actor a : target.getParent().getChildren()) {
				a.addAction(Actions.alpha(1.0f, 0.1f));
			}
		}
	}

	private class DrawableActor extends Group {

		private static final float MAX_TIME = 0.1f;

		private float time = 0;

		private Actor follow;

		private Drawable drawable;

		private float deltaRotation = 15.0f;

		private Color color;

		private int dissapearing;

		private DrawableActor(Drawable drawable, Color color) {
			this.drawable = drawable;
			this.color = color;
		}

		public void setFollow(Actor follow) {
			if (follow == this) {
				setTouchable(Touchable.disabled);
				time = MAX_TIME;
				dissapearing = -1;
			} else {
				setTouchable(Touchable.enabled);
				this.follow = follow;
				time = 0;
				dissapearing = 1;
			}
			select.play();
		}

		@Override
		public void act(float delta) {
			time += dissapearing * delta;
			time = (float) MathUtil.clamp(time, 0, MAX_TIME);

			float r = deltaRotation * (1 - time / MAX_TIME);
			setBounds(follow.getX(), follow.getY(), follow.getWidth(),
					follow.getHeight());
			setRotation(follow.getRotation() + r);
			setScale(follow.getScaleX(), follow.getScaleY());
			setOrigin(follow.getOriginX(), follow.getOriginY());

			if (time <= 0 && dissapearing == -1) {
				remove();
			}
			super.act(delta);
		}

		@Override
		protected void drawChildren(Batch batch, float parentAlpha) {
			float alpha = Interpolation.exp5Out.apply(time / MAX_TIME);

			float width = alpha * getWidth();

			super.drawChildren(batch, parentAlpha);
			batch.setColor(color);
			drawable.draw(batch, getWidth() / 2.0f - width / 2.0f, 0, width,
					getHeight());
			batch.setColor(Color.WHITE);
		}

	}
}
