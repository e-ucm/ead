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
package es.eucm.ead.editor.view.widgets.mockup.edition;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.model.AddChildToEntity;
import es.eucm.ead.editor.control.actions.model.Rename;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.events.FieldEvent;
import es.eucm.ead.editor.view.builders.mockup.edition.EditionWindow;
import es.eucm.ead.editor.view.listeners.ActionForTextFieldListener;
import es.eucm.ead.editor.view.listeners.ChangeNoteFieldListener;
import es.eucm.ead.editor.view.widgets.mockup.buttons.BottomProjectMenuButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.MenuButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.MenuButton.Position;
import es.eucm.ead.editor.view.widgets.mockup.buttons.ToolbarButton;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.editor.components.Note;
import es.eucm.ead.schema.editor.components.Parent;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.FieldNames;

public abstract class MoreComponent extends EditionComponent {

	private static final String IC_MORE = "ic_more";

	private static String IC_CLONE;

	protected static final float PREF_BOTTOM_BUTTON_WIDTH = .30F;
	protected static final float PREF_BOTTOM_BUTTON_HEIGHT = .18F;
	private static final int MAX_TITLE_CARACTERS = 20;
	private static final int MAX_DESCRIPTION_CARACTERS = 200;

	private final TextField name;
	private final TextArea description;
	private ChangeNoteFieldListener noteListener;

	private Note note;

	public MoreComponent(EditionWindow parent, final Controller controller,
			Skin skin) {
		super(parent, controller, skin);

		final I18N i18n = controller.getApplicationAssets().getI18N();
		String type = null;
		if (this instanceof MoreElementComponent) {
			type = i18n.m("element");
			IC_CLONE = "ic_duplicate_element";
		} else {
			type = i18n.m("scene");
			IC_CLONE = "ic_duplicate_scene";
		}

		this.name = new TextField("", skin);
		this.name.setMaxLength(MAX_TITLE_CARACTERS);
		final Class<?> actionClass = getNoteActionClass();
		if (actionClass != null) {
			this.name.setTextFieldListener(new ActionForTextFieldListener(
					controller, actionClass, this, FieldNames.NOTE_TITLE));
		}
		final String untitled = type + " " + i18n.m("untitled");
		this.name.setMessageText(untitled);

		this.description = new TextArea("", skin);
		this.description.setMaxLength(MAX_DESCRIPTION_CARACTERS);
		if (actionClass != null) {
			this.description
					.setTextFieldListener(new ActionForTextFieldListener(
							controller, actionClass, this,
							FieldNames.NOTE_DESCRIPTION));
		}
		final String emptyDescription = type + " " + i18n.m("emptydescription");
		this.description.setMessageText(emptyDescription);

		final MenuButton cloneButton = new BottomProjectMenuButton(viewport,
				super.i18n.m("general.clone"), skin, IC_CLONE,
				PREF_BOTTOM_BUTTON_WIDTH, PREF_BOTTOM_BUTTON_HEIGHT,
				Position.RIGHT);
		cloneButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Array<Object> sel = controller.getModel().getSelection();
				if (sel.size > 0) {
					Object first = sel.get(0);
					if (first instanceof ModelEntity) {
						ModelEntity entity = (ModelEntity) first;
						controller
								.action(AddChildToEntity.class,
										controller.getEditorGameAssets().copy(
												entity),
										Model.getComponent(entity, Parent.class)
												.getParent());

						// TODO show a notification to the user indicating that
						// the entity was successfully added
					}
				}
			}
		});

		this.add(this.name).fillX().expandX();
		this.row();
		this.add(this.description).fill().expand().center().height(300f);
		this.row();
		this.add(cloneButton);
	}

	@Override
	protected Button createButton(Vector2 viewport, Controller controller) {
		return new ToolbarButton(viewport, skin.getDrawable(IC_MORE),
				i18n.m("edition.more"), skin);
	}

	/**
	 * @return the {@link Rename} that will be performed when the name has
	 *         changed.
	 */
	protected abstract Class<?> getNoteActionClass();

	/**
	 * @return the {@link Note} linked to the current editing scene or editing
	 *         element.
	 */
	public abstract Note getNote(Model model);

	/**
	 * Updates the displayed title/description of the current editing scene;
	 * 
	 */
	public void initialize(Controller controller) {
		final Model model = controller.getModel();
		if (this.noteListener != null && note != null) {
			model.removeListener(note, this.noteListener);
		}
		Note prevNote = this.note;
		this.note = getNote(model);
		if (this.note == null || prevNote == this.note)
			return;
		this.name.setText(note.getTitle() == null ? "" : note.getTitle());
		this.description.setText(note.getDescription() == null ? "" : note
				.getDescription());
		model.addFieldListener(note,
				this.noteListener = new ChangeNoteFieldListener() {
					@Override
					public void titleChanged(FieldEvent event) {
						final Object newValue = event.getValue();
						String text = null;
						if (newValue == null) {
							text = "";
						} else {
							text = newValue.toString();
						}
						MoreComponent.this.name.setText(text);
					}

					@Override
					public void descriptionChanged(FieldEvent event) {
						final Object newValue = event.getValue();
						String text = null;
						if (newValue == null) {
							text = "";
						} else {
							text = newValue.toString();
						}
						MoreComponent.this.description.setText(text);
					}
				});

	}

	/**
	 * Does the necessary stuff to release this component's resources.
	 * 
	 * @param controller
	 */
	public void release(Controller controller) {

	}
}
