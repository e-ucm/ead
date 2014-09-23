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
package es.eucm.ead.editor.view.widgets.gallery;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.model.ChangeDocumentation;
import es.eucm.ead.editor.control.actions.model.DeleteScene;
import es.eucm.ead.editor.control.actions.model.EditScene;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.view.builders.gallery.BaseGallery;
import es.eucm.ead.editor.view.listeners.TextFieldListener;
import es.eucm.ead.engine.assets.Assets.AssetLoadedCallback;
import es.eucm.ead.schema.editor.components.Documentation;
import es.eucm.ead.schema.editor.components.GameData;
import es.eucm.ead.schema.editor.components.Thumbnail;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.entities.ResourceCategory;

public class SceneItem extends GalleryItem implements
		AssetLoadedCallback<Texture> {

	private ModelEntity scene;
	private Controller controller;
	private Documentation documentation;
	private Container<Actor> container;

	public SceneItem(final Controller controller, ModelEntity scen,
			BaseGallery gallery) {
		super(new Image(), "", 0f, 0f, true, controller.getApplicationAssets()
				.getSkin(), "scene", true, gallery);
		this.scene = scen;

		this.controller = controller;
		Thumbnail thumbnail = Q.getThumbnail(controller, scene);
		documentation = Q.getComponent(scene, Documentation.class);
		controller.getEditorGameAssets().get(thumbnail.getThumbnail(),
				Texture.class, this);
		TextField nameTf = (TextField) name;
		nameTf.setText(getDocumentationName());
		nameTf.setMessageText(gallery.getI18n().m("name"));
		nameTf.addListener(new TextFieldListener(nameTf) {

			@Override
			protected void keyTyped(String text) {
				controller.action(ChangeDocumentation.class, documentation,
						true, text);
			}
		});
		nameTf.addListener(new FocusListener() {
			@Override
			public void keyboardFocusChanged(FocusEvent event, Actor actor,
					boolean focused) {
				if (focused) {
					String sceneId = controller.getModel().getIdFor(scene);
					if (sceneId != null) {
						controller.action(EditScene.class, sceneId);
					}
				}
			}
		});
		container = new Container<Actor>(new Image(skin.getDrawable("first")));
		container.setFillParent(true);
		container.setTouchable(Touchable.disabled);
		container.top().left();
		checkInitial();
	}

	public void checkInitial() {
		Model model = controller.getModel();
		String sceneId = model.getIdFor(scene);
		if (sceneId != null) {
			ModelEntity game = model.getGame();
			String initialScene = Q.getComponent(game, GameData.class)
					.getInitialScene();
			if (!container.hasParent() && initialScene != null
					&& sceneId.equals(initialScene)) {
				addActor(container);
				model.getResource(model.getIdFor(game), ResourceCategory.GAME)
						.setModified(true);
			} else if (container.hasParent()) {
				container.remove();
			}
		}
	}

	@Override
	public void deleteItem() {
		String sceneId = controller.getModel().getIdFor(scene);
		if (sceneId != null) {
			controller.action(DeleteScene.class, sceneId, scene);
		}
	}

	public ModelEntity getScene() {
		return scene;
	}

	@Override
	public String getName() {
		return getDocumentationName();
	}

	private String getDocumentationName() {
		if (documentation == null) {
			return "";
		}
		String name = documentation.getName();
		return name == null ? "" : name;
	}

	@Override
	public void loaded(String fileName, Texture asset) {
		setThumbnail(asset);
	}
}
