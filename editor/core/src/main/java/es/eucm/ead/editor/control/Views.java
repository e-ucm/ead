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
package es.eucm.ead.editor.control;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

import es.eucm.ead.editor.view.builders.DialogBuilder;
import es.eucm.ead.editor.view.builders.ViewBuilder;
import es.eucm.ead.editor.view.builders.classic.MainBuilder;
import es.eucm.ead.editor.view.builders.classic.dialogs.ConfirmationDialogBuilder;
import es.eucm.ead.editor.view.builders.classic.dialogs.InfoDialogBuilder;
import es.eucm.ead.editor.view.builders.classic.dialogs.NewProjectDialog;
import es.eucm.ead.editor.view.builders.mockup.camera.Picture;
import es.eucm.ead.editor.view.builders.mockup.camera.Video;
import es.eucm.ead.editor.view.builders.mockup.edition.ElementEdition;
import es.eucm.ead.editor.view.builders.mockup.edition.SceneEdition;
import es.eucm.ead.editor.view.builders.mockup.gallery.ElementGallery;
import es.eucm.ead.editor.view.builders.mockup.gallery.Gallery;
import es.eucm.ead.editor.view.builders.mockup.gallery.ProjectGallery;
import es.eucm.ead.editor.view.builders.mockup.gallery.RepositoryGallery;
import es.eucm.ead.editor.view.builders.mockup.gallery.SceneGallery;
import es.eucm.ead.editor.view.builders.mockup.menu.InitialScreen;
import es.eucm.ead.editor.view.builders.mockup.menu.ProjectScreen;
import es.eucm.ead.editor.view.widgets.Dialog;
import es.eucm.ead.editor.view.widgets.menu.ContextMenu;

/**
 * Controls all the views
 */
public class Views {

	protected Controller controller;

	private Group rootContainer;

	private Map<String, Actor> viewsCache;

	private Map<String, Dialog> dialogsCache;

	private Map<String, ViewBuilder> viewsBuilders;

	private Map<String, DialogBuilder> dialogBuilders;

	private Map<Actor, ContextMenu> contextMenues;

	protected String currentViewName;

	protected ViewBuilder currentView;

	private ContextMenu currentContextMenu;

	private InputListener captureRightClick = new InputListener() {
		@Override
		public boolean touchDown(InputEvent event, float x, float y,
				int pointer, int button) {
			if (button == Buttons.RIGHT) {
				contextMenu(event.getListenerActor(), event.getStageX(),
						event.getStageY());
				event.cancel();
			}
			return true;
		}
	};

	private InputListener closeContextMenu = new InputListener() {

		private Vector2 auxVector = new Vector2();

		@Override
		public boolean touchDown(InputEvent event, float x, float y,
				int pointer, int button) {
			if (currentContextMenu != null) {
				currentContextMenu.remove();
				currentContextMenu = null;
				// Resend touch down
				auxVector.set(event.getStageX(), event.getStageY());
				event.getStage().stageToScreenCoordinates(auxVector);
				event.getStage().touchDown((int) auxVector.x,
						(int) auxVector.y, event.getPointer(),
						event.getButton());

			}
			return true;
		}
	};

	/**
	 * 
	 * @param controller
	 *            the editor controller
	 * @param rootContainer
	 *            the root container where the main view must be added
	 */
	public Views(Controller controller, Group rootContainer) {
		this.controller = controller;
		this.rootContainer = rootContainer;
		viewsCache = new HashMap<String, Actor>();
		dialogsCache = new HashMap<String, Dialog>();
		viewsBuilders = new HashMap<String, ViewBuilder>();
		dialogBuilders = new HashMap<String, DialogBuilder>();
		contextMenues = new IdentityHashMap<Actor, ContextMenu>();
		addViews();
		addDialogs();
	}

	public Group getRootContainer() {
		return rootContainer;
	}

	public Actor getKeyboardFocus() {
		return rootContainer.getStage().getKeyboardFocus();
	}

	public void setKeyboardFocus(Actor actor) {
		rootContainer.getStage().setKeyboardFocus(actor);
	}

