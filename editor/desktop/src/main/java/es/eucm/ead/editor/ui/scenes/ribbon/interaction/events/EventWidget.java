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
import es.eucm.ead.editor.control.actions.model.SetField;
import es.eucm.ead.editor.model.Model.FieldListener;
import es.eucm.ead.editor.model.events.FieldEvent;
import es.eucm.ead.editor.view.controllers.OptionsController.ChangeListener;
import es.eucm.ead.editor.view.controllers.ReflectionOptionsController;
import es.eucm.ead.editor.view.controllers.options.OptionController;
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

	private ReflectionOptionsController<T> optionsController;

	private EventFieldListener eventFieldListener = new EventFieldListener();

	private T editedEvent;

	public EventWidget(Controller controller, Class<T> eventClass) {
		super(false);
		this.controller = controller;

		skin = controller.getApplicationAssets().getSkin();
		i18N = controller.getApplicationAssets().getI18N();

		LinearLayout row = new LinearLayout(true);
		Image image = new Image(skin.getDrawable(getEventIcon()));
		row.add(image);
		add(row);
		optionsController = new ReflectionOptionsController<T>(controller,
				skin, eventClass);
		optionsController.addChangeListener(new EventChangeListener());
		add(optionsController.getPanel());
	}

	public void readEvent(T event) {
		this.editedEvent = event;
		controller.getModel().removeListenerFromAllTargets(eventFieldListener);
		optionsController.read(event);
		controller.getModel().addFieldListener(event, eventFieldListener);
	}

	public abstract String getEventIcon();

	private class EventFieldListener implements FieldListener {

		@Override
		public boolean listenToField(String fieldName) {
			return true;
		}

		@Override
		public void modelChanged(FieldEvent event) {
			optionsController.setValue(event.getField(), event.getValue());
		}
	}

	private class EventChangeListener implements ChangeListener {

		@Override
		public void valueUpdated(OptionController source, String field,
				Object value) {
			controller.action(SetField.class, editedEvent, field, value);
		}
	}
}
