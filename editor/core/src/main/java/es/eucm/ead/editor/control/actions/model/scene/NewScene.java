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
package es.eucm.ead.editor.control.actions.model.scene;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.ModelAction;
import es.eucm.ead.editor.control.actions.model.SetSelection;
import es.eucm.ead.editor.control.commands.CompositeCommand;
import es.eucm.ead.editor.control.commands.FieldCommand;
import es.eucm.ead.editor.control.commands.ListCommand.AddToListCommand;
import es.eucm.ead.editor.control.commands.ResourceCommand.AddResourceCommand;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.schema.editor.components.Documentation;
import es.eucm.ead.schema.editor.components.EditState;
import es.eucm.ead.schema.editor.components.SceneMap;
import es.eucm.ead.schema.editor.data.Cell;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.FieldName;
import es.eucm.ead.schemax.entities.ResourceCategory;

/**
 * Creates a new empty scene and sets it as the current edited scene.
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> <em>String</em> the scene name</dd>
 * <dd>You can pass the following optional arguments:</dd>
 * <dd><strong>args[1]</strong> <em>String</em> Optional: the scene id</dd>
 * <dd><strong>args[2]</strong> <em>ModelEntity</em> Optional: the entity that
 * will be the scene</dd>
 * <dd>or</dd>
 * <dd><strong>args[1]</strong> <em>Integer</em> Optional: the row of the new
 * scene in the {@link SceneMap}</dd>
 * <dd><strong>args[2]</strong> <em>Integer</em> Optional: the column of the new
 * scene in the {@link SceneMap</dd>
 * </dl>
 */
public class NewScene extends ModelAction {

	private SetSelection setSelection;

	public NewScene() {
		super(true, false, new Class[] { String.class }, new Class[] {
				String.class, String.class, ModelEntity.class }, new Class[] {
				String.class, Integer.class, Integer.class });
	}

	@Override
	public void initialize(Controller controller) {
		super.initialize(controller);
		setSelection = controller.getActions().getAction(SetSelection.class);
	}

	@Override
	public CompositeCommand perform(Object... args) {
		Model model = controller.getModel();

		String id;
		ModelEntity scene;
		String name = (String) args[0];
		if (args.length == 3 && args[1] instanceof String) {
			id = (String) args[1];
			scene = (ModelEntity) args[2];
			Q.getComponent(scene, Documentation.class).setName(name);
		} else {
			id = model.createId(ResourceCategory.SCENE);
			scene = controller.getTemplates().createScene(name);
		}

		ModelEntity game = model.getGame();
		EditState editState = Q.getComponent(game, EditState.class);
		SceneMap sceneMap = Q.getComponent(game, SceneMap.class);

		CompositeCommand compositeCommand = new CompositeCommand();
		compositeCommand.addCommand(new AddResourceCommand(model, id, scene,
				ResourceCategory.SCENE));
		if (args.length < 3 || args[1] instanceof String) {
			createCell(id, sceneMap, compositeCommand);
		} else {
			createCellAt(id, (Integer) args[1], (Integer) args[2], sceneMap,
					compositeCommand);
		}
		compositeCommand.addCommand(new AddToListCommand(editState, editState
				.getSceneorder(), id));
		compositeCommand.addCommand(setSelection.perform(null,
				Selection.RESOURCE, id));
		compositeCommand.addCommand(setSelection.perform(Selection.RESOURCE,
				Selection.SCENE, scene));
		compositeCommand.addCommand(setSelection.perform(Selection.SCENE,
				Selection.EDITED_GROUP, scene));

		return compositeCommand;
	}

	/**
	 * Creates a cell with the given id in the first empty space found in the
	 * map. If there is no empty space a new row will be added.
	 * 
	 * @param id
	 * @param sceneMap
	 * @param compositeCommand
	 * @param compositeCommand
	 * @return
	 */
	private void createCell(String id, SceneMap sceneMap,
			CompositeCommand compositeCommand) {
		Cell cell = Q.createCell(id, sceneMap);
		if (cell != null) {
			compositeCommand.addCommand(new AddToListCommand(sceneMap, sceneMap
					.getCells(), cell));
		} else {

			// There are no empty spaces in our map, let's automatically create
			// a new row of cells
			int rows = sceneMap.getRows();
			createCellAt(id, rows, 0, sceneMap, compositeCommand);
			compositeCommand.addCommand(new FieldCommand(sceneMap,
					FieldName.ROWS, rows + 1));
		}
	}

	/**
	 * Creates a cell with the given rows and column and adds it to the list via
	 * an {@link AddToListCommand}.
	 * 
	 * @param id
	 * @param row
	 * @param column
	 * @param sceneMap
	 * @param compositeCommand
	 */
	private void createCellAt(String id, int row, int column,
			SceneMap sceneMap, CompositeCommand compositeCommand) {
		Cell cell = new Cell();
		// There are no empty spaces in our map, let's automatically create
		// a new row of cells
		cell.setSceneId(id);
		cell.setRow(row);
		cell.setColumn(column);

		compositeCommand.addCommand(new AddToListCommand(sceneMap, sceneMap
				.getCells(), cell));
	}
}
