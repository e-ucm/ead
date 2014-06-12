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
package es.eucm.ead.editor.view.widgets.mockup.panels.behaviours;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.components.behaviors.Behavior;
import es.eucm.ead.schema.effects.Effect;
import es.eucm.ead.schema.effects.GoTo;

public class GoToPanel extends EffectBehaviourPanel {

	private TextField valueX;

	private TextField valueY;

	private Dialog dialog;

	public GoToPanel(Skin skin, I18N i18n) {
		super(skin);

		this.dialog = new Dialog(i18n.m("general.effects.bad-go-to"), skin);
		this.dialog.button(i18n.m("general.ok"));

		this.valueX = new TextField("", skin);
		this.valueX.setMessageText("0.0");
		this.valueY = new TextField("", skin);
		this.valueY.setMessageText("0.0");

		this.add(new Label("X : ", skin));
		this.add(this.valueX).expandX().fill();
		this.row();
		this.add(new Label("Y : ", skin));
		this.add(this.valueY).expandX().fill();
	}

	@Override
	public boolean actBehaviour(Behavior behavior) {
		Effect effect = new GoTo();
		try {
			((GoTo) effect).setX(Float.valueOf(valueX.getText()));
			((GoTo) effect).setY(Float.valueOf(valueY.getText()));
		} catch (NumberFormatException e) {
			this.dialog.show(getStage());
			return false;
		}
		behavior.getEffects().clear();
		behavior.getEffects().add(effect);

		return true;
	}

	public void actPanel(float x, float y) {
		this.valueX.setText("" + x);
		this.valueY.setText("" + y);
	}
}