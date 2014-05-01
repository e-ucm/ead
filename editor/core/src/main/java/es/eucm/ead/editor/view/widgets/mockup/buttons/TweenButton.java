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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;

public class TweenButton extends VerticalGroup {

	private String icon;
	private TweenType type;
	private Image image;
	private Label label;

	public enum TweenType {
		ROTATE, SCALE, MOVE, REMOVE
	}

	public TweenButton(final Skin skin, final String icon, String name, TweenType type,
			DragAndDrop dragAndDrop) {
		super();
		this.icon = icon;
		this.type = type;
		this.image = new Image(skin, icon);
		this.label = new Label(name, skin);
		this.label.setFontScale(0.5f);
		
		this.addActor(this.image);
		this.addActor(this.label);

		dragAndDrop.addSource(new Source(this) {
			@Override
			public void dragStop(InputEvent event, float x, float y,
					int pointer, Payload payload, Target target) {
			}

			public Payload dragStart(InputEvent event, float x, float y,
					int pointer) {
				Image valid = new Image(skin, icon);
				valid.setColor(Color.GREEN);

				Payload payload = new Payload();
				payload.setDragActor(new Image(skin, icon));

				payload.setValidDragActor(valid);

				return payload;
			}
		});
	}

	public String getIcon() {
		// TODO Auto-generated method stub
		return icon;
	}

	public TweenType getType() {
		return this.type;
	}
}
