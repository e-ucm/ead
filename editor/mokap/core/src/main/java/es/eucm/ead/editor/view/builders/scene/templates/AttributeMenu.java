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
package es.eucm.ead.editor.view.builders.scene.templates;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.engine.gdx.AbstractWidget;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;

public class AttributeMenu extends AbstractWidget {

	public static final float ANIMATION_TIME = 0.3f;

	private final float PAD = WidgetBuilder.dpToPixels(8);

	private Skin skin;

	private float buttonHeight;

	private int selectedIndex;

	private TextButton selected;

	private LinearLayout list;

	private Drawable backgroundList;

	public AttributeMenu(Skin skin) {

		backgroundList = skin.getDrawable(SkinConstants.DRAWABLE_PAGE);

		list = new LinearLayout(false, backgroundList);

		setTouchable(Touchable.disabled);

		list.pad(PAD, PAD, PAD, PAD);

		this.skin = skin;

		selectedIndex = 0;
		selected = new TextButton("", skin);

		addActor(list);
		addActor(selected);

	}

	@Override
	public float getPrefHeight() {
		return buttonHeight;
	}

	@Override
	public float getPrefWidth() {
		return list.getWidth();
	}

	public void addAttribute(String name) {
		TextButton button = new TextButton(name, skin,
				SkinConstants.STYLE_TEMPLATE);
		button.pad(0, PAD * 2, 0, PAD * 2);
		if (selectedIndex == 0) {
			buttonHeight = button.getHeight();
			selectedIndex = 0;
		}
		list.add(button);
		list.pack();
		list.setY(maxPositionY());
	}

	public void clearAttributeMenu() {
		list.getChildren().clear();
		selectedIndex = 0;
	}

	public void show() {
		updateText();
		clearActions();
		addAction(Actions.sequence(Actions.visible(true),
				Actions.fadeIn(ANIMATION_TIME)));
	}

	@Override
	public void layout() {
		super.layout();
		selected.setWidth(list.getWidth() - PAD * 2);
		selected.setPosition(-selected.getWidth() * 0.5f, -buttonHeight * 0.5f
				- PAD * 0.5f);

		list.setX(-list.getWidth() * 0.5f);
	}

	public void hide() {
		clearActions();
		addAction(Actions.sequence(Actions.fadeOut(ANIMATION_TIME),
				Actions.visible(false)));
	}

	public void verticalMove(float amount) {
		if (amount < 0) {
			list.setY(Math.max(Math.round(list.getY() + amount), maxPositionY()));
		} else {
			list.setY(Math.min(Math.round(list.getY() + amount), minPositionY()));
		}
		calculateAndUpdateIndex();
	}

	private void calculateAndUpdateIndex() {
		int newIndex = list.getChildren().size
				- MathUtils.ceil(Math.abs(list.getY()
						+ backgroundList.getBottomHeight() + PAD)
						/ buttonHeight);
		selectedIndex = newIndex;
		updateText();
	}

	private float minPositionY() {
		return -backgroundList.getBottomHeight() - PAD - buttonHeight * 0.5f;
	}

	private float maxPositionY() {
		return backgroundList.getTopHeight() + PAD + buttonHeight * 0.5f
				- list.getHeight();
	}

	private void updateText() {
		selected.setText(((TextButton) list.getChildren().get(
				list.getChildren().size - 1 - selectedIndex)).getText()
				.toString());
	}

	public String getAttributeSelected() {
		int size = list.getChildren().size;
		return ((TextButton) list.getChildren().get(size - 1 - selectedIndex))
				.getText().toString();
	}
}
