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
package es.eucm.ead.editor.view.builders.scene.components;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.model.generic.SetField;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.builders.scene.components.variablesWidgets.Level2LogicExpressionWithWidget;
import es.eucm.ead.editor.view.builders.scene.components.variablesWidgets.LogicExpressionWithWidget;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.editor.view.widgets.selectors.Selector;
import es.eucm.ead.engine.expressions.Operation;
import es.eucm.ead.engine.expressions.Parser;
import es.eucm.ead.engine.expressions.operators.OperationsFactory;
import es.eucm.ead.schema.components.Visibility;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.ComponentIds;
import es.eucm.ead.schemax.FieldName;

public class VisibilityEditor extends ComponentEditor<Visibility> implements
		Selector.SelectorListener<String> {

	private static final float PAD = WidgetBuilder.dpToPixels(10);

	private LogicExpressionWithWidget operationWidget;

	private Visibility visibility;

	public VisibilityEditor(Controller controller) {
		super(SkinConstants.IC_VISIBILITY, controller.getApplicationAssets()
				.getI18N().m("visibility"), ComponentIds.VISIBILITY, controller);
	}

	@Override
	protected void buildContent() {
		operationWidget = new Level2LogicExpressionWithWidget(controller,
				LogicExpressionWithWidget.OpValue.AND);
		operationWidget.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				controller.action(SetField.class, visibility,
						FieldName.CONDITION, operationWidget.getExpression());
			}
		});

		LinearLayout changeAndByOr = new LinearLayout(true);
		changeAndByOr.add(new Label(i18N.m("and"), skin,
				SkinConstants.STYLE_CONTEXT));
		changeAndByOr
				.add(WidgetBuilder.icon(SkinConstants.IC_YOYO,
						i18N.m("exchange_and_or"), SkinConstants.STYLE_GRAY))
				.marginLeft(PAD).marginRight(PAD);
		changeAndByOr.add(new Label(i18N.m("or"), skin,
				SkinConstants.STYLE_CONTEXT));

		changeAndByOr.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				operationWidget.invertOperation();
				controller.action(SetField.class, visibility,
						FieldName.CONDITION, operationWidget.getExpression());
				read(null, visibility);
			}
		});

		list.add(changeAndByOr).marginBottom(PAD).marginTop(PAD);
		list.add(operationWidget).expandX();
	}

	@Override
	protected void read(ModelEntity entity, Visibility component) {
		visibility = component;

		OperationsFactory of = new OperationsFactory();
		Operation exp = null;
		try {
			exp = (Operation) Parser.parse(visibility.getCondition(), of);
		} catch (Exception e) {
			System.err.println(visibility.getCondition());
		}

		if (exp != null
				&& !exp.getName().equals(operationWidget.getOp().getValue())) {
			operationWidget.invertOperation();
		}
		operationWidget.clearWidget();
		operationWidget.loadExpression(exp);

		if (operationWidget.isEmpty()) {
			operationWidget.resetWidget();
		}

	}

	@Override
	protected Visibility buildNewComponent() {
		visibility = new Visibility();
		visibility.setCondition("btrue");

		return visibility;
	}

	@Override
	public void selected(String selected) {

	}

	@Override
	public void cancelled() {

	}
}
