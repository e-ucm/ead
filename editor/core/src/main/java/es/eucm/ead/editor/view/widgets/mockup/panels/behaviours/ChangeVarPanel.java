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

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.ead.editor.view.widgets.mockup.buttons.FlagButton;
import es.eucm.ead.editor.view.widgets.mockup.edition.FlagPanel;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.components.behaviors.Behavior;
import es.eucm.ead.schema.editor.components.VariableDef;
import es.eucm.ead.schema.effects.ChangeVar;
import es.eucm.ead.schema.effects.Effect;

public class ChangeVarPanel extends EffectBehaviourPanel {

	private SelectBox<String> value;

	private FlagButton flag;

	private Dialog dialog;

	public ChangeVarPanel(Skin skin, I18N i18n, final FlagPanel flagPanel) {
		super(skin);

		String varName = i18n.m("general.flag-singular");

		this.dialog = new Dialog(i18n.m("general.effects.bad-change-var"), skin);
		this.dialog.button(i18n.m("general.ok"));

		this.flag = new FlagButton(varName, skin);
		this.flag.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				flagPanel.show();
				flagPanel.setParentButton(flag);
			}
		});

		this.value = new SelectBox<String>(skin);
		this.value.setItems(i18n.m("general.inactive"),
				i18n.m("general.active"));

		this.add(this.flag).expandX().fill();
		this.row();
		this.add(new Label(i18n.m("general.edition.change-to"), skin));
		this.row();
		this.add(this.value).expandX().fill();

	}

	@Override
	public boolean actBehaviour(Behavior behavior) {
		if (this.flag.getVariableDef() == null) {
			this.dialog.show(getStage());
			return false;
		}

		Effect effect = new ChangeVar();
		((ChangeVar) effect).setVariable(this.flag.getVariableDef().getName());
		String expression;
		if (this.value.getSelectedIndex() == 1) {
			expression = "btrue";
		} else {
			expression = "bfalse";
		}
		((ChangeVar) effect).setExpression(expression);

		behavior.getEffects().clear();
		behavior.getEffects().add(effect);

		return true;
	}

	public void actPanel(VariableDef fButton, String expression) {
		this.flag.setVariableDef(fButton);
		this.flag.setName(fButton.getName());
		if (expression.equals("bfalse")) {
			this.value.setSelectedIndex(0);
		} else { // expression is btrue
			this.value.setSelectedIndex(1);
		}
	}
}
