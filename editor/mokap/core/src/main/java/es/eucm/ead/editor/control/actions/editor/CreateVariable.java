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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.EditorAction;
import es.eucm.ead.editor.control.actions.model.generic.AddToArray;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.editor.components.VariableDef;
import es.eucm.ead.schema.editor.components.VariableDef.Type;
import es.eucm.ead.schema.editor.components.Variables;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.ModelStructure;

public class CreateVariable extends EditorAction implements TextInputListener {

	private static final String IDENTIFIER_EXPRESSION = "^[^\\d\\W]\\w*\\Z";

	private TextInputListener resultListener;

	private Variables variables;

	private Array<String> names = new Array<String>();

	private I18N i18N;

	public CreateVariable() {
		super(true, false, TextInputListener.class);
	}

	@Override
	public void initialize(Controller controller) {
		super.initialize(controller);
		i18N = controller.getApplicationAssets().getI18N();
	}

	@Override
	public void perform(Object... args) {
		resultListener = (TextInputListener) args[0];
		ModelEntity game = (ModelEntity) controller.getModel()
				.getResource(ModelStructure.GAME_FILE).getObject();
		variables = Q.getComponent(game, Variables.class);
		names.clear();
		for (VariableDef def : variables.getVariablesDefinitions()) {
			names.add(def.getName());
		}

		Gdx.input.getTextInput(this, i18N.m("new.variable"), newVariableName(),
				"");
	}

	private void retry(String message, String variable) {
		Gdx.input.getTextInput(this, message, variable, "");
	}

	private String newVariableName() {
		String flagName = "flag";
		int i = 1;
		while (names.contains(flagName, false)) {
			flagName = "flag" + ++i;
		}
		return flagName;
	}

	@Override
	public void input(String text) {
		if (text.length() > 24) {
			retry(i18N.m("variable.too.long"), text);
		} else if (names.contains(text, false)) {
			retry(i18N.m("duplicated.variable"), text);
		} else if (text.matches(IDENTIFIER_EXPRESSION)) {
			VariableDef variableDef = new VariableDef();
			variableDef.setName(text);
			variableDef.setType(Type.BOOLEAN);
			variableDef.setInitialValue("false");
			controller.action(AddToArray.class, variables,
					variables.getVariablesDefinitions(), variableDef);
			controller.getModel().getResource(ModelStructure.GAME_FILE)
					.setModified(true);
			resultListener.input(text);
			Gdx.graphics.requestRendering();
		} else {
			retry(i18N.m("invalid.variable"), text);
		}
	}

	@Override
	public void canceled() {
		resultListener.canceled();
		Gdx.graphics.requestRendering();
	}
}
