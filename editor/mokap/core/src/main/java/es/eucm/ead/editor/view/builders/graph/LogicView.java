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
package es.eucm.ead.editor.view.builders.graph;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import es.eucm.commander.Commander;
import es.eucm.commander.Commands;
import es.eucm.commander.Commands.CommandListener;
import es.eucm.commander.Commands.Type;
import es.eucm.commander.actions.Action;
import es.eucm.commander.actions.ModifyList;
import es.eucm.commander.actions.SetSelectionHierarchy;
import es.eucm.commander.commands.Command;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.MokapController.BackListener;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.builders.ViewBuilder;
import es.eucm.ead.editor.view.builders.graph.effects.ChangeVarNodeBuilder;
import es.eucm.ead.editor.view.builders.graph.effects.PlaySoundNodeBuilder;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.components.Logic;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.ComponentIds;
import es.eucm.gdx.CommanderUtils;
import es.eucm.gdx.WidgetBuilder;
import es.eucm.gdx.WidgetUtils;
import es.eucm.gdx.wrappers.ArrayWrapperFactory;
import es.eucm.graph.core.Grapher;

public class LogicView implements ViewBuilder, BackListener {

	private Grapher grapher;

	private Commander commander;

	private Controller controller;

	@Override
	public void initialize(Controller c) {
		this.controller = c;
		commander = new Commander();
		commander.getAction(ModifyList.class).registerListWrapperFactory(
				Array.class, new ArrayWrapperFactory());
		Skin skin = controller.getApplicationAssets().getSkin();
		I18N i18N = controller.getApplicationAssets().getI18N();
		CommanderUtils.setCommander(commander);
		WidgetBuilder.setSkin(skin);
		WidgetUtils.modalContainer.setBackground(skin
				.getDrawable(SkinConstants.DRAWABLE_SEMI_TRANSPARENT));
		grapher = new Grapher(commander, skin, new LogicGraphBuilder(),
				new InitNodeBuilder(skin, i18N), new ChangeVarNodeBuilder(
						commander, controller), new PlaySoundNodeBuilder(
						commander, controller));

		grapher.getGallery()
				.getToolbar()
				.addMainAction(skin.getDrawable(SkinConstants.IC_GO),
						new ClickListener() {
							@Override
							public void clicked(InputEvent event, float x,
									float y) {
								controller.getViews().back();
							}
						});
		grapher.getGallery()
				.getToolbar()
				.addMainAction(
						new Label(i18N.m("scene.logic"), skin,
								SkinConstants.STYLE_TOOLBAR));

		commander.getAction(ReadGraph.class).grapher = grapher;
		commander.getCommands().addListener(new CommandListener() {
			@Override
			public void updated(Commands commands, Type type, Command command) {
				String id = (String) controller.getModel().getSelection()
						.getSingle(Selection.MOKAP_RESOURCE);
				controller.getModel().getResource(id).setModified(true);
			}
		});
	}

	@Override
	public Actor getView(Object... args) {
		ModelEntity scene = (ModelEntity) controller.getModel().getSelection()
				.getSingle(Selection.SCENE);
		Logic logic = (Logic) Q.getComponentById(scene, ComponentIds.LOGIC);
		if (logic == null) {
			logic = new Logic();
			logic.setId(ComponentIds.LOGIC);
			scene.getComponents().add(logic);
		}
		commander.perform(SetSelectionHierarchy.class, null, Selection.SCENE,
				scene, Grapher.GRAPHS_LIST, logic.getSequences());
		grapher.prepare();
		commander.getCommands().pushStack();
		return grapher;
	}

	@Override
	public void release(Controller controller) {
		commander.getCommands().popStack(false);
	}

	@Override
	public boolean onBackPressed() {
		if (!WidgetUtils.hideModal()) {
			return grapher.back();
		} else {
			return true;
		}
	}

	public static class ReadGraph extends Action {

		public Grapher grapher;

		@Override
		public Command perform(Object... args) {
			grapher.readGraph();
			return null;
		}
	}
}
