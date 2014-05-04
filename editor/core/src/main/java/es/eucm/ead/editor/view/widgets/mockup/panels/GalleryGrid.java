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
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.widgets.mockup.ConfirmationDialog;
import es.eucm.ead.editor.view.widgets.mockup.ToolBar;
import es.eucm.ead.editor.view.widgets.mockup.buttons.ToolbarButton;
import es.eucm.ead.engine.I18N;

/**
 * A GridPanel that has multiple selection functionality after long press. Its
 * elements must implement {@link SelectListener} in order to be able to select
 * them otherwise nothing would happen after a LongPress event.
 */
public class GalleryGrid<T extends Actor> extends GridPanel<T> {

	private static final float DEFAULT_ENTYTY_SPACING = 20f;
	private static final float BACK_BUTTON_PAD_LEFT = 40f;

	/**
	 * A collection storing the entities we've selected.
	 */
	protected Array<SelectListener> selectedEntities;

	/**
	 * A collection storing the entities we've selected as Actors.
	 */
	protected Array<Actor> selectedActors;
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

	public GalleryGrid(Skin skin, int cols, Vector2 point, WidgetGroup root,
			Controller controller, Actor... actorsToHide) {
		this(skin, cols, point, root, true, controller);
	}

	public GalleryGrid(Skin skin, int cols, Vector2 point, WidgetGroup root,
			boolean selectable, Controller controller) {
		super(cols, DEFAULT_ENTYTY_SPACING);
		if (!selectable)
			return;
		this.i18n = controller.getApplicationAssets().getI18N();
		this.actorsToHide = new Array<Actor>(false, 2);
		this.selectedEntities = new Array<SelectListener>(false, 16);
		this.selectedActors = new Array<Actor>(false, 16);
		this.selecting = false;
		addCaptureListener(new ActorGestureListener() {

			/**
			 * Auxiliary attribute used to know if the target of our event it's
			 * indeed instance of GalleryEntity (which implements
			 * SelectListener)
			 */
			private SelectListener target;
			private Actor targetActor;

			@Override
			public void touchDown(InputEvent event, float x, float y,
					int pointer, int button) {

				this.targetActor = event.getTarget();
				while (this.targetActor != null
						&& !(this.targetActor instanceof SelectListener)) {
					this.targetActor = this.targetActor.getParent();
				}
				if (this.targetActor == null
						|| !(this.targetActor instanceof SelectListener))
					return;
				prepareTouchDown(this.targetActor);
			}

			private void prepareTouchDown(Actor target) {
				this.target = (SelectListener) target;
				if (selecting) {
					if (this.target.isSelected()) {
						this.target.deselect();
						removeSelectedEntry(target, this.target);
					} else {
						this.target.select();
						addSelectedEntry(target, this.target);
					}
				}
			}

			@Override
			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				if (selecting) {
					return;
				}
				if (this.targetActor != null)
					GalleryGrid.this.entityClicked(event, this.targetActor);
			}

			@Override
			public boolean longPress(Actor actor, float x, float y) {
				if (selecting) {
					return true;
				}
				if (this.target instanceof SelectListener) {
					startSelecting();
				}
				return true;
			}

			private void addSelectedEntry(Actor actor, SelectListener entity) {
				if (selectedEntities.size == 0) {
					deleteButton.setVisible(true);
				}
				selectedActors.add(actor);
				selectedEntities.add(entity);
				numSelectedEntities.setText(i18n.m(selectedEntities.size,
						"general.gallery.selected-singular",
						"general.gallery.selected-plural",
						selectedEntities.size));
				GalleryGrid.this.entitySelected(actor, selectedEntities.size);
			}

			private void removeSelectedEntry(Actor actor, SelectListener entity) {
				selectedEntities.removeValue(entity, true);
				selectedActors.removeValue(actor, true);
				final int entitiesCount = selectedEntities.size;
				numSelectedEntities.setText(i18n.m(entitiesCount,
						"general.gallery.selected-singular",
						"general.gallery.selected-plural", entitiesCount));
				if (entitiesCount == 0) {
					deleteButton.setVisible(false);
				}
				GalleryGrid.this.entitySelected(actor, entitiesCount);
			}

			private void startSelecting() {
				selecting = true;
				this.target.select();
				addSelectedEntry(this.targetActor, this.target);
				changeActorsVisibility(false);
			}
		});

