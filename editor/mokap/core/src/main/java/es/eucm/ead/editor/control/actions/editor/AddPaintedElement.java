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
package es.eucm.ead.editor.control.actions.editor;

import es.eucm.ead.editor.control.actions.EditorAction;
import es.eucm.ead.editor.control.actions.model.AddSceneElement;
import es.eucm.ead.editor.control.background.BackgroundExecutor;
import es.eucm.ead.editor.control.background.BackgroundExecutor.BackgroundTaskListener;
import es.eucm.ead.editor.control.background.BackgroundTask;
import es.eucm.ead.editor.view.builders.scene.draw.BrushStrokes;
import es.eucm.ead.schema.entities.ModelEntity;

/**
 * 
 * <p>
 * Creates an element from the Pixmap that has been drawn.
 * </p>
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> <em>{@link BrushStrokes}</em> the object with
 * which the element has been drawn.</dd>
 * </dl>
 */
public class AddPaintedElement extends EditorAction {

	private BrushStrokes brushStrokes;

	public AddPaintedElement() {
		super(true, false, BrushStrokes.class);
	}

	@Override
	public void perform(Object... args) {
		brushStrokes = (BrushStrokes) args[0];
		controller.getBackgroundExecutor().submit(saveTask, saveListener);
	}

	private final BackgroundTask<Boolean> saveTask = new BackgroundTask<Boolean>() {
		@Override
		public Boolean call() throws Exception {

			boolean saved = brushStrokes.save();
			brushStrokes.release();
			return saved;
		}
	};

	private final BackgroundTaskListener<Boolean> saveListener = new BackgroundTaskListener<Boolean>() {

		@Override
		public void completionPercentage(float percentage) {

		}

		@Override
		public void done(BackgroundExecutor backgroundExecutor, Boolean result) {
			if (result) {
				ModelEntity sceneElement = brushStrokes.createSceneElement();
				controller.action(AddSceneElement.class, sceneElement);
			}
		}

		@Override
		public void error(Throwable e) {

		}
	};

}
