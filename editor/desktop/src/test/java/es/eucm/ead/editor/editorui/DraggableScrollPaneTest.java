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
package es.eucm.ead.editor.editorui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.esotericsoftware.tablelayout.Cell;

import es.eucm.ead.editor.view.widgets.dragndrop.DraggableScrollPane;

public class DraggableScrollPaneTest extends EditorUITest {

	@Override
	protected void builUI(Group root) {
		Gdx.gl.glClearColor(1f, 1f, 1f, 1f);

		Skin skin = controller.getApplicationAssets().getSkin();

		final Table table = new Table();
		table.defaults().uniform();
		final DraggableScrollPane dragPane = new DraggableScrollPane(table);
		int rows = 50;
		for (int i = 0; i < rows; ++i) {
			for (int j = 1; j < 5; ++j) {
				Label label = null;
				if (MathUtils.randomBoolean()) {
					label = new Label("[label with position " + (i * rows + j)
							+ "]", skin);
					dragPane.addSource(new Source(label) {

						@Override
						public Payload dragStart(InputEvent event, float x,
								float y, int pointer) {
							dragPane.setCancelTouchFocus(false);
							dragPane.cancel();

							Payload payload = new Payload();
							Actor actor = getActor();
							payload.setDragActor(actor);
							Cell currentCell = table.getCell(actor);
							payload.setObject(currentCell);

							return payload;
						}

						@Override
						public void dragStop(InputEvent event, float x,
								float y, int pointer, Payload payload,
								Target target) {
							Cell sourceCell = (Cell) payload.getObject();
							sourceCell.setWidget(getActor());
						}
					});
				}
				table.add(label);
			}
			table.row();
		}

		Container container = new Container(dragPane).fill();
		container.setFillParent(true);
		root.addActor(container);
	}

	public static void main(String[] args) {
		new LwjglApplication(new DraggableScrollPaneTest(),
				"Draggable Scroll Pane test", 300, 300);
	}
}
