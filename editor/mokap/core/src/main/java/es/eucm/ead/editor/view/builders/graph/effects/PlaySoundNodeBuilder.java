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

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import es.eucm.commander.Commander;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.utils.ProjectUtils;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.schema.effects.PlaySound;
import es.eucm.graph.model.Node;

public class PlaySoundNodeBuilder extends EffectNodeBuilder<PlaySound> {

	private Controller controller;

	public PlaySoundNodeBuilder(Commander commander, Controller controller) {
		super(commander, controller.getApplicationAssets().getSkin(),
				controller.getApplicationAssets().getI18N());
		this.controller = controller;
	}

	@Override
	public Drawable getIcon() {
		return skin.getDrawable(SkinConstants.IC_SOUND);
	}

	@Override
	public EffectModal<PlaySound> buildEditor() {
		return new PlaySoundModal(this, controller, commander, skin, i18N);
	}

	@Override
	public Node newNode() {
		Node node = new Node();
		PlaySound sound = new PlaySound();
		node.setContent(sound);
		node.addFork("next");
		return node;
	}

	@Override
	public boolean canAdd() {
		return true;
	}

	@Override
	public Actor buildNodeRepresentation(Node node) {

		PlaySound changeVar = (PlaySound) node.getContent();

		Container<Actor> container = new Container<Actor>();
		float spacing = WidgetBuilder.dpToPixels(8);
		container.pad(spacing);
		if (changeVar.getUri() == null || changeVar.getUri().isEmpty()) {
			container.setActor(new Label(i18N.m("invalid.effect"), skin));
		} else {
			HorizontalGroup statement = new HorizontalGroup();
			statement.space(spacing);

			statement.addActor(WidgetBuilder.icon(SkinConstants.IC_SOUND,
					SkinConstants.STYLE_GRAY));
			statement.addActor(new Label(ProjectUtils.getFileName(changeVar
					.getUri()), skin));
			statement.space(spacing);

			statement.addActor(WidgetBuilder.icon(SkinConstants.IC_VOLUME,
					SkinConstants.STYLE_GRAY));
			statement.addActor(new Label((int) (changeVar.getVolume() * 100)
					+ "%", skin));

			container.setActor(statement);
		}
		return container;
	}
}
