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
package es.eucm.ead.editor.view.builders.mockup.edition;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.widgets.mockup.ToolBar;
import es.eucm.ead.editor.view.widgets.mockup.edition.AddElementComponent;
import es.eucm.ead.editor.view.widgets.mockup.edition.AddInteractionComponent;
import es.eucm.ead.editor.view.widgets.mockup.edition.EditionComponent;
import es.eucm.ead.editor.view.widgets.mockup.edition.TextComponent;
import es.eucm.ead.schema.actors.Scene;

/**
 * A view that allows the user to edit {@link Scene}s.
 */
public class SceneEdition extends EditionWindow {

	public static final String NAME = "mockup_scene_edition";
	private ToolBar topToolbar;
	private Container wrapper;

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public Actor build(Controller controller) {
		Actor ac = super.build(controller);
		/*this.wrapper.toFront();
		this.topToolbar.toFront();
		Array<Actor> ch = this.topToolbar.getChildren();
		for (Actor i : ch) {
			i.toFront();
		}*/
		return ac;
	}

	@Override
	protected void editionComponents(Array<EditionComponent> editionComponents,
			Vector2 viewport, Controller controller, Skin skin) {

		editionComponents.add(new TextComponent(this, controller, skin));
		editionComponents.add(new AddInteractionComponent(this, controller,
				skin));

		AddElementComponent comp = new AddElementComponent(this, controller,
				skin);
		editionComponents.add(comp);

		this.topToolbar = comp.getToolbar();
		this.wrapper = new Container(this.topToolbar).fillX().top();
		this.wrapper.setFillParent(true);

		this.getRoot().addActor(this.wrapper);

	}
}