		initializeTopToolBar(skin, point, root);
	}

	private void initializeTopToolBar(Skin skin, Vector2 viewport,
			WidgetGroup root) {
		final Dialog confirmDialog = new ConfirmationDialog(
				this.i18n.m("general.gallery.delete-resources"), null,
				this.i18n.m("general.accept"), this.i18n.m("general.cancel"),
				skin) {
			@Override
			protected void result(Object object) {
				onHide(!(Boolean) object);
			}
		};

		this.topToolbar = new ToolBar(viewport, skin);
		this.topToolbar.setVisible(false);

		this.deleteButton = new ToolbarButton(viewport, IC_DELETE,
				this.i18n.m("general.delete"), skin);
		final Button backButton = new ToolbarButton(viewport, IC_GO_BACK,
				this.i18n.m("general.gallery.deselect"), skin);

		final ClickListener mListener = new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				final Actor target = event.getListenerActor();
				if (target == deleteButton) {
					confirmDialog.getContentTable().clearChildren();
					final String message = i18n.m(selectedEntities.size,
							"general.gallery.delete-singular",
							"general.gallery.delete-plural",
							selectedEntities.size);
					confirmDialog.text(message).show(getStage());
				} else if (target == backButton) {
					onHide(true);
				}
			}
		};
		this.deleteButton.addListener(mListener);
		backButton.addListener(mListener);

		this.numSelectedEntities = new Label("", skin);
		this.topToolbar.add(backButton);
		this.topToolbar.add(this.numSelectedEntities).left().expandX()
				.align(Align.center);
		addExtrasToTopToolbar(this.topToolbar);
		this.topToolbar.add(this.deleteButton);
		final Container wrapper = new Container(this.topToolbar).fillX().top();
		wrapper.setFillParent(true);
		root.addActor(wrapper);
	}

	private void changeActorsVisibility(boolean visible) {
		int i = 0;
		final int length = this.actorsToHide.size;
		for (; i < length; ++i) {
			final Actor actorToHide = this.actorsToHide.get(i);
			if (actorToHide != null) {
				actorToHide.setVisible(visible);
			}
		}
		this.topToolbar.setVisible(!visible);
	}

	/**
	 * Invoked only if target Actor implements {@link SelectListener}. Called
	 * when target Actor was clicked if {@link GalleryGrid} is not in Selection
	 * Mode. Convenience method that should be overridden if needed.
	 */
	protected void entityClicked(InputEvent event, Actor target) {
	}

	/**
	 * Invoked when an entity was selected or unmarked in selection mode.
	 * 
	 * @param actor
	 * @param entitiesCount
	 */
	protected void entitySelected(Actor actor, int entitiesCount) {
	}

	/**
	 * Convenience method. Add here any extra {@link Actor} to the {link ToolBar
	 * topToolbar shown when we are selecting entities.
	 * 
	 * @param topToolbar
	 */
	protected void addExtrasToTopToolbar(ToolBar topToolbar) {
	}

	/**
	 * Resets previous visibility changes to actors.
	 * 
	 * @param deselect
	 *            if true the actors will only be deselected, erased otherwise.
	 */
	public void onHide(boolean deselect) {
		changeActorsVisibility(true);
		for (final SelectListener select : this.selectedEntities) {
			select.deselect();
		}
		if (!deselect) {
			onDelete(selectedActors);
		}
		this.selectedActors.clear();
		this.selectedEntities.clear();
		this.selecting = false;
	}

	/**
	 * Invoked when the used agreed to delete the selected actors.
	 * 
	 * @param selectedActors
	 */
	protected void onDelete(Array<Actor> selectedActors) {
	}

	/**
	 * True if we're in "selection mode"
	 */
	public boolean isSelecting() {
		return this.selecting;
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
