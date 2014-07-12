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
package es.eucm.ead.editor.ui.scenes.ribbon.interaction.events;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.controllers.ClassOptionsController;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.components.behaviors.Event;

/**
 * This widget holds the {@link Event} edition inside a
 * {@link es.eucm.ead.schema.components.behaviors.Behavior}.
 */
public abstract class EventWidget<T extends Event> extends LinearLayout {

	protected Controller controller;

	protected Skin skin;

	protected I18N i18N;

	private ClassOptionsController<T> optionsController;

	public EventWidget(Controller controller, Class<T> eventClass) {
		super(false);
		this.controller = controller;

		skin = controller.getApplicationAssets().getSkin();
		i18N = controller.getApplicationAssets().getI18N();

		LinearLayout row = new LinearLayout(true);
		Image image = new Image(skin.getDrawable(getEventIcon()));
		row.add(image);
		add(row);
		optionsController = new ClassOptionsController<T>(controller, skin,
				eventClass, null);
		add(optionsController.getPanel());
	}

	public void readEvent(T event) {
		optionsController.read(event);
	}

	public abstract String getEventIcon();

}
