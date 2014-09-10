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
package es.eucm.ead.editor.view.widgets.editionview.prefabs;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.ComponentId;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.irreversibles.scene.AddBehaviorPrefab;
import es.eucm.ead.editor.control.actions.irreversibles.scene.ChangeBehaviorEffect;
import es.eucm.ead.editor.view.widgets.MultiStateButton;
import es.eucm.ead.editor.view.widgets.VarTextDown;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.schema.components.behaviors.Behavior;
import es.eucm.ead.schema.components.behaviors.events.Touch;
import es.eucm.ead.schema.components.conversation.Conversation;
import es.eucm.ead.schema.components.conversation.LineNode;
import es.eucm.ead.schema.effects.ChangeVar;
import es.eucm.ead.schema.effects.ChangeVar.Context;
import es.eucm.ead.schema.effects.TriggerConversation;
import es.eucm.ead.schema.entities.ModelEntity;

public class ChangeVariablePanel extends PrefabComponentPanel {

	private static final int PREFS_TEXT_ROWS = 3;
	private static final float PAD = 20, BUTTON_MARGIN = 75;

	private VarTextDown varTextDown;
	private TextArea textArea;
	private MultiStateButton stateButton;

	private ChangeVar changeVar;

	public ChangeVariablePanel(float iconPad, float size,
			final Controller controller, Actor touchable) {
		super("variable80x80", iconPad, size, "edition.changeVariable",
				ComponentId.PREFAB_CHANGE_VAR, controller, touchable);

		Array<String> states = new Array<String>();
		states.add(i18n.m("edition.true"));
		states.add(i18n.m("edition.false"));
		states.add(i18n.m("edition.opposite"));
		Array<Color> colors = new Array<Color>();
		colors.add(Color.GREEN);
		colors.add(Color.RED);
		colors.add(Color.YELLOW);

		changeVar = new ChangeVar();
		changeVar.setContext(Context.GLOBAL);

		stateButton = new MultiStateButton(skin, states, colors, BUTTON_MARGIN);
		stateButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				updateChangeVar();
			}
		});

		textArea = new TextArea("", skin);
		textArea.setPrefRows(PREFS_TEXT_ROWS);
		textArea.setMessageText(i18n.m("edition.optionalMessage"));

		varTextDown = new VarTextDown(skin, controller) {
			@Override
			protected void doAction() {
				updateChangeVar();
			}
		};

		panel.add(varTextDown).padTop(PAD).center().expand();
		panel.row();

		LinearLayout stateValue = new LinearLayout(true);
		stateValue.add(new Label(i18n.m("edition.newValue") + ": ", skin))
				.margin(0, 0, PAD, 0);
		stateValue.add(stateButton).expandX();

		panel.add(stateValue).pad(PAD);
		panel.row();
		panel.add(i18n.m("edition.changeVar.text") + ": ").expandX().left()
				.padLeft(PAD);
		panel.row();
		panel.add(textArea).expand().fill().pad(PAD);
		panel.row();
	}

	private void updateChangeVar() {
		if (varTextDown != null && varTextDown.getSelectedVariableDef() != null) {
			if (component == null) {
				component = new Behavior();
				((Behavior) component).setEvent(new Touch());
				component.setId(componentId);
				controller.action(AddBehaviorPrefab.class, component);
			}
			String nameVar = varTextDown.getSelectedVariableDef().getName();
			String expression = getState();
			changeVar.setVariable(nameVar);
			changeVar.setExpression(expression);

			controller.action(ChangeBehaviorEffect.class, component, changeVar);
			if (nameVar != null) {
				setUsed(true);
			}
		}
	}

	@Override
	protected void actualizePanel() {
		String var = null;
		String expression = null;
		String text = "";
		if (component != null && ((Behavior) component).getEffects().size > 0) {
			changeVar = (ChangeVar) ((Behavior) component).getEffects().first();
			var = changeVar.getVariable();
			expression = changeVar.getExpression();
			// initialize text
		}
		stateButton.selectText(booleanToString(expression));
		varTextDown.reloadPanel(var);
		textArea.setText(text);

	}

	private String booleanToString(String string) {
		if (string != null && string == "btrue") {
			return i18n.m("edition.true");
		} else if (string != null && string == "bfalse") {
			return i18n.m("edition.false");
		} else {
			return i18n.m("edition.opposite");
		}
	}

	private String getState() {
		if (stateButton.getText().toString().equals(i18n.m("edition.true"))) {
			return "btrue";
		} else if (stateButton.getText().toString()
				.equals(i18n.m("edition.false"))) {
			return "bfalse";
		} else {
			return "( not $" + varTextDown.getSelectedVariableDef().getName()
					+ " )";
		}
	}
}
