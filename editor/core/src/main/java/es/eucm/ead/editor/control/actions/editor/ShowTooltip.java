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
package es.eucm.ead.editor.control.actions.editor;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.EditorAction;

/**
 * <p>
 * Shows a toast with the given text
 * </p>
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> <em>String</em> The tooltip text</dd>
 * <dd><strong>args[1]</strong> <em>Actor</em> The actor owner of the tooltip</dd>
 * </dl>
 */
public class ShowTooltip extends EditorAction {

	private static final float TOOLTIP_TIME = 1.0f;

	private static final String STYLE_TOAST = "toast";

	private Label label;

	private Vector2 position = new Vector2();

	public ShowTooltip() {
		super(true, true, String.class, Actor.class);
	}

	@Override
	public void initialize(Controller controller) {
		super.initialize(controller);
		Skin skin = controller.getApplicationAssets().getSkin();
		label = new Label("", skin, STYLE_TOAST);
		label.setTouchable(Touchable.disabled);
	}

	@Override
	public void perform(Object... args) {
		label.setText((String) args[0]);
		label.pack();

		Actor actor = (Actor) args[1];

		position.set(0, 0);
		actor.localToStageCoordinates(position);

		label.setPosition(position.x, position.y - label.getHeight());
		label.clearActions();
		label.addAction(Actions.sequence(Actions.alpha(0.0f),
				Actions.alpha(1.0f, TOOLTIP_TIME, Interpolation.exp5Out),
				Actions.delay(TOOLTIP_TIME),
				Actions.alpha(0.0f, TOOLTIP_TIME, Interpolation.exp5Out),
				Actions.removeActor()));
		controller.getViews().addToModalsContainer(label);
	}
}
