/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2013 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          C Profesor Jose Garcia Santesmases sn,
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

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.widgets.mockup.ToolBar;
import es.eucm.ead.editor.view.widgets.mockup.buttons.ToolbarButton;
import es.eucm.ead.engine.I18N;

/**
 * A GridPanel that has multiple selection functionality after long press. Its
 * elements must implement {@link SelectListener} in order to be able to select
 * them otherwise nothing would happen after a LongPress event.
 */
public class GalleryGrid<T extends Actor> extends GridPanel<T> {

	private static final float DEFAULT_DIALOG_PADDING_LEFT_RIGHT = 20f;

	/**
	 * A collection storing the entities we've selected.
	 */
	protected Array<SelectListener> selectedEntities;

	/**
	 * If it's true we're in "selection mode"
	 */
	private boolean selecting;

	/**
	 * Auxiliary attribute that automatically hides it's contents when
	 * necessary.
	 */
	private Array<Actor> actorsToHide;

	/**
	 * The top tool bar that will be shown when we're in selection "mode".
	 */
	private ToolBar topToolbar;

	/**
	 * Just a label showing how many entities we've selected in the topToolBar.
	 */
	private Label numSelectedEntities;

	/**
	 * The button that will allow us to delete our selected entities.
	 */
	private Button deleteButton;
	
	private I18N i18n;
	
	private static final String IC_GO_BACK = "ic_goback",
			IC_DELETE = "ic_delete";

	public GalleryGrid(Skin skin, int rows, int cols, Vector2 point,
			WidgetGroup root, Controller controller, Actor... actorsToHide) {
		super(skin, rows, cols, 20f); // Change pad
		if (actorsToHide == null) {
			throw new IllegalArgumentException("actorsToHide can't be null.");
		}
		this.i18n = controller.getEditorAssets().getI18N();
		this.actorsToHide = new Array<Actor>(false, 2);
		defaults().expand().fill().uniform();
		selectedEntities = new Array<SelectListener>();
		selecting = false;
		addListener(new ActorGestureListener() {

			/**
			 * Auxiliary attribute used to know if the target of our event it's
			 * indeed instance of GalleryEntity (which implements
			 * SelectListener)
			 */
			private SelectListener target;

			@Override
			public void touchDown(InputEvent event, float x, float y,
					int pointer, int button) {

				Actor targ = event.getTarget();
				while (targ != null && !(targ instanceof SelectListener)) {
					targ = targ.getParent();
				}
				if (targ == null)
					return;
				prepareTouchDown(targ);
				super.touchDown(event, x, y, pointer, button);
			}

			private void prepareTouchDown(Actor target) {
				this.target = (SelectListener) target;
				if (selecting) {
					if (this.target.isSelected()) {
						this.target.deselect();
						removeSelectedEntry(this.target);
					} else {
						this.target.select();
						addSelectedEntry(this.target);
					}
				}
			}

			@Override
			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				if (selecting)
					return;

				GalleryGrid.this.entityClicked(event);

			}

			@Override
			public boolean longPress(Actor actor, float x, float y) {
				if (selecting)
					return true;
				if (target instanceof SelectListener) {
					startSelecting();
				}
				return true;
			}

			private void addSelectedEntry(SelectListener entity) {
				if (selectedEntities.size == 0) {
					deleteButton.setVisible(true);
				}
				selectedEntities.add(entity);
				numSelectedEntities.setText(String
						.valueOf(selectedEntities.size));
			}

			private void removeSelectedEntry(SelectListener entity) {
				selectedEntities.removeValue(entity, true);
				int entitiesCount = selectedEntities.size;
				numSelectedEntities.setText(String.valueOf(entitiesCount));
				if (entitiesCount == 0) {
					deleteButton.setVisible(false);
				}
			}

			private void startSelecting() {
				selecting = true;
				target.select();
				addSelectedEntry(target);
				changeActorsVisibility(false);
			}
		});

		initializeTopToolBar(skin, point, root);
	}

	private void initializeTopToolBar(Skin skin, Vector2 viewport,
			WidgetGroup root) {
		final Dialog confirmDialog = new Dialog(i18n.m("general.gallery.delete-resources"), skin,
				"exit-dialog") {
			protected void result(Object object) {
				onHide();
			}
		}.button(i18n.m("general.cancel"), false).button(i18n.m("general.accept"), true)
				.key(Keys.BACK, false).key(Keys.ENTER, true);
		confirmDialog.padLeft(DEFAULT_DIALOG_PADDING_LEFT_RIGHT);
		confirmDialog.padRight(DEFAULT_DIALOG_PADDING_LEFT_RIGHT);

		confirmDialog.setMovable(false);
		topToolbar = new ToolBar(viewport, skin);
		topToolbar.setVisible(false);

		deleteButton = new ToolbarButton(viewport, IC_DELETE, i18n.m("general.delete"), skin);
		final Button backButton = new ToolbarButton(viewport, IC_GO_BACK, i18n.m("general.gallery.deselect"), skin);
		backButton.padLeft(20); //Necessary for show the text 'Deselect' complete in spanish
		ClickListener mListener = new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Actor target = event.getListenerActor();
				if (target == deleteButton) {
					confirmDialog.getContentTable().clearChildren();
					String message = i18n.m(selectedEntities.size, "general.gallery.delete-singular","general.gallery.delete-plural", selectedEntities.size);
					confirmDialog.text(message).show(getStage());
				} else if (target == backButton) {
					onHide();
				}
			}
		};
		deleteButton.addListener(mListener);
		backButton.addListener(mListener);

		numSelectedEntities = new Label("", skin);
		topToolbar.add(backButton);
		topToolbar.add(numSelectedEntities).left().expandX();
		topToolbar.add(deleteButton);
		Container wrapper = new Container(topToolbar).fillX().top();
		wrapper.setFillParent(true);
		root.addActor(wrapper);
	}

	private void changeActorsVisibility(boolean visible) {
		int i = 0, length = actorsToHide.size;
		for (; i < length; ++i) {
			Actor actorToHide = actorsToHide.get(i);
			if (actorToHide != null) {
				actorToHide.setVisible(visible);
			}
		}
		topToolbar.setVisible(!visible);
	}

	/**
	 * Called when this Actor was clicked if we're not in Selection Mode.
	 * Convenience method that should be overridden if needed.
	 */
	protected void entityClicked(InputEvent event) {
	}

	/**
	 * Resets previous visibility changes to actors.
	 */
	public void onHide() {
		changeActorsVisibility(true);
		for (SelectListener select : selectedEntities) {
			select.deselect();
		}
		selectedEntities.clear();
		selecting = false;
	}

	/**
	 * True if we're in "selection mode"
	 */
	public boolean isSelecting() {
		return selecting;
	}

	/**
	 * Adds an actor that will be hidden when we enter Selection Mode and will
	 * be visible again after we exit Selection Mode.
	 * 
	 * @param actorToHide
	 */
	public void addActorToHide(Actor actorToHide) {
		this.actorsToHide.add(actorToHide);
	}

	/**
	 * This interface establishes the behavior of a gallery entry that can be
	 * selected.
	 */
	public static interface SelectListener {

		/**
		 * Called when it's selected.
		 */
		void select();

		/**
		 * Called when it's no longer selected.
		 */
		void deselect();

		/**
		 * @return true if is selected, false otherwise.
		 */
		boolean isSelected();

	}
}
