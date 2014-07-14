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
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.editor.ChangeView;
import es.eucm.ead.editor.control.actions.model.SetSelection;
import es.eucm.ead.editor.control.actions.model.scene.ReorderSelection;
import es.eucm.ead.editor.control.actions.model.scene.ReorderSelection.Type;
import es.eucm.ead.editor.view.builders.mockup.edition.EditionWindow;
import es.eucm.ead.editor.view.builders.mockup.edition.ElementEdition;
import es.eucm.ead.editor.view.widgets.mockup.buttons.BottomProjectMenuButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.MenuButton.Position;
import es.eucm.ead.editor.view.widgets.mockup.buttons.ToolbarButton;
import es.eucm.ead.engine.I18N;

/**
 * A table that can be dragged and has the next options: Delete selected
 * elements Move to back or front the selected elements Go to edit element
 */
public class ElementSelectedComponent extends EditionComponent {

	private static final String IC_BACK = "ic_layer_toback",
			IC_FRONT = "ic_layer_tofront", IC_EDIT = "ic_editelement",
			IC_REMOVE = "ic_delete", IC_OPTIONS = "ic_settings",
			IC_DESELECT = "ic_deselect";

	private static final float PREF_BOTTOM_BUTTON_WIDTH = .35F;
	private static final float PREF_BOTTOM_BUTTON_HEIGHT = .145F;

	private static final float REMOVE_PAD_TOP = 16f;

	private Button toBack;

	private Button toFront;

	private Button edit;

	private Button delete;

	private Button deselect;

	public ElementSelectedComponent(EditionWindow parent,
			final Controller controller, Skin skin) {
		super(parent, controller, skin);

		I18N i18n = controller.getApplicationAssets().getI18N();
		final Vector2 viewport = controller.getPlatform().getSize();

		this.toBack = new BottomProjectMenuButton(viewport,
				i18n.m("general.edition.to-back"), skin, IC_BACK,
				PREF_BOTTOM_BUTTON_WIDTH, PREF_BOTTOM_BUTTON_HEIGHT,
				Position.RIGHT);
		this.toBack.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				controller.action(ReorderSelection.class, Type.TO_BACK);
			}
		});

		this.toFront = new BottomProjectMenuButton(viewport,
				i18n.m("general.edition.to-front"), skin, IC_FRONT,
				PREF_BOTTOM_BUTTON_WIDTH, PREF_BOTTOM_BUTTON_HEIGHT,
				Position.RIGHT);
		this.toFront.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				controller.action(ReorderSelection.class, Type.TO_FRONT);
			}
		});

		this.edit = new BottomProjectMenuButton(viewport,
				i18n.m("general.edit"), skin, IC_EDIT,
				PREF_BOTTOM_BUTTON_WIDTH, PREF_BOTTOM_BUTTON_HEIGHT,
				Position.RIGHT);
		this.edit.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {

				Object[] selection = controller.getModel().getSelection()
						.getCurrent();
				if (selection.length > 0) {
					Object aux = selection[0];
					controller.action(SetSelection.class, aux);
					controller.action(ChangeView.class, ElementEdition.class);
				}
			}
		});

		this.delete = new BottomProjectMenuButton(viewport,
				i18n.m("general.delete"), skin, IC_REMOVE,
				PREF_BOTTOM_BUTTON_WIDTH, PREF_BOTTOM_BUTTON_HEIGHT,
				Position.RIGHT);
		this.delete.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Object[] selection = controller.getModel().getSelection()
						.getCurrent();
				if (selection.length > 0) {
					/*
					 * controller.action(RemoveChildrenFromEntity.class,
					 * controller.getModel().getEditScene(), selection);
					 */
				}
			}
		});

		this.deselect = new BottomProjectMenuButton(viewport,
				i18n.m("general.gallery.deselect"), skin, IC_DESELECT,
				PREF_BOTTOM_BUTTON_WIDTH, PREF_BOTTOM_BUTTON_HEIGHT,
				Position.RIGHT);
		this.deselect.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Object[] selection = controller.getModel().getSelection()
						.getCurrent();
				if (selection.length > 0) {
					controller.action(SetSelection.class);
				}
			}
		});

		this.setVisible(false);
		this.delete.setDisabled(true);
		this.edit.setDisabled(true);
		this.toBack.setDisabled(true);
		this.toFront.setDisabled(true);
		this.deselect.setDisabled(true);

		/*
		 * controller.getModel().addSelectionListener( new
		 * ModelListener<SelectionEvent>() {
		 * 
		 * @Override public void modelChanged(SelectionEvent event) { int size =
		 * controller.getModel().getSelection().size; delete.setDisabled(size ==
		 * 0); edit.setDisabled(size != 1); toBack.setDisabled(size == 0);
		 * toFront.setDisabled(size == 0); deselect.setDisabled(size == 0); }
		 * });
		 */

		this.add(edit);
		this.row();
		this.add(toBack);
		this.row();
		this.add(toFront);
		this.row();
		this.add(deselect);
		this.row();
		this.add(delete).padTop(REMOVE_PAD_TOP);
		this.pack();
	}

	@Override
	protected Button createButton(Vector2 viewport, Controller controller) {
		// FIXME Provisional name and icon button.
		return new ToolbarButton(viewport, skin.getDrawable(IC_OPTIONS),
				i18n.m("general.options"), skin);
	}
}
