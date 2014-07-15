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
package es.eucm.ead.editor.editorui.resources;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Container;

import es.eucm.ead.editor.assets.EditorGameAssets;
import es.eucm.ead.editor.editorui.EditorUITest;
import es.eucm.ead.editor.ui.resources.frames.AnimationEditor;
import es.eucm.ead.schema.renderers.Frame;
import es.eucm.ead.schema.renderers.Frames;
import es.eucm.ead.schema.renderers.Image;

public class AnimationEditorTest extends EditorUITest {

	@Override
	protected void builUI(Group root) {

		EditorGameAssets gameAssets = controller.getEditorGameAssets();

		// Prepare some images and frames...
		gameAssets.setLoadingPath("cooldemo", true);
		controller.getCommands().pushStack();

		Frames frames = new Frames();
		for (int i = 1; i < 11; ++i) {
			Frame frame = new Frame();
			Image image = new Image();
			image.setUri("images/p1_walk" + (i < 10 ? "0" + i : i) + ".png");

			frame.setRenderer(image);
			frame.setTime(.01f * i);
			frames.getFrames().add(frame);
		}

		// Create the widget
		AnimationEditor animEditor = new AnimationEditor(controller);
		animEditor.initialize(frames);

		Container container = new Container(animEditor);
		container.fill();
		container.setFillParent(true);
		root.addActor(container);
	}

	public static void main(String[] args) {
		new LwjglApplication(new AnimationEditorTest(),
				"Animation Editor test", 450, 700);
	}
}
