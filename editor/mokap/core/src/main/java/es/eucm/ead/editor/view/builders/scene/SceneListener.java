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
package es.eucm.ead.editor.view.builders.scene;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.model.ExitGroupEdition;
import es.eucm.ead.editor.control.actions.model.SetSceneEditState;
import es.eucm.ead.editor.control.actions.model.SetSelection;
import es.eucm.ead.editor.control.actions.model.scene.ActorTransformToEntity;
import es.eucm.ead.editor.control.actions.model.scene.MultipleActorTransformToEntity;
import es.eucm.ead.editor.control.actions.model.scene.NewGroupHierarchyToEntities;
import es.eucm.ead.editor.control.actions.model.scene.RemoveChildrenFromEntity;
import es.eucm.ead.editor.control.actions.model.scene.UngroupHierarchyToEntities;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.view.builders.scene.groupeditor.GroupEditor.GroupEvent;
import es.eucm.ead.editor.view.builders.scene.groupeditor.GroupEditor.GroupListener;
import es.eucm.ead.engine.utils.EngineUtils;
import es.eucm.ead.schema.entities.ModelEntity;

/**
 * Created by angel on 20/05/14.
 */
public class SceneListener extends GroupListener {

	private Array<ModelEntity> tmpModelEntities = new Array<ModelEntity>();
	private Array<Actor> tmpActors = new Array<Actor>();

	private Controller controller;

	public SceneListener(Controller controller) {
		this.controller = controller;
	}

	@Override
	public void selectionUpdated(GroupEvent groupEvent, Array<Actor> selection) {
		Object[] arguments = new Object[selection.size + 2];
		arguments[0] = Selection.EDITED_GROUP;
		arguments[1] = Selection.SCENE_ELEMENT;
		int i = 2;
		for (Actor actor : selection) {
			arguments[i++] = Q.getModelEntity(actor);
		}
		controller.action(SetSelection.class, arguments);
	}

	@Override
	public void transformed(GroupEvent groupEvent, Group parent,
			Array<Actor> transformed) {
		tmpActors.clear();
		tmpActors.add(parent);
		tmpActors.addAll(transformed);
		controller.action(MultipleActorTransformToEntity.class, transformed);
	}

	@Override
	public void deleted(GroupEvent groupEvent, Group parent,
			Array<Actor> deleted) {
		tmpModelEntities.clear();
		for (Actor actor : deleted) {
			tmpModelEntities.add(Q.getModelEntity(actor));
			controller.getEngine().getGameLoop()
					.removeEntity(EngineUtils.getActorEntity(actor));
		}
		controller.action(RemoveChildrenFromEntity.class,
				Q.getModelEntity(parent), tmpModelEntities);
	}

	@Override
	public void grouped(GroupEvent groupEvent, Group parent, Group newGroup,
			Array<Actor> grouped) {
		controller.action(NewGroupHierarchyToEntities.class, parent, newGroup,
				grouped);
	}

	@Override
	public void ungrouped(GroupEvent groupEvent, Group parent, Group oldGroup,
			Array<Actor> ungrouped) {
		controller.action(UngroupHierarchyToEntities.class, parent, oldGroup,
				ungrouped);
	}

	@Override
	public void enteredGroupEdition(GroupEvent groupEvent, Group group) {
		// Group transformation is reset when entering a group
		controller.getCommands().pushStack();
		controller.action(ActorTransformToEntity.class, group);
		ModelEntity modelEntity = Q.getModelEntity(group);
		if (modelEntity != null) {
			controller.action(SetSelection.class, Selection.SCENE,
					Selection.EDITED_GROUP, modelEntity);
		}

	}

	@Override
	public void exitedGroupEdition(GroupEvent groupEvent, Group parent,
			Group oldGroup, Actor simplifiedGroup) {
		controller.getCommands().popStack(true);
		controller.action(ExitGroupEdition.class, parent, oldGroup,
				simplifiedGroup);
	}

	@Override
	public void containerUpdated(GroupEvent event, Group container) {
		controller.action(SetSceneEditState.class, controller.getModel()
				.getSelection().getSingle(Selection.SCENE), container.getX(),
				container.getY(), container.getScaleX());
	}
}
