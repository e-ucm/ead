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

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;

import es.eucm.ead.editor.assets.EditorGameAssets;
import es.eucm.ead.editor.view.widgets.dragndrop.DraggableGridLayout;
import es.eucm.ead.engine.assets.Assets.AssetLoadedCallback;

public class DraggableGridLayoutTest extends EditorUITest {

	@Override
	protected void builUI(Group root) {

		EditorGameAssets gameAssets = controller.getEditorGameAssets();
		Skin skin = controller.getApplicationAssets().getSkin();
		gameAssets.setLoadingPath("cooldemo", true);

		DraggableGridLayout gridPane = new DraggableGridLayout(4, 4, null);
		for (int i = 1; i < 25; ++i) {
			final Image image = new Image();
			image.setScaling(Scaling.fit);

			if (i < 12) {
				gameAssets.get("images/p1_walk" + (i < 10 ? "0" + i : i)
						+ ".png", Texture.class,
						new AssetLoadedCallback<Texture>() {

							@Override
							public void loaded(String fileName, Texture asset) {
								image.setDrawable(new TextureRegionDrawable(
										new TextureRegion(asset)));
							}

						});
			} else {
				image.setDrawable(skin.getDrawable("newscene48x48"));
			}

			gridPane.add(MathUtils.randomBoolean() ? image : null);
		}

		Container container = new Container(gridPane);
		container.setFillParent(true);
		root.addActor(container);
	}

	public static void main(String[] args) {
		new LwjglApplication(new DraggableGridLayoutTest(),
				"Draggable Grid Layout test", 250, 350);
	}
}
