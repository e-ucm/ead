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

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import es.eucm.ead.editor.control.MockupViews;

/**
 * Panel is a generic lightweight container with {@link #show()} and
 * {@link #hide()} methods. Sizes and positions children using table constraint.
 */
public class HiddenPanel extends Table {

	private boolean modal = false;

	private int columns;
	private int lastColCount;

	public HiddenPanel(Skin skin) {
		this(skin, (Drawable) null);
	}

	public HiddenPanel(Skin skin, String drawable) {
		this(skin, skin.getDrawable(drawable));
	}

	public HiddenPanel(Skin skin, Drawable drawableBackground) {
		super(skin);
		setBackground(drawableBackground);
		columns = 0;
		lastColCount = 0;
	}

	public void show(Stage stage) {
		show(stage, null);
	}

	public void show(Stage stage, Action action) {
		MockupViews.setUpHiddenPanel(this, stage);
		stage.addActor(this);
		clearActions();
		if (action != null) {
			addAction(Actions.sequence(action,
					Actions.touchable(Touchable.enabled)));
		}
		validate();
	}

	public void hide() {
		hide(null);
	}

	public void setModal(boolean modal) {
		this.modal = modal;
	}

	public void hide(Action action) {
		Stage stage = getStage();
		if (stage == null) {
			return;
		}
		setTouchable(Touchable.disabled);
		MockupViews.removeHitListener(this, stage);
		if (action != null) {
			addAction(sequence(action, Actions.removeActor()));
		} else {
			remove();
		}
	}

	public void setColumns(int cols) {
		this.columns = cols;
	}

	@Override
	public Cell add() {
		if (columns > 0 && lastColCount >= columns) {
			row();
			lastColCount = 0;
		}
		lastColCount++;
		return super.add();
	}

	@Override
	public <T extends Actor> Cell<T> add(T actor) {
		if (columns > 0 && lastColCount >= columns) {
			row();
			lastColCount = 0;
		}
		lastColCount++;
		return super.add(actor);
	}

	@Override
	public Cell row() {
		lastColCount = 0;
		return super.row();
	}

	public boolean isModal() {
		return modal;
	}
}