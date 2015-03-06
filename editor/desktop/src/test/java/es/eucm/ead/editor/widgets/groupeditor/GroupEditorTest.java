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
package es.eucm.ead.editor.widgets.groupeditor;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.engine.gdx.AbstractWidget;
import es.eucm.ead.editor.view.widgets.groupeditor.GroupEditor;
import es.eucm.ead.editor.view.widgets.groupeditor.GroupEditorConfiguration;
import es.eucm.ead.editor.widgets.AbstractWidgetTest;

/**
 * Created by angel on 20/03/14.
 */
public class GroupEditorTest extends AbstractWidgetTest {

	private static Drawable drawable;

	@Override
	public AbstractWidget createWidget(Controller controller) {
		setFillWindow(true);
		Skin skin = controller.getApplicationAssets().getSkin();
		drawable = skin.getDrawable("blank");
		final GroupEditor container = new GroupEditor(
				controller.getShapeRenderer(), new GroupEditorConfiguration());
		container.setBackground(skin.getDrawable("blank"));

		final Group root = new Group();
		container.setRootGroup(root);

		container.addListener(new InputListener() {
			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				if (keycode == Keys.A) {
					Actor a = new RectangleActor();
					root.addActor(a);
				}
				return true;
			}
		});
		return container;
	}

	public static void main(String args[]) {
		new LwjglApplication(new GroupEditorTest(), "Test for DragAndDrop",
				1000, 600);
	}

	public static class RectangleActor extends Group {

		public RectangleActor() {
			setColor((float) Math.random(), (float) Math.random(),
					(float) Math.random(), 1.0f);
			setPosition(((float) Math.random()) * 900,
					((float) Math.random()) * 400);
			setSize(((float) Math.random()) * 100 + 50,
					((float) Math.random()) * 100 + 50);
		}

		@Override
		protected void drawChildren(Batch batch, float parentAlpha) {
			batch.setColor(getColor());
			drawable.draw(batch, 0, 0, getWidth(), getHeight());
			batch.setColor(Color.WHITE);
		}

		@Override
		public void setPosition(float x, float y) {
			super.setPosition(x, y);
		}
	}
}
