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
package es.eucm.ead.editor.ui.scenes;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.model.scene.CreateGroup;
import es.eucm.ead.editor.control.actions.model.scene.MultipleActorTransformToEntity;
import es.eucm.ead.editor.control.actions.model.scene.RemoveChildrenFromEntity;
import es.eucm.ead.editor.control.actions.model.scene.Ungroup;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.view.widgets.groupeditor.GroupEditor.GroupEvent;
import es.eucm.ead.editor.view.widgets.groupeditor.GroupEditor.GroupListener;
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
	public void transformed(GroupEvent groupEvent, Array<Actor> transformed) {
		controller.action(MultipleActorTransformToEntity.class, transformed);
	}

	@Override
	public void deleted(GroupEvent groupEvent, Group parent,
			Array<Actor> deleted) {
		tmpModelEntities.clear();
		tmpModelEntities.add(Model.getModelEntity(parent));
		for (Actor actor : deleted) {
			tmpModelEntities.add(Model.getModelEntity(actor));
			controller.getEntitiesLoader().freeEntity(
					Model.getActorEntity(actor));
		}
		controller.action(RemoveChildrenFromEntity.class,
				tmpModelEntities.items);
	}

	@Override
	public void grouped(GroupEvent groupEvent, Group parent, Group newGroup,
			Array<Actor> grouped) {
		controller.action(CreateGroup.class, parent, newGroup, grouped);
	}

	@Override
	public void ungrouped(GroupEvent groupEvent, Group parent, Group oldGroup,
			Array<Actor> ungrouped) {
		controller.action(Ungroup.class, parent, oldGroup, ungrouped);
	}

	@Override
	public void endedGroupEdition(GroupEvent groupEvent, Group parent,
			Group oldGroup, Actor simplifiedGroup) {
		if (oldGroup == simplifiedGroup) {
			tmpActors.clear();
			tmpActors.add(oldGroup);
			for (Actor child : oldGroup.getChildren()) {
				tmpActors.add(child);
			}

			controller.action(MultipleActorTransformToEntity.class, tmpActors,
					true);
		}
	}
}
