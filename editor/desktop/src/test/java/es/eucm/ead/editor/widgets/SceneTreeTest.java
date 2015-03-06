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
package es.eucm.ead.editor.widgets;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.engine.gdx.AbstractWidget;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.editor.view.widgets.scenetree.SceneTree;
import es.eucm.ead.editor.view.widgets.scenetree.SceneTree.Node;
import es.eucm.ead.editor.view.widgets.scenetree.SceneTreeListener;

/**
 * Created by angel on 20/03/14.
 */
public class SceneTreeTest extends AbstractWidgetTest {
	private int i = 0;

	@Override
	public AbstractWidget createWidget(Controller controller) {
		Skin skin = controller.getApplicationAssets().getSkin();
		final SceneTree sceneTree = new SceneTree(skin);

		Drawable paste = skin.getDrawable("clipboard");
		Drawable copy = skin.getDrawable("copy");
		Drawable undo = skin.getDrawable("undo");

		sceneTree.addNode("1 Test node", paste, copy)
				.addNode("1.1 Ñor ñor", undo)
				.addNode("1.1.1. Another node over here", undo, copy, paste)
				.addNode("1.1.1.1. kñjañs fasñ kldfja ñskj fañsk afsdf");
		sceneTree.addNode("2 Another node", copy).addNode("2.1 Otro");
		sceneTree.addNode("3 Another node", copy).addNode("3.1 Otro");
		sceneTree.addNode("4 Another node", copy).addNode("4.1 Otro");
		sceneTree.addNode("5 Another node", copy);
		sceneTree.addNode("6 Another node", copy, undo);
		sceneTree.addNode("7 Another node", copy, paste);
		sceneTree.addNode("8 Another node", copy);

		LinearLayout container = new LinearLayout(true);
		container.add(sceneTree).expandX();

		sceneTree.addListener(new SceneTreeListener() {
			@Override
			public boolean nodeAdded(SceneTreeEvent event, Node node) {
				System.out.println("Node added: " + node.getNodeName());
				return true;
			}

			@Override
			public boolean nodeRemoved(SceneTreeEvent event, Node node) {
				System.out.println("Node removed: " + node.getNodeName());
				return true;
			}

			@Override
			public boolean nodeUpdated(SceneTreeEvent event, Node node) {
				System.out.println(node.getNodeName());
				return true;
			}
		});

		sceneTree.addListener(new InputListener() {

			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				if (keycode == Keys.A) {
					sceneTree.addNode("New node " + i++);
					return true;
				}
				return false;
			}
		});
		container.setWidth(1000);

		return container;
	}

	public static void main(String args[]) {
		new LwjglApplication(new SceneTreeTest(), "Test for Scene Tree", 1000,
				600);
	}
}
