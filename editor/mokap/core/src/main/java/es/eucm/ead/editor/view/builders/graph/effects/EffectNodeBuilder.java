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
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import es.eucm.commander.Commander;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.effects.Effect;
import es.eucm.gdx.WidgetUtils;
import es.eucm.graph.core.NodeBuilder;
import es.eucm.graph.model.Node;
import es.eucm.graph.model.Node.Fork;

public abstract class EffectNodeBuilder<T extends Effect> implements
		NodeBuilder<T> {

	protected Commander commander;

	protected Skin skin;

	protected I18N i18N;

	private EffectModal<T> modal;

	public EffectNodeBuilder(Commander commander, Skin skin, I18N i18N) {
		this.commander = commander;
		this.skin = skin;
		this.i18N = i18N;
	}

	@Override
	public void edit(T content, EditionResult editionResult) {
		commander.getCommands().pushStack();
		getModal().setEditionResult(editionResult);
		getModal().read(content);
		WidgetUtils.showModal(null, getModal());
	}

	private EffectModal<T> getModal() {
		if (modal == null) {
			modal = buildEditor();
		}
		return modal;
	}

	public abstract EffectModal<T> buildEditor();

	@Override
	public Actor buildForkRepresentation(Node node, Fork fork) {
		return new Image(
				skin.getDrawable(SkinConstants.DRAWABLE_TRANSPARENT_48));
	}
}
