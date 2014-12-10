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

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class DropdownPane extends Table {

	private static final float ANIMATION_TIME = 0.2f;

	private Table head;

	protected Table body;

	private boolean open;

	protected Skin skin;

	private ClickListener openClosePane;

	public DropdownPane(Skin skin) {
		super(skin);
		this.skin = skin;
		this.open = false;

		head = new Table(skin);
		head.setTransform(true);

		body = new Table(skin);
		body.setVisible(false);
		body.setTransform(true);

		openClosePane = new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				if (open) {
					close();
				} else {
					open();
				}
			}
		};

		add(head).expand().fill();
	}

	public DropdownPane(Skin skin, String background) {
		this(skin);
		body.setBackground(skin.getDrawable(background));
	}

	public void setHead(Actor actor) {
		setHead(actor, actor);
	}

	public void setHead(Actor actor, Actor drop) {
		drop.addListener(openClosePane);
		head.add(actor).expand().fill();
	}

	public void addToBody(Actor actor) {
		body.add(actor).expand().fill();
		body.row();
		body.pack();
		body.setOrigin(body.getWidth() / 2, body.getHeight());
	}

	public boolean isOpen() {
		return open;
	}

	public void open() {
		if (!open) {
			body.clearActions();
			add(body).expand().fill();
			body.addAction(Actions.sequence(Actions.scaleTo(1, 0),
					Actions.visible(true),
					Actions.scaleTo(1, 1, ANIMATION_TIME)));

			open = !open;
		}
	}

	public void close() {
		if (open) {
			body.clearActions();
			body.addAction(Actions.sequence(
					Actions.scaleTo(1, 0, ANIMATION_TIME),
					Actions.visible(false), Actions.removeActor(body)));

			open = !open;
		}
	}

}
