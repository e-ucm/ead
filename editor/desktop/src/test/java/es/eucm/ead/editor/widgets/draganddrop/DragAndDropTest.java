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
package es.eucm.ead.editor.widgets.draganddrop;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.widgets.AbstractWidget;
import es.eucm.ead.editor.view.widgets.drag.DragAndDropContainer;
import es.eucm.ead.editor.widgets.AbstractWidgetTest;

/**
 * Created by angel on 20/03/14.
 */
public class DragAndDropTest extends AbstractWidgetTest {
	@Override
	public AbstractWidget createWidget(Controller controller) {
		Skin skin = controller.getApplicationAssets().getSkin();
		final Drawable drawable = skin.getDrawable("warning");
		final DragAndDropContainer container = new DragAndDropContainer(skin);
		container.setSize(1000, 600);
		container.setSceneSize(400, 300);

		container.addListener(new InputListener() {
			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				if (keycode == Keys.A) {
					container.addActorToScene(new DummyActor(Color.WHITE,
							drawable));
				}
				return true;
			}
		});

		return container;
	}

	public static void main(String args[]) {
		new LwjglApplication(new DragAndDropTest(), "Test for DragAndDrop",
				1000, 600);
	}

	public static class DummyActor extends Group {

		private Color color;

		protected Drawable drawable;

		public DummyActor(Color color, Drawable drawable) {
			this.color = color;
			this.drawable = drawable;
			this.setWidth(drawable.getMinWidth());
			this.setHeight(drawable.getMinHeight());
		}

		@Override
		protected void drawChildren(Batch batch, float parentAlpha) {
			super.drawChildren(batch, parentAlpha);
			batch.setColor(color);
			drawable.draw(batch, 0, 0, getWidth(), getHeight());
			batch.setColor(Color.WHITE);
		}
	}
}
