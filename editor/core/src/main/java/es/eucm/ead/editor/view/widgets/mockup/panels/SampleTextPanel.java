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
package es.eucm.ead.editor.view.widgets.mockup.panels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.model.AddChildToEntity;
import es.eucm.ead.editor.control.actions.model.ReplaceEntity;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.events.SelectionEvent;
import es.eucm.ead.schema.components.controls.Label;
import es.eucm.ead.schema.entities.ModelEntity;

public class SampleTextPanel extends SamplePanel {

	private boolean textSelected;

	private Controller controller;

	public SampleTextPanel(final Controller controller, Skin skin, int cols,
			boolean text, boolean colors) {
		super(controller, skin, cols, text, colors);
		this.controller = controller;
		this.textSelected = false;

		controller.getModel().addSelectionListener(
				new ModelListener<SelectionEvent>() {

					@Override
					public void modelChanged(SelectionEvent event) {
						Array<Object> sel = controller.getModel()
								.getSelection();
						textSelected = false;
						if (sel.size == 1) {
							String text = null;
							if (sel.first() instanceof ModelEntity
									&& Model.hasComponent(
											(ModelEntity) sel.first(),
											es.eucm.ead.schema.components.controls.Label.class)) {
								textSelected = true;
								text = Model
										.getComponent(
												(ModelEntity) sel.first(),
												es.eucm.ead.schema.components.controls.Label.class)
										.getText();
							}
							if (text == null) {
								setTextFieldText("");
							} else {
								setTextFieldText(text);
							}
						} else {
							setTextFieldText("");
						}
					}

				});
	}

	@Override
	protected TextFieldListener setListenerToTextField() {
		return new TextFieldListener() {

			@Override
			public void keyTyped(TextField textField, char c) {
				if (c == '\n' || c == '\r') {
					textField.getStage().unfocusAll();
					Gdx.input.setOnscreenKeyboardVisible(false);

					if (textField.getText().isEmpty())
						return;

					if (!textSelected) {
						Object context = controller.getModel()
								.getEditionContext();
						if (context instanceof ModelEntity) {
							ModelEntity text = new ModelEntity();
							Label label = new Label();
							label.setText(textField.getText());
							// TODO Change the style
							label.setStyle("welcome");
							text.getComponents().add(label);
							controller.action(AddChildToEntity.class, text,
									context);
						}
					} else {
						Object sel = controller.getModel().getSelection()
								.first();
						if (sel instanceof ModelEntity) {
							ModelEntity element = (ModelEntity) sel;
							ModelEntity copy = controller.getEditorGameAssets()
									.copy(element);

							Label labelComponent = Model.getComponent(copy,
									Label.class);
							labelComponent.setText(textField.getText());
							controller.action(ReplaceEntity.class, element,
									copy);
						}
					}
				}
			}
		};
	}
}
