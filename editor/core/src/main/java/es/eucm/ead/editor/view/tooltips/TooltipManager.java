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
package es.eucm.ead.editor.view.tooltips;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Align;

/**
 * Controls the tooltips
 */
public class TooltipManager extends InputListener {

	public static final float TOOLTIP_TIME = 0.5f;

	public static final float TOOLTIP_OFFSET = 15;

	private Group editorRoot;

	private Label tooltipLabel;

	private Vector2 tooltipPosition = new Vector2();

	private Actor target;

	private Tooltip tooltip;

	private float remainingTime = -1;

	private boolean tooltipShowing;

	public TooltipManager(Group editorRoot, LabelStyle tooltipStyle) {
		editorRoot.addCaptureListener(this);
		this.editorRoot = editorRoot;
		tooltipLabel = new Label("", tooltipStyle);
		tooltipLabel.setAlignment(Align.center);
	}

	@Override
	public void enter(InputEvent event, float x, float y, int pointer,
			Actor fromActor) {
		if (pointer == -1) {
			if (event.getTarget() instanceof Tooltip) {
				Actor target = event.getTarget();
				Tooltip tooltip = (Tooltip) target;
				if (tooltip.getTooltip() != null) {
					this.target = target;
					this.tooltip = tooltip;
					this.remainingTime = tooltipShowing ? 0 : TOOLTIP_TIME;
				}
			}
		}
	}

	@Override
	public void exit(InputEvent event, float x, float y, int pointer,
			Actor toActor) {
		if (pointer == -1) {
			if (event.getTarget() == target) {
				tooltipLabel.remove();
				remainingTime = -1;
				if (!(toActor instanceof Tooltip)) {
					tooltipShowing = false;
				}
			}
		}
	}

	/**
	 * @param delta
	 *            time after last update
	 */
	public void update(float delta) {
		if (remainingTime >= 0) {
			remainingTime -= delta;
			if (remainingTime <= 0) {
				showTooltip();
			}
		}
	}

	private void showTooltip() {
		remainingTime = -1;
		tooltipShowing = true;
		tooltipPosition.set(target.getWidth() * tooltip.getXOffset(),
				target.getHeight() * tooltip.getYOffset());
		target.localToAscendantCoordinates(editorRoot, tooltipPosition);

		tooltipLabel.setText(tooltip.getTooltip());

		Vector2 textBounds = tooltipLabel.getTextBounds();
		tooltipLabel.setSize(Math.round(textBounds.x + TOOLTIP_OFFSET),
				Math.round(textBounds.y + TOOLTIP_OFFSET));

		tooltipPosition.x = Math.max(
				0,
				Math.min(tooltipPosition.x, Gdx.graphics.getWidth()
						- tooltipLabel.getWidth()));
		tooltipLabel.setPosition(Math.round(tooltipPosition.x),
				Math.round(tooltipPosition.y - tooltipLabel.getHeight()));
		editorRoot.addActor(tooltipLabel);
	}
}
