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
package es.eucm.ead.editor.ui.scenes.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.model.ChangeCellPosition;
import es.eucm.ead.editor.control.actions.model.IncreaseMapSize;
import es.eucm.ead.editor.control.actions.model.SetSelection;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Model.FieldListener;
import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.Model.SelectionListener;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.model.events.FieldEvent;
import es.eucm.ead.editor.model.events.ListEvent;
import es.eucm.ead.editor.model.events.SelectionEvent;
import es.eucm.ead.editor.view.listeners.SceneNameListener;
import es.eucm.ead.editor.view.widgets.IconButton;
import es.eucm.ead.editor.view.widgets.dragndrop.DraggableGridLayout;
import es.eucm.ead.editor.view.widgets.dragndrop.DraggableLinearLayout.DropListener;
import es.eucm.ead.schema.editor.components.GameData;
import es.eucm.ead.schema.editor.components.SceneMap;
import es.eucm.ead.schema.editor.data.Cell;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.FieldName;
import es.eucm.ead.schemax.entities.ResourceCategory;

/**
 * This widget displays the information stored in the {@link SceneMap}. A map
 * with the available scenes in our game where the user can drag'n drop to
 * reorder the scenes as he pleases.
 */
public class SceneMapWidget extends DraggableGridLayout {

	private SceneMap sceneMap;
	private Controller controller;
	private ButtonGroup buttonGroup;
	private NameListener sceneNameListener;
	private FieldListener sceneMapListener = new SceneMapListener();
	private ModelListener<ListEvent> cellsListener = new CellsListener();
	private SelectionListener selectionListener = new MapSelectionListener();

	public SceneMapWidget(Controller control) {

		controller = control;
		buttonGroup = new ButtonGroup();
		sceneNameListener = new NameListener(controller);
		addListener(new DropListener<DropGridEvent>() {

			@Override
			public void actorDropped(DropGridEvent event) {

				controller.action(ChangeCellPosition.class, event.getOldRow(),
						event.getOldColumn(), event.getNewRow(),
						event.getNewColumn());
			}
		});

		Skin skin = controller.getApplicationAssets().getSkin();
		String plusIcon = "plus24x24";
		final Button moreStartRow = new IconButton(plusIcon, skin);
		final Button moreFinalRow = new IconButton(plusIcon, skin);
		final Button moreStartCol = new IconButton(plusIcon, skin);
		final Button moreFinalCol = new IconButton(plusIcon, skin);

		controller.getModel().addSelectionListener(selectionListener);
		ClickListener moreButton = new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {

				Actor actor = event.getListenerActor();
				if (actor == moreStartRow) {
					controller.action(IncreaseMapSize.class, true,
							IncreaseMapSize.BEGINING);
				} else if (actor == moreFinalRow) {
					controller.action(IncreaseMapSize.class, true,
							IncreaseMapSize.END);

					// Scroll at the maxY once the layout method was invoked
					Gdx.app.postRunnable(new Runnable() {

						@Override
						public void run() {
							setScrollY(getMaxY());
						}

					});
				} else if (actor == moreStartCol) {
					controller.action(IncreaseMapSize.class, false,
							IncreaseMapSize.BEGINING);
				} else if (actor == moreFinalCol) {
					controller.action(IncreaseMapSize.class, false,
							IncreaseMapSize.END);

					// Scroll at the maxX once the layout method was invoked
					Gdx.app.postRunnable(new Runnable() {

						@Override
						public void run() {
							setScrollX(getMaxX());
						}

					});
				}
			}
		};

		moreStartRow.addListener(moreButton);
		moreFinalRow.addListener(moreButton);
		moreStartCol.addListener(moreButton);
		moreFinalCol.addListener(moreButton);

