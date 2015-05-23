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
package es.eucm.ead.editor.view.builders.graph.effects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import es.eucm.commander.Commander;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.utils.ProjectUtils;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.drawables.TextureDrawable;
import es.eucm.ead.editor.view.widgets.Tile;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.engine.assets.Assets;
import es.eucm.ead.schema.effects.GoScene;
import es.eucm.ead.schema.effects.PlaySound;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.entities.ResourceCategory;
import es.eucm.graph.model.Node;

public class GoSceneNodeBuilder extends EffectNodeBuilder<GoScene> implements
		Assets.AssetLoadedCallback<Texture> {

	private TextureDrawable thumbnail;
	private Controller controller;

	public GoSceneNodeBuilder(Commander commander, Controller controller) {
		super(commander, controller.getApplicationAssets().getSkin(),
				controller.getApplicationAssets().getI18N());
		this.controller = controller;
	}

	@Override
	public Drawable getIcon() {
		return skin.getDrawable(SkinConstants.IC_LINK);
	}

	@Override
	public EffectModal<GoScene> buildEditor() {
		return new GoSceneModal(this, controller, commander, skin, i18N);
	}

	@Override
	public Node newNode() {
		Node node = new Node();
		GoScene goScene = new GoScene();
		goScene.setTransition(GoScene.Transition.FADE_IN);
		goScene.setSceneId(null);
		goScene.setDuration(.8f);
		node.setContent(goScene);
		node.addFork("next");
		return node;
	}

	@Override
	public boolean canAdd() {
		return true;
	}

	@Override
	public Actor buildNodeRepresentation(Node node) {

		GoScene goScene = (GoScene) node.getContent();

		Container<Actor> container = new Container<Actor>();
		float spacing = WidgetBuilder.dpToPixels(8);
		container.pad(spacing);
		if (goScene.getSceneId() != null && goScene.getSceneId().isEmpty()) {
			container.setActor(new Label(i18N.m("invalid.effect"), skin));
		} else {
			HorizontalGroup statement = new HorizontalGroup();
			statement.space(spacing);

			statement.addActor(WidgetBuilder.icon(SkinConstants.IC_LINK,
					SkinConstants.STYLE_GRAY));

			String sceneId = goScene.getSceneId();
			String message = "";
			if (sceneId == null) {
				message = i18N.m("go.previous.scene") + ", ";
			} else {
				ModelEntity scene = (ModelEntity) controller.getModel()
						.getResource(sceneId, ResourceCategory.SCENE)
						.getObject();

				Tile tile = new Tile(skin);
				tile.setBackground(new Image(thumbnail = new TextureDrawable()));
				tile.setText(i18N.m("go.to",
						Q.getTitle(scene, i18N.m("untitled"))));
				Q.getThumbnailTexture(scene, this);
				statement.addActor(tile);
			}

			message += i18N.m("speed") + ": "
					+ Q.getSpeedTag(goScene.getDuration())
					+ (sceneId == null ? ", " : "\n") + i18N.m("transition")
					+ ": " + i18N.m(goScene.getTransition().toString());
			statement.addActor(new Label(message, skin));
			container.setActor(statement);
		}
		return container;
	}

	@Override
	public void loaded(String fileName, Texture asset) {
		thumbnail.setTexture(asset);
	}

	@Override
	public void error(String fileName, Class type, Throwable exception) {
	}
}
