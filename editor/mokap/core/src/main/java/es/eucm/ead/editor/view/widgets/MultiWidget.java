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
package es.eucm.ead.editor.view.widgets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;

public class MultiWidget extends Container<WidgetGroup> {

	private static final float ANIM_TIME = 0.15f;

	private Array<WidgetGroup> widgets;

	protected float maxHeight;

	private WidgetGroup toHide;

	protected WidgetGroup toShow;

	private Runnable actionAddActor;

	private MultiWidgetStyle style;

	public MultiWidget() {
		initialize(null);
	}

	public MultiWidget(Skin skin) {
		initialize(skin.get(MultiWidgetStyle.class));
	}

	public MultiWidget(MultiWidgetStyle style) {
		initialize(style);
	}

	private void initialize(MultiWidgetStyle style) {
		this.style = style;

		if (style != null) {
			setBackground(style.background);
			if (style.color != null) {
				setColor(style.color);
			}
		}

		widgets = new Array<WidgetGroup>();
		maxHeight = 0;
		fill();

		actionAddActor = new Runnable() {
			@Override
			public void run() {

				float timeShow = ANIM_TIME * (1 - Math.abs(toShow.getScaleY()));

				setActor(toShow);
				toShow.addAction(Actions.sequence(Actions.parallel(
						Actions.scaleTo(1, 1, timeShow, Interpolation.sineIn),
						Actions.fadeIn(timeShow)), Actions
						.touchable(Touchable.enabled)));
			}
		};
	}

	public void addWidgets(WidgetGroup... w) {
		for (WidgetGroup widget : w) {
			widget.setTouchable(Touchable.disabled);

			if (widget instanceof Table) {
				((Table) widget).setTransform(true);
			}
			widgets.add(widget);
			if (maxHeight < widget.getPrefHeight()) {
				maxHeight = widget.getPrefHeight();
			}
		}

		if (getActor() == null) {
			setActor(w[0]);
			toShow = w[0];
			w[0].setTouchable(Touchable.enabled);
		}
	}

	/**
	 * Set the default Widget, without animation.
	 * 
	 */
	public void showSimple() {
		showSimple(0);
	}

	/**
	 * Set the Widget in index position, without animation.
	 * 
	 */
	public void showSimple(int index) {
		if (index < widgets.size) {
			WidgetGroup widget = widgets.get(index);
			widget.setTouchable(Touchable.enabled);
			widget.getColor().a = 1;
			widget.setScaleY(1);
			setActor(widget);
			toShow = widget;
			toHide = null;
		}
	}

	public void setSelectedWidget(int index) {
		if (widgets.size > index) {
			WidgetGroup newBar = widgets.get(index);

			if (newBar != toShow) {
				for (WidgetGroup widget : widgets) {
					widget.clearActions();
				}

				Actor current = getActor();
				if (current != null) {
					current.setTouchable(Touchable.disabled);
				} else {
					current = toShow;
				}

				toShow = newBar;

				toHide = (WidgetGroup) current;
				toHide.setOrigin(Align.center);

				if (toShow.getScaleY() == 1) {
					toShow.setScaleY(0);
					toShow.setOriginY(toHide.getOriginY());
					toShow.getColor().a = 0;
				}

				float timeHide = ANIM_TIME * Math.abs(toHide.getScaleY());

				Action actionShow = Actions.run(actionAddActor);
				Action actionHide = Actions.parallel(
						Actions.scaleTo(1, 0, timeHide, Interpolation.sineOut),
						Actions.fadeOut(timeHide));

				if (newBar == current) {
					toHide.addAction(actionShow);
				} else {
					toHide.addAction(Actions.sequence(actionHide, actionShow));
				}
			}
		}
	}

	@Override
	public float getPrefHeight() {
		float backgroungPadding = 0;
		if (getBackground() != null) {
			backgroungPadding = getBackground().getBottomHeight()
					+ getBackground().getTopHeight();
		}
		return maxHeight + backgroungPadding;
	}

	public Array<WidgetGroup> getWidgets() {
		return widgets;
	}

	public WidgetGroup getCurrentWidget() {
		return this.getActor();
	}

	public static class MultiWidgetStyle {

		public Drawable background;

		public Color color;

	}
}