	private void addViews() {
		addView(new MainBuilder(controller));
		addView(new InitialScreen());
		addView(new ProjectScreen());
		addView(new ProjectGallery());
		addView(new Gallery());
		addView(new ElementGallery());
		addView(new SceneGallery());
		addView(new Picture());
		addView(new Video());
		addView(new SceneEdition());
		addView(new ElementEdition());
		addView(new RepositoryGallery());
	}

	private void addDialogs() {
		addDialog(new NewProjectDialog());
		addDialog(new ConfirmationDialogBuilder());
		addDialog(new InfoDialogBuilder());
	}

	public void addView(ViewBuilder viewBuilder) {
		viewsBuilders.put(viewBuilder.getName(), viewBuilder);
	}

	public void addDialog(DialogBuilder dialogBuilder) {
		dialogBuilders.put(dialogBuilder.getName(), dialogBuilder);
	}

	/**
	 * Sets as root the view with the given name. Hides any other current view
	 * 
	 * @param name
	 *            the view name
	 */
	public void setView(String name) {
		if (currentView != null) {
			currentView.release(controller);
		}

		ViewBuilder builder = viewsBuilders.get(name);
		Actor view = viewsCache.get(name);
		if (view == null) {
			if (builder != null) {
				view = builder.build(controller);
				viewsCache.put(name, view);
			}
		}
		currentViewName = name;

		if (builder != null) {
			builder.initialize(controller);
		}

		currentView = builder;

		if (view != null) {
			rootContainer.clearChildren();
			rootContainer.addActor(view);
			if (view instanceof WidgetGroup) {
				((WidgetGroup) view).invalidateHierarchy();
			}
		}
	}

	/**
	 * Clears the views cache. Called whenever all the views must be regenerated
	 * (e.g., when the interface language changed)
	 */
	public void clearCache() {
		viewsCache.clear();
	}

	/**
	 * Reloads the current view
	 */
	public void reloadCurrentView() {
		setView(currentViewName);
	}

	public void showDialog(String name, Object... arguments) {
		Dialog dialog = dialogsCache.get(name);
		boolean center = false;
		if (dialog == null) {
			DialogBuilder builder = dialogBuilders.get(name);
			if (builder == null) {
				Gdx.app.error("Views", "No dialog with name " + name);
				return;
			} else {
				dialog = builder.build(controller, arguments);
				dialog.setSize(
						Math.max(dialog.getPrefWidth(), dialog.getWidth()),
						Math.max(dialog.getPrefHeight(), dialog.getHeight()));
				center = true;
				dialogsCache.put(name, dialog);
			}
		}
		dialog.show(controller.getViews().getRootContainer().getStage());
		// Can't be centered until is added
		if (center) {
			dialog.center();
		}
	}

	/**
	 * Registers a context menu that will be shown when the user right clicks
	 * owner
	 * 
	 * @param owner
	 *            the owner of the context menu
	 * @param contextMenu
	 *            the context menu to show
	 */
	public void registerContextMenu(Actor owner, ContextMenu contextMenu) {
		contextMenu.setOpaque(true);
		contextMenu.addListener(closeContextMenu);
		owner.addListener(captureRightClick);
		contextMenues.put(owner, contextMenu);
	}

	/**
	 * Show the context menu associated to owner. If owner does not have an
	 * associated context menu, nothing happens
	 * 
	 * @param owner
	 *            the owner of the context menu
	 * @param x
	 *            x position for the context menu
	 * @param y
	 *            y position for the context menu
	 */
	public void contextMenu(Actor owner, float x, float y) {
		ContextMenu contextMenu = contextMenues.get(owner);
		if (contextMenu != null) {
			contextMenu.setPosition(x, y + 3);
			contextMenu.pack();
			rootContainer.addActor(contextMenu);
			currentContextMenu = contextMenu;
			rootContainer.addListener(closeContextMenu);
		}
	}

	public void requestKeyboardFocus(Actor actor) {
		rootContainer.getStage().setKeyboardFocus(actor);
	}
}
