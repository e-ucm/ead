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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import es.eucm.ead.editor.control.ViewsHistory.ViewUpdate;
import es.eucm.ead.editor.view.builders.DialogBuilder;
import es.eucm.ead.editor.view.builders.ViewBuilder;
import es.eucm.ead.editor.view.builders.classic.dialogs.ConfirmationDialogBuilder;
import es.eucm.ead.editor.view.builders.classic.dialogs.InfoDialogBuilder;
import es.eucm.ead.editor.view.builders.classic.dialogs.NewProjectDialog;
import es.eucm.ead.editor.view.widgets.Dialog;
import es.eucm.ead.editor.view.widgets.menu.ContextMenu;
import es.eucm.ead.editor.view.widgets.menu.ContextMenuItem;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Controls all the views
 */
public class Views {

	public static final int CONTEXT_MENU_OFFSET = 3;

	protected Controller controller;

	private Group viewsContainer;

	private Group modalsContainer;

	private Map<Class, ViewBuilder> viewsBuilders;

	private Map<String, Dialog> dialogsCache;

	private Map<String, DialogBuilder> dialogBuilders;

	private Map<Actor, ContextMenu> contextMenues;

	protected ViewBuilder currentView;

	private Object[] currentArgs;

	private ContextMenu currentContextMenu;

	private ViewsHistory viewsHistory;

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
				// Resend touch down if user pressed outside the context menu
				if (!(event.getTarget() instanceof ContextMenuItem)) {
					auxVector.set(event.getStageX(), event.getStageY());
					event.getStage().stageToScreenCoordinates(auxVector);
					event.getStage().touchDown((int) auxVector.x,
							(int) auxVector.y, event.getPointer(),
							event.getButton());
				}

			}
			return true;
		}
	};

	/**
	 * 
	 * @param controller
	 *            the editor controller
	 * @param viewsContainer
	 *            the root container where the main view must be added
	 * @param modalsContainer
	 *            the container where context menues must appear
	 */
	public Views(Controller controller, Group viewsContainer,
			Group modalsContainer) {
		this.controller = controller;
		this.viewsContainer = viewsContainer;
		this.modalsContainer = modalsContainer;
		dialogsCache = new HashMap<String, Dialog>();
		viewsBuilders = new HashMap<Class, ViewBuilder>();
		dialogBuilders = new HashMap<String, DialogBuilder>();
		contextMenues = new IdentityHashMap<Actor, ContextMenu>();
		viewsHistory = new ViewsHistory();
		addDialogs();
	}

	public Group getViewsContainer() {
		return viewsContainer;
	}

	public Actor getKeyboardFocus() {
		return viewsContainer.getStage().getKeyboardFocus();
	}

	public void setKeyboardFocus(Actor actor) {
		viewsContainer.getStage().setKeyboardFocus(actor);
	}

	private void addDialogs() {
		addDialog(new NewProjectDialog());
		addDialog(new ConfirmationDialogBuilder());
		addDialog(new InfoDialogBuilder());
	}

	public void addDialog(DialogBuilder dialogBuilder) {
		dialogBuilders.put(dialogBuilder.getName(), dialogBuilder);
	}

	/**
	 * Sets as root the view with the given class. Releases the current view
	 * 
	 * @param viewClass
	 *            the view name
	 */
	public <T extends ViewBuilder> void setView(Class<T> viewClass,
			Object... args) {
		if (currentView != null) {
			currentView.release(controller);
		}

		ViewBuilder builder = viewsBuilders.get(viewClass);

		if (builder == null) {
			try {
				builder = ClassReflection.newInstance(viewClass);
				builder.initialize(controller);
				viewsBuilders.put(viewClass, builder);
			} catch (ReflectionException e) {
				Gdx.app.error("Views",
						"Impossible to create view " + viewClass, e);
				return;
			}
		}

		Actor view = builder.getView(args);
		if (view != null) {
			viewsContainer.clearChildren();
			viewsContainer.addActor(view);
			if (view instanceof WidgetGroup) {
				((WidgetGroup) view).invalidateHierarchy();
			}
		}

		currentView = builder;
		this.currentArgs = args;

		viewsHistory.viewUpdated(currentView.getClass(), currentArgs);
	}

	public void reinitializeAllViews() {
		for (ViewBuilder viewBuilder : viewsBuilders.values()) {
			viewBuilder.release(controller);
			viewBuilder.initialize(controller);
		}
		setView(currentView.getClass(), currentArgs);
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
		dialog.show(getViewsContainer().getStage());
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
			showContextMenu(contextMenu, x, y);
		}
	}

	/**
	 * Show the given context menu in the given coordinates
	 */
	public void showContextMenu(ContextMenu contextMenu, float x, float y) {
		contextMenu.pack();
		contextMenu.setPosition(x,
				y + CONTEXT_MENU_OFFSET - contextMenu.getHeight());
		modalsContainer.addActor(contextMenu);
		currentContextMenu = contextMenu;
		modalsContainer.addListener(closeContextMenu);
	}

	public void requestKeyboardFocus(Actor actor) {
		viewsContainer.getStage().setKeyboardFocus(actor);
	}

	public ViewBuilder getCurrentView() {
		return currentView;
	}

	public Object[] getCurrentArgs() {
		return currentArgs;
	}

	/**
	 * Goes to the previous view
	 */
	public void back() {
		ViewUpdate viewUpdate = viewsHistory.back();
		if (viewUpdate != null) {
			setView(viewUpdate.getViewClass(), viewUpdate.getArgs());
		}
	}

	/**
	 * Goes to the next view
	 */
	public void next() {
		ViewUpdate viewUpdate = viewsHistory.next();
		if (viewUpdate != null) {
			setView(viewUpdate.getViewClass(), viewUpdate.getArgs());
		}
	}
}
