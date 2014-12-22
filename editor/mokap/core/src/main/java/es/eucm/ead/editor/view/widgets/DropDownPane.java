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

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import es.eucm.ead.editor.view.widgets.layouts.ResizableWidget;

/**
 * A panel with a header and a body that can be shown and hidden
 */
public class DropDownPane extends Table {

	private Table header;

	protected Table bodyContent;

	protected ResizableWidget body;

	private boolean open;

	public DropDownPane() {
		this.open = false;

		header = new Table();
		header.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				if (open) {
					close();
				} else {
					open();
				}
			}
		});

		add(header).expand().fill();
		row();
		add(body = new ResizableWidget()).expand().fill();

		bodyContent = new Table();
		bodyContent.setFillParent(true);
		bodyContent.setVisible(false);

		body.addActor(bodyContent);
	}

	public void addHeaderRow(Actor actor) {
		header.add(actor).expand().fill();
		header.row();
	}

	public void addBodyRow(Actor actor) {
		bodyContent.add(actor).expand().fill();
		bodyContent.row();
	}

	public boolean isOpen() {
		return open;
	}

	/**
	 * Unfolds the body of the drop down pane
	 */
	public void open(boolean animate) {
		if (!open) {
			open = true;
			bodyContent.setVisible(true);
			if (animate) {
				body.clearActions();
				body.addAction(Actions.sizeTo(bodyContent.getPrefWidth(),
						bodyContent.getPrefHeight(), 0.40f,
						Interpolation.exp5Out));
				bodyContent.addAction(Actions.sequence(Actions.visible(true),
						Actions.alpha(0), Actions.delay(0.1f),
						Actions.alpha(1.0f, 0.4f, Interpolation.exp5Out)));
			} else {
				body.setSize(bodyContent.getPrefWidth(),
						bodyContent.getPrefHeight());
				bodyContent.getColor().a = 1.0f;
				bodyContent.setVisible(true);
			}
		}
	}

	public void open() {
		open(true);
	}

	/**
	 * Closes the body of the drop down pane
	 */
	public void close(boolean animate) {
		if (open) {
			open = false;
			if (animate) {
				body.addAction(Actions.sizeTo(body.getWidth(), 0, 0.40f,
						Interpolation.exp5Out));
				bodyContent.addAction(Actions.sequence(
						Actions.alpha(0.0f, 0.2f, Interpolation.exp5Out),
						Actions.visible(false)));
			} else {
				body.setHeight(0);
				bodyContent.getColor().a = 0.0f;
				bodyContent.setVisible(false);
			}
		}
	}

	public void close() {
		close(true);
	}

	@Override
	public void layout() {
		bodyContent.pack();
		body.setWidth(bodyContent.getWidth());
		super.layout();
	}
}
