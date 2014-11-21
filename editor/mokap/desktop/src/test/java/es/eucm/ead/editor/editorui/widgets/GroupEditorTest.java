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
package es.eucm.ead.editor.editorui.widgets;

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

import es.eucm.ead.editor.editorui.UITest;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.builders.scene.groupeditor.GroupEditor;
import es.eucm.ead.engine.I18N;

/**
 * Created by angel on 20/03/14.
 */
public class GroupEditorTest extends UITest {

	private static Drawable drawable;

	public static void main(String args[]) {
		new LwjglApplication(new GroupEditorTest(), "Test for GroupEditorTest",
				1000, 600);
	}

	@Override
	protected Actor buildUI(Skin skin, I18N i18n) {
		drawable = skin.getDrawable(SkinConstants.DRAWABLE_BLANK);
		final GroupEditor container = new GroupEditor(skin);

		final Group root = new Group();
		container.setRootGroup(root);

		container.addListener(new InputListener() {
			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				if (keycode == Keys.A) {
					Actor a = new RectangleActor();
					root.addActor(a);
				} else if (keycode == Keys.M) {
					container.setMultipleSelection(true);
				} else if (keycode == Keys.N) {
					container.setMultipleSelection(false);
				}
				return true;
			}
		});
		stage.setKeyboardFocus(container);
		RectangleActor rectangleActor = new RectangleActor();
		rectangleActor.setBounds(0, 0, 50, 50);
		rectangleActor.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				root.addActor(new RectangleActor());
				return true;
			}
		});
		container.addActor(rectangleActor);
		container.setFillParent(true);
		return container;
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
