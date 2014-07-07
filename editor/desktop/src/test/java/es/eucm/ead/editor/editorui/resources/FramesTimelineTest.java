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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Container;

import es.eucm.ead.editor.assets.EditorGameAssets;
import es.eucm.ead.editor.editorui.EditorUITest;
import es.eucm.ead.editor.ui.resources.frames.FramesTimeline;
import es.eucm.ead.schema.renderers.Frame;
import es.eucm.ead.schema.renderers.Frames;
import es.eucm.ead.schema.renderers.Image;

public class FramesTimelineTest extends EditorUITest {

	@Override
	protected void builUI(Group root) {

		EditorGameAssets gameAssets = controller.getEditorGameAssets();

		// Prepare some images and frames...
		gameAssets.setLoadingPath("framesTest", true);
		controller.getCommands().pushContext();

		Frames frames = new Frames();
		for (int i = 0; i < 20; ++i) {
			Frame frame = new Frame();
			Image image = new Image();
			image.setUri("externalResources/ic_" + (i % 10) + ".png");

			frame.setRenderer(image);
			frame.setTime(MathUtils.random(.1f, .5f));
			frames.getFrames().add(frame);
		}

		// Create the widget
		FramesTimeline widget = new FramesTimeline(controller);
		widget.setFrames(frames.getFrames());

		Container container = new Container(widget);
		container.setFillParent(true);
		root.addActor(container);
	}

	public static void main(String[] args) {
		new LwjglApplication(new FramesTimelineTest(), "Frames Timeline test",
				400, 600);
	}
}