		Table table = new Table();
		table.add(moreStartRow).colspan(3).expandX().fillX();
		table.row();
		table.add(moreStartCol).expandY().fillY();
		table.add(getWidget()).expand().fill();
		table.add(moreFinalCol).expandY().fillY();
		table.row();
		table.add(moreFinalRow).colspan(3).expandX().fillX();
		setWidget(table);
	}

	public void prepare() {
		Model model = controller.getModel();

		sceneMap = Q.getComponent(model.getGame(), SceneMap.class);
		model.addListListener(sceneMap.getCells(), cellsListener);
		model.addFieldListener(sceneMap, sceneMapListener);

		controller.action(SetSelection.class, null, Selection.SCENE_MAP,
				sceneMap);
		controller.action(SetSelection.class, Selection.SCENE_MAP,
				Selection.SCENE, model.getResource(
						Q.getComponent(model.getGame(), GameData.class)
								.getInitialScene(), ResourceCategory.SCENE));
	}

	public void release() {

		sceneNameListener.remove();
		Model model = controller.getModel();
		model.removeListenerFromAllTargets(cellsListener);
		model.removeListenerFromAllTargets(sceneMapListener);
	}

	/**
	 * Reads the model and builds a {@link SceneMapWidget}.
	 */
	private void refreshSceneMap() {
		reset(sceneMap.getRows(), sceneMap.getColumns());

		buttonGroup.getButtons().clear();
		buttonGroup.getAllChecked().clear();
		for (Cell cell : sceneMap.getCells()) {
			SceneWidget sceneWidget = createSceneWidgetFromId(cell.getSceneId());
			addAt(cell.getRow(), cell.getColumn(), sceneWidget);
			buttonGroup.add(sceneWidget);
		}
	}

	private SceneWidget createSceneWidgetFromId(String id) {
		return new SceneWidget(controller, id);
	}

	/**
	 * Used to know when a cell was removed/added from/to the map. Used when a
	 * {@link DropGridEvent} is received.
	 */
	private class CellsListener implements ModelListener<ListEvent> {

		@Override
		public void modelChanged(ListEvent event) {

			Cell cell = (Cell) event.getElement();
			switch (event.getType()) {
			case REMOVED:
				addAt(cell.getRow(), cell.getColumn(), null);
				break;
			case ADDED:
				int row = cell.getRow();
				int column = cell.getColumn();
				String sceneId = cell.getSceneId();

				SceneWidget sceneWidget = (SceneWidget) getCellAt(row, column)
						.getWidget();
				// Don't create the SameWidget twice
				if (sceneWidget == null
						|| !sceneWidget.getSceneId().equals(sceneId)) {
					SceneWidget newSceneWidget = createSceneWidgetFromId(sceneId);
					addAt(row, column, newSceneWidget);
					newSceneWidget.setChecked(true);
				} else {
					sceneWidget.setChecked(true);
				}
				break;
			}
		}
	}

	/**
	 * Used to know when to load new {@link SceneWidget} or when to notify the
	 * widgets that a new {@link SceneWidget} has received focus.
	 */
	private class MapSelectionListener implements SelectionListener {

		private boolean isMap;

		@Override
		public void modelChanged(SelectionEvent event) {
			if (event.getType() == SelectionEvent.Type.FOCUSED) {
				if (isMap) {
					refreshSceneMap();
				} else {
					sceneNameListener
							.setUp((ModelEntity) event.getSelection()[0]);
				}
			}
		}

		@Override
		public boolean listenToContext(String contextId) {
			isMap = Selection.SCENE_MAP.equals(contextId);
			return isMap || Selection.SCENE.equals(contextId);
		}
	}

	/**
	 * Notifies that the {@link es.eucm.ead.schema.editor.components.SceneMap}
	 * rows or columns fields have changed.
	 */
	private class SceneMapListener implements FieldListener {

		@Override
		public void modelChanged(FieldEvent event) {
			refreshSceneMap();
		}

		@Override
		public boolean listenToField(String fieldName) {
			return FieldName.ROWS.equals(fieldName)
					|| FieldName.COLUMNS.equals(fieldName);
		}
	}

	/**
	 * Updates the name of the selected scene.
	 */
	private class NameListener extends SceneNameListener {

		public NameListener(Controller controller) {
			super(controller);
		}

		@Override
		public void nameChanged(String name) {
			Cell cellFromId = Q.getCellFromId(sceneId, sceneMap.getCells());
			SceneWidget widget = (SceneWidget) getCellAt(cellFromId.getRow(),
					cellFromId.getColumn()).getWidget();
			widget.setName(name);
		}
	}
}
