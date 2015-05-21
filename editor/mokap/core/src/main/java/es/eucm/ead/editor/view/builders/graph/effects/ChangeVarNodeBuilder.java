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
package es.eucm.ead.editor.view.builders.graph.effects;

import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import es.eucm.commander.Commander;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.editor.CreateVariable;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.schema.editor.components.VariableDef;
import es.eucm.ead.schema.editor.components.Variables;
import es.eucm.ead.schema.effects.ChangeVar;
import es.eucm.ead.schema.effects.ChangeVar.Context;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.ModelStructure;
import es.eucm.graph.model.Node;

public class ChangeVarNodeBuilder extends EffectNodeBuilder<ChangeVar> {

	private Controller controller;

	public ChangeVarNodeBuilder(Commander commander, Controller controller) {
		super(commander, controller.getApplicationAssets().getSkin(),
				controller.getApplicationAssets().getI18N());
		this.controller = controller;
	}

	@Override
	public Drawable getIcon() {
		return skin.getDrawable(SkinConstants.IC_VARIABLE);
	}

	@Override
	public EffectModal<ChangeVar> buildEditor() {
		return new ChangeVarModal(this, commander, skin, i18N);
	}

	@Override
	public Node newNode() {
		Node node = new Node();
		ChangeVar changeVar = new ChangeVar();
		changeVar.setContext(Context.GLOBAL);
		if (getVariables().size > 0) {
			VariableDef variableDef = getVariables().first();
			changeVar.setVariable(variableDef.getName());
		}
		changeVar.setExpression("bfalse");
		node.setContent(changeVar);
		node.addFork("next");
		return node;
	}

	public Array<VariableDef> getVariables() {
		ModelEntity game = (ModelEntity) controller.getModel()
				.getResource(ModelStructure.GAME_FILE).getObject();
		return Q.getComponent(game, Variables.class).getVariablesDefinitions();
	}

	public void newVariable(TextInputListener inputListener) {
		controller.action(CreateVariable.class, inputListener);
	}

	@Override
	public boolean canAdd() {
		return true;
	}

	@Override
	public Actor buildNodeRepresentation(Node node) {

		ChangeVar changeVar = (ChangeVar) node.getContent();

		Container<Actor> container = new Container<Actor>();
		container.pad(WidgetBuilder.dpToPixels(8));
		if (changeVar.getExpression() == null
				|| changeVar.getVariable() == null) {
			container.setActor(new Label(i18N.m("invalid.effect"), skin));
		} else {
			HorizontalGroup statement = new HorizontalGroup();
			statement.space(WidgetBuilder.dpToPixels(8));

			statement.addActor(WidgetBuilder.icon(SkinConstants.IC_VARIABLE,
					SkinConstants.STYLE_GRAY));
			statement.addActor(new Label(i18N.m("set"), skin));

			Container<Label> variable = new Container<Label>(new Label(
					changeVar.getVariable(), skin));
			variable.setBackground(skin
					.getDrawable(SkinConstants.DRAWABLE_BLANK));
			variable.setColor(skin.getColor(SkinConstants.COLOR_LIGHT_GRAY));
			variable.pad(WidgetBuilder.dpToPixels(8));
			statement.addActor(variable);

			statement.addActor(new Label(i18N.m("to"), skin));

			Container<Label> value = new Container<Label>(new Label(
					i18N.m(changeVar.getExpression().substring(1)), skin));
			value.setBackground(skin.getDrawable(SkinConstants.DRAWABLE_BLANK));
			value.setColor(skin.getColor(changeVar.getExpression().equals(
					"bfalse") ? SkinConstants.COLOR_RED_100
					: SkinConstants.COLOR_GREEN_100));
			value.pad(WidgetBuilder.dpToPixels(8));
			statement.addActor(value);

			container.setActor(statement);
		}
		return container;
	}
}
