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
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.model.AddToMap;
import es.eucm.ead.editor.control.actions.model.ChangeCellPosition;
import es.eucm.ead.editor.control.actions.model.SetSelection;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Model.FieldListener;
import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.Model.SelectionListener;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.model.events.FieldEvent;
import es.eucm.ead.editor.model.events.ListEvent;
import es.eucm.ead.editor.model.events.SelectionEvent;
import es.eucm.ead.editor.view.widgets.dragndrop.DraggableGridLayout;
import es.eucm.ead.editor.view.widgets.dragndrop.DraggableLinearLayout.DropListener;
import es.eucm.ead.schema.editor.components.SceneMap;
import es.eucm.ead.schema.editor.data.Cell;
import es.eucm.ead.schemax.FieldName;

/**
 * This widget displays the information stored in the {@link SceneMap}. A map
 * with the available scenes in our game where the user can drag'n drop to
 * reorder the scenes as he pleases.
 */
public class SceneMapWidget extends DraggableGridLayout {

	private SceneMap sceneMap;
	private Controller controller;
	private FieldListener sceneMapListener = new SceneMapListener();
	private ModelListener<ListEvent> tilesListener = new CellsListener();
	private SelectionListener selectionListener = new MapSelectionListener();

	public SceneMapWidget(Controller control) {

		controller = control;

		addListener(new DropListener<DropGridEvent>() {

			@Override
			public void actorDropped(DropGridEvent event) {

				controller.action(ChangeCellPosition.class, event.getOldRow(),
						event.getOldColumn(), event.getNewRow(),
						event.getNewColumn());
			}
		});

		Skin skin = controller.getApplicationAssets().getSkin();
		final Button moreStartRow = new TextButton("+", skin);
		final Button moreFinalRow = new TextButton("+", skin);
		final Button moreStartCol = new TextButton("+", skin);
		final Button moreFinalCol = new TextButton("+", skin);

		controller.getModel().addSelectionListener(selectionListener);
		ClickListener moreButton = new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {

				Actor actor = event.getListenerActor();
				if (actor == moreStartRow) {
					controller.action(AddToMap.class, true, AddToMap.BEGINING);
				} else if (actor == moreFinalRow) {
					controller.action(AddToMap.class, true, AddToMap.END);

					// Scroll at the maxY once the layout method was invoked
					Gdx.app.postRunnable(new Runnable() {

						@Override
						public void run() {
							setScrollY(getMaxY());
						}

					});
				} else if (actor == moreStartCol) {
					controller.action(AddToMap.class, false, AddToMap.BEGINING);
				} else if (actor == moreFinalCol) {
					controller.action(AddToMap.class, false, AddToMap.END);

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

	public void initialize() {
		Model model = controller.getModel();

		sceneMap = Q.getComponent(model.getGame(), SceneMap.class);
		model.addListListener(sceneMap.getCells(), tilesListener);
		model.addFieldListener(sceneMap, sceneMapListener);

		controller.action(SetSelection.class, null, Selection.SCENE_MAP,
				sceneMap);
	}

	public void release() {
		Model model = controller.getModel();
		model.removeListenerFromAllTargets(tilesListener);
		model.removeListenerFromAllTargets(sceneMapListener);
	}

	/**
	 * Reads the model and builds a {@link SceneMapWidget}.
	 */
	private void refreshSceneMap() {
		reset(sceneMap.getRows(), sceneMap.getColumns());

		for (Cell cell : sceneMap.getCells()) {
			addAt(cell.getRow(), cell.getColumn(),
					createSceneWidgetFromId(cell.getSceneId()));
		}
		invalidateHierarchy();
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
				addAt(cell.getRow(), cell.getColumn(),
						createSceneWidgetFromId(cell.getSceneId()));
				break;
			}
		}
	}

	/**
	 * Used to know when to load new {@link SceneWidget} or when to notify the
	 * widgets that a new {@link SceneWidget} has received focus.
	 */
	private class MapSelectionListener implements SelectionListener {

		@Override
		public void modelChanged(SelectionEvent event) {
			if (event.getType() == SelectionEvent.Type.FOCUSED) {
				refreshSceneMap();
			}
		}

		@Override
		public boolean listenToContext(String contextId) {
			return Selection.SCENE_MAP.equals(contextId);
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
}
