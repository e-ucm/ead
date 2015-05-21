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
package es.eucm.ead.editor.view.builders.graph;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.components.behaviors.events.Init;
import es.eucm.graph.core.NodeBuilder;
import es.eucm.graph.model.Node;
import es.eucm.graph.model.Node.Fork;

public class InitNodeBuilder implements NodeBuilder<Init> {

	private Skin skin;

	private I18N i18N;

	public InitNodeBuilder(Skin skin, I18N i18N) {
		this.skin = skin;
		this.i18N = i18N;
	}

	@Override
	public Drawable getIcon() {
		return null;
	}

	@Override
	public Node newNode() {
		Node node = new Node();
		node.setContent(new Init());
		return node;
	}

	@Override
	public void edit(Init content, EditionResult result) {

	}

	@Override
	public boolean canAdd() {
		return false;
	}

	@Override
	public Actor buildNodeRepresentation(Node node) {
		HorizontalGroup group = new HorizontalGroup();
		group.space(WidgetBuilder.dpToPixels(8));
		group.addActor(WidgetBuilder.icon(SkinConstants.IC_SCENE,
				SkinConstants.STYLE_GRAY));
		group.addActor(new Label(i18N.m("event.init"), skin));
		return group;
	}

	@Override
	public Actor buildForkRepresentation(Node node, Fork fork) {
		return new Image(
				skin.getDrawable(SkinConstants.DRAWABLE_TRANSPARENT_48));
	}
}
