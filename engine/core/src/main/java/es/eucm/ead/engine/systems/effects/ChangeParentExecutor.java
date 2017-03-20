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
package es.eucm.ead.engine.systems.effects;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.engine.variables.VariablesManager;
import es.eucm.ead.schema.effects.ChangeParent;

/**
 * Created by jtorrente on 16/11/2015.
 */
public class ChangeParentExecutor extends EffectExecutor<ChangeParent> {

	public static final String CHANGE_PARENT_EXECUTOR = "ChangeParentExecutor";
	private VariablesManager variablesManager;

	public ChangeParentExecutor(VariablesManager variablesManager) {
		this.variablesManager = variablesManager;
	}

	@Override
	public void execute(Entity target, ChangeParent effect) {
		if (!(target instanceof EngineEntity)) {
			Gdx.app.debug(CHANGE_PARENT_EXECUTOR,
					"Can't execute effect - target is not of type EngineEntity");
			return;
		}

		Object value = variablesManager.evaluateExpression(effect
				.getNewParent());
		EngineEntity newParent = null;
		if (value instanceof EngineEntity) {
			newParent = (EngineEntity) value;
		} else if (value instanceof Array) {
			Array array = (Array) value;
			if (array.size > 0 && array.get(0) instanceof EngineEntity) {
				newParent = (EngineEntity) array.get(0);
			}
		}

		if (newParent == null) {
			Gdx.app.debug(CHANGE_PARENT_EXECUTOR,
					"Can't execute effect - new parent is not of type EngineEntity");
			return;
		}

		EngineEntity child = (EngineEntity) target;
		Group childGroup = child.getGroup();
		Vector2 stageOrigin = new Vector2(childGroup.getX(), childGroup.getY());
		stageOrigin = childGroup.localToStageCoordinates(stageOrigin);
		childGroup.remove();
		newParent.getGroup().addActor(childGroup);
		Vector2 newLocalCoordinates = childGroup
				.stageToLocalCoordinates(stageOrigin);
		childGroup.setX(newLocalCoordinates.x);
		childGroup.setY(newLocalCoordinates.y);
	}
}
