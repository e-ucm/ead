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
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.editor.ChangeView;
import es.eucm.ead.editor.control.actions.model.Reorder;
import es.eucm.ead.editor.control.actions.model.SetSelection;
import es.eucm.ead.editor.control.actions.model.scene.RemoveChildrenFromEntity;
import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.events.SelectionEvent;
import es.eucm.ead.editor.view.builders.mockup.edition.ElementEdition;
import es.eucm.ead.editor.view.widgets.mockup.buttons.BottomProjectMenuButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.MenuButton.Position;
import es.eucm.ead.engine.I18N;

/**
 * A table that can be dragged and has the next options: Delete selected
 * elements Move to back or front the selected elements Go to edit element
 */
public class ElementSelectedWidget extends Window {

	private static final String IC_BACK = "ic_layer_toback",
			IC_FRONT = "ic_layer_tofront", IC_EDIT = "ic_pencil",
			IC_REMOVE = "ic_close_trash";

	private static final float PREF_BOTTOM_BUTTON_WIDTH = .01F;
	private static final float PREF_BOTTOM_BUTTON_HEIGHT = .12F;
	private static final float REMOVE_PAD_TOP = 16f;

	public ElementSelectedWidget(Skin skin, final Controller controller) {
		super("", skin);

		final I18N i18n = controller.getApplicationAssets().getI18N();
		final Vector2 viewport = controller.getPlatform().getSize();

		this.setTitle(i18n.m("general.options"));

		final Button toBack = new BottomProjectMenuButton(viewport,
				i18n.m("general.edition.to-back"), skin, IC_BACK,
				PREF_BOTTOM_BUTTON_WIDTH, PREF_BOTTOM_BUTTON_HEIGHT,
				Position.RIGHT);
		toBack.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {

				Array<Object> selection = controller.getModel().getSelection();
				if (selection.size > 0) {
					// Needed to move the elements in order
					Array<Integer> positionAux = new Array<Integer>();
					for (Object element : selection) {
						positionAux.add(controller.getModel().getEditScene()
								.getChildren().indexOf(element));
					}

					// Ordered list by actual Z
					ordenedLists(selection, positionAux, true);
					for (Object element : selection) {
						controller.action(Reorder.class, element, -1, true,
								controller.getModel().getEditScene()
										.getChildren());
					}
				}
			}
		});

		final Button toFront = new BottomProjectMenuButton(viewport,
				i18n.m("general.edition.to-front"), skin, IC_FRONT,
				PREF_BOTTOM_BUTTON_WIDTH, PREF_BOTTOM_BUTTON_HEIGHT,
				Position.RIGHT);
		toFront.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {

				Array<Object> selection = controller.getModel().getSelection();
				if (selection.size > 0) {
					// Needed to move the elements in order
					Array<Integer> positionAux = new Array<Integer>();
					for (Object element : selection) {
						positionAux.add(controller.getModel().getEditScene()
								.getChildren().indexOf(element));
					}

					// Ordered list by actual Z
					ordenedLists(selection, positionAux, false);
					for (Object element : selection) {
						controller.action(Reorder.class, element, +1, true,
								controller.getModel().getEditScene()
										.getChildren());
					}
				}
			}
		});

		final Button edit = new BottomProjectMenuButton(viewport,
				i18n.m("general.edit"), skin, IC_EDIT,
				PREF_BOTTOM_BUTTON_WIDTH, PREF_BOTTOM_BUTTON_HEIGHT,
				Position.RIGHT);
		edit.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {

				Array<Object> selection = controller.getModel().getSelection();
				if (selection.size > 0) {
					Object aux = selection.first();
					selection.clear();
					selection.add(aux);
					// TODO that appear only the selected element to edit it
					// Go to element edition
					controller.action(ChangeView.class, ElementEdition.class);
				}
			}
		});

		final Button delete = new BottomProjectMenuButton(viewport,
				i18n.m("general.delete"), skin, IC_REMOVE,
				PREF_BOTTOM_BUTTON_WIDTH, PREF_BOTTOM_BUTTON_HEIGHT,
				Position.RIGHT);
		delete.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Array<Object> selection = controller.getModel().getSelection();
				if (selection.size > 0) {
					controller.action(RemoveChildrenFromEntity.class,
							controller.getModel().getEditScene(), selection);
					selection.clear();
					// Assure that not left selection rectangle
					controller.action(SetSelection.class, new Array());
				}
			}
		});

		delete.setDisabled(true);
		edit.setDisabled(true);
		toBack.setDisabled(true);
		toFront.setDisabled(true);

		controller.getModel().addSelectionListener(
				new ModelListener<SelectionEvent>() {
					@Override
					public void modelChanged(SelectionEvent event) {
						if (controller.getModel().getSelection().size == 0) {
							delete.setDisabled(true);
							edit.setDisabled(true);
							toBack.setDisabled(true);
							toFront.setDisabled(true);
						} else if (controller.getModel().getSelection().size == 1) {
							delete.setDisabled(false);
							edit.setDisabled(true);
							toBack.setDisabled(false);
							toFront.setDisabled(false);
						} else {
							delete.setDisabled(false);
							edit.setDisabled(false);
							toBack.setDisabled(false);
							toFront.setDisabled(false);
						}
					}
				});

		this.add(toBack);
		this.row();
		this.add(toFront);
		this.row();
		this.add(edit);
		this.row();
		this.add(delete).padTop(REMOVE_PAD_TOP);
		this.pack();
	}

	/**
	 * Orders two list by values of main list, the order can be decreasing and
	 * incremental.
	 * 
	 * @param slave
	 * @param main
	 * @param increment
	 */
	private void ordenedLists(Array<Object> slave, Array<Integer> main,
			boolean increment) {
		if (increment) {
			ordenedListsIncrement(slave, main, 0, slave.size - 1);
		} else {
			ordenedListsDecrement(slave, main, 0, slave.size - 1);
		}
	}

	private void ordenedListsIncrement(Array<Object> slave,
			Array<Integer> main, int first, int last) {
		int i = first, j = last;
		int pivot = main.get((first + last) / 2);
		do {
			while (main.get(i) < pivot)
				i++;
			while (main.get(j) > pivot)
				j--;
			if (i <= j) {
				main.swap(i, j);
				slave.swap(i, j);
				i++;
				j--;
			}
		} while (i <= j);
		if (first < j) {
			ordenedListsIncrement(slave, main, first, j);
		}
		if (last > i) {
			ordenedListsIncrement(slave, main, i, last);
		}
	}

	private void ordenedListsDecrement(Array<Object> slave,
			Array<Integer> main, int first, int last) {
		int i = first, j = last;
		int pivot = main.get((first + last) / 2);
		do {
			while (main.get(i) > pivot)
				i++;
			while (main.get(j) < pivot)
				j--;
			if (i <= j) {
				main.swap(i, j);
				slave.swap(i, j);
				i++;
				j--;
			}
		} while (i <= j);
		if (first < j) {
			ordenedListsDecrement(slave, main, first, j);
		}
		if (last > i) {
			ordenedListsDecrement(slave, main, i, last);
		}
	}
}
