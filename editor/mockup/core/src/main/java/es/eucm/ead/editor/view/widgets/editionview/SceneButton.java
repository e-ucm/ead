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
package es.eucm.ead.editor.view.widgets.editionview;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.engine.assets.Assets.AssetLoadedCallback;
import es.eucm.ead.schema.editor.components.Documentation;
import es.eucm.ead.schema.editor.components.Thumbnail;
import es.eucm.ead.schema.entities.ModelEntity;

public class SceneButton extends Button implements AssetLoadedCallback<Texture> {

	private TextureRegionDrawable drawable;
	private TextureRegion region;
	private Label label;

	private ModelEntity scene;

	private Controller controller;
	private Image image;

	public SceneButton(Controller controller, Skin skin, String style) {
		this(null, controller, 0, skin, style);

	}

	public SceneButton(Controller controller, float pad, Skin skin, String style) {
		this(null, controller, pad, skin, style);

	}

	public SceneButton(ModelEntity scene, Controller controller, Skin skin,
			String style) {
		this(scene, controller, 0, skin, style);

	}

	public SceneButton(Controller controller, Skin skin) {
		this(null, controller, 0, skin, "default");

	}

	public SceneButton(Controller controller, float pad, Skin skin) {
		this(null, controller, pad, skin, "default");

	}

	public SceneButton(ModelEntity scene, Controller controller, Skin skin) {
		this(scene, controller, 0, skin, "default");

	}

	public SceneButton(ModelEntity scene, Controller controller, float pad,
			Skin skin) {
		this(scene, controller, 0, skin, "default");

	}

	public SceneButton(ModelEntity scene, Controller controller, float pad,
			Skin skin, String style) {
		super(skin, style);

		this.controller = controller;
		this.scene = scene;

		image = new Image();
		drawable = new TextureRegionDrawable(region = new TextureRegion());
		image.setScaling(Scaling.fit);

		add(image).pad(pad, pad, pad / 2, pad).fill();
		row();

		label = new Label(" ", skin);
		add(label).expand().center().pad(0, pad, pad, pad);
	}

	@Override
	public void loaded(String fileName, Texture asset) {
		region.setRegion(asset);
		drawable.setRegion(region);
		image.setDrawable(drawable);
		image.invalidateHierarchy();
	}

	public ModelEntity getScene() {
		return scene;
	}

	public void updateScene() {
		Documentation documentation = Q
				.getComponent(scene, Documentation.class);
		if (documentation != null && documentation.getName() != null) {
			label.setText(documentation.getName());
		} else {
			label.setText(" ");
		}

		Thumbnail thumbnail = Q.getThumbnail(controller, scene);
		controller.getEditorGameAssets().get(thumbnail.getThumbnail(),
				Texture.class, this);
	}

	public void actualizeName() {
		Documentation documentation = Q
				.getComponent(scene, Documentation.class);
		if (documentation != null && documentation.getName() != null) {
			label.setText(documentation.getName());
		} else {
			label.setText(" ");
		}
	}
}
