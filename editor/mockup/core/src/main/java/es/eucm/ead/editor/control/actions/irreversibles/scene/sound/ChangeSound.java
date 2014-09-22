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
package es.eucm.ead.editor.control.actions.irreversibles.scene.sound;

import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.irreversibles.IrreversibleAction;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Model.Resource;
import es.eucm.ead.schema.effects.PlaySound;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.entities.ResourceCategory;

/**
 * Changes the {@link PlaySound#setVolume(float)} or
 * {@link PlaySound#setLoop(boolean)} of a given {@link PlaySound}. </p>
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> <em>{@link PlaySound}</em> to be changed</dd>
 * <dd><strong>args[1]</strong> <em>{@link Float} or {@link Boolean}</em> the
 * new value</dd>
 * </dl>
 */
public class ChangeSound extends IrreversibleAction {

	public ChangeSound() {
		super(ResourceCategory.SCENE, true, false, new Class[] {
				PlaySound.class, Boolean.class }, new Class[] {
				PlaySound.class, Float.class });
	}

	@Override
	protected void action(ModelEntity entity, Object[] args) {

		PlaySound playSound = (PlaySound) args[0];
		Object value = args[1];
		if (value instanceof Boolean) {
			playSound.setLoop((Boolean) value);
		} else if (value instanceof Float) {
			playSound.setVolume((Float) value);
		}
		modifyResource();
	}

	private void modifyResource() {
		Model model = controller.getModel();
		Object single = model.getSelection().getSingle(Selection.RESOURCE);
		if (single != null) {
			String id = single.toString();
			Resource resource = model.getResource(id, ResourceCategory.SCENE);
			if (resource != null) {
				resource.setModified(true);
			}
		}
	}

}
