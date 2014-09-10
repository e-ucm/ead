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

import com.badlogic.gdx.scenes.scene2d.Actor;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.irreversibles.scene.RemoveComponents;
import es.eucm.ead.editor.model.events.SelectionEvent;
import es.eucm.ead.schema.components.ModelComponent;
import es.eucm.ead.schema.entities.ModelEntity;

public abstract class PrefabComponentPanel extends PrefabPanel {

	protected ModelComponent component;

	protected String componentId;

	public PrefabComponentPanel(String icon, float size, String panelName,
			String componentId, Controller controller, Actor touchable) {
		super(icon, size, panelName, controller, touchable);
		this.componentId = componentId;
	}

	@Override
	protected void trashClicked() {
		if (component != null) {
			controller.action(RemoveComponents.class, componentId);
			component = null;
			setUsed(false);
		}

		actualizePanel();
	}

	@Override
	protected abstract void actualizePanel();

	@Override
	public void modelChanged(SelectionEvent event) {
		if (selection.get(Selection.SCENE_ELEMENT).length == 1) {
			this.setDisabled(false);
			this.component = null;

			ModelEntity modelEntity = (ModelEntity) selection
					.getSingle(Selection.SCENE_ELEMENT);
			for (ModelComponent component : modelEntity.getComponents()) {
				String id = component.getId();
				if (id != null && id.equals(componentId)) {
					this.component = component;
					setUsed(true);
					return;
				}
			}
			setUsed(false);
		} else {
			this.setDisabled(true);
			setUsed(false);
		}
	}

	@Override
	protected void showPanel() {
		actualizePanel();
		super.showPanel();
	}

}
