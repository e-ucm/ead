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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import es.eucm.ead.editor.control.ViewsHistory.ViewUpdate;
import es.eucm.ead.editor.control.actions.editor.ChangeView;
import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.events.LoadEvent;
import es.eucm.ead.editor.model.events.LoadEvent.Type;
import es.eucm.ead.editor.view.builders.Builder;
import es.eucm.ead.editor.view.builders.DialogBuilder;
import es.eucm.ead.editor.view.builders.ViewBuilder;
import es.eucm.ead.editor.view.widgets.Dialog;

import java.util.HashMap;
import java.util.Map;

/**
 * Controls all the views
 */
public class Views implements ModelListener<LoadEvent> {

	public static final int CONTEXT_MENU_OFFSET = 3;

	protected Controller controller;

	private Group viewsContainer;

	private Group modalsContainer;

	private Map<Class, ViewBuilder> viewsBuilders;

	private Map<Class, DialogBuilder> dialogBuilders;

	protected ViewBuilder currentView;

	private Object[] currentArgs;

	private Actor currentContextMenu;

	private ViewsHistory viewsHistory;

	private InputListener closeContextMenu = new InputListener() {

		private Vector2 auxVector = new Vector2();

		@Override
		public boolean touchDown(InputEvent event, float x, float y,
				int pointer, int button) {
			if (currentContextMenu != null) {
				// Resend touch down if user pressed outside the context menu
				boolean resendTouch = event.getTarget() != currentContextMenu
						&& !event.getTarget()
								.isDescendantOf(currentContextMenu);

				currentContextMenu.remove();
				currentContextMenu = null;

				if (resendTouch) {
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
		controller.getModel().addLoadListener(this);
		this.viewsContainer = viewsContainer;
		this.modalsContainer = modalsContainer;
		viewsBuilders = new HashMap<Class, ViewBuilder>();
		dialogBuilders = new HashMap<Class, DialogBuilder>();
		viewsHistory = new ViewsHistory();
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

	public void setScrollFocus(Actor actor) {
		viewsContainer.getStage().setScrollFocus(actor);
	}

	public ViewsHistory getViewsHistory() {
		return viewsHistory;
	}

	private <T extends Builder> T getBuilder(Class<T> viewClass,
			Map viewsBuilders) {
		Builder builder = (Builder) viewsBuilders.get(viewClass);

		if (builder == null) {
			try {
				builder = ClassReflection.newInstance(viewClass);
				builder.initialize(controller);
				viewsBuilders.put(viewClass, builder);
			} catch (ReflectionException e) {
				Gdx.app.error("Views",
						"Impossible to create view " + viewClass, e);
				return null;
			}
		}
		return (T) builder;
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
		currentView = getBuilder(viewClass, viewsBuilders);
		if (currentView != null) {
			Actor view = currentView.getView(args);
			if (view != null) {
				viewsContainer.clearChildren();
				viewsContainer.addActor(view);
				if (view instanceof WidgetGroup) {
					((WidgetGroup) view).invalidateHierarchy();
				}
			}
			this.currentArgs = args;
			viewsHistory.viewUpdated(currentView.getClass(), currentArgs);
		}
	}

	public void reinitializeAllViews() {
		for (ViewBuilder viewBuilder : viewsBuilders.values()) {
			viewBuilder.release(controller);
			viewBuilder.initialize(controller);
		}
		setView(currentView.getClass(), currentArgs);
	}

	public <T extends DialogBuilder> void showDialog(Class<T> dialogClass,
			Object... args) {
		DialogBuilder currentView = getBuilder(dialogClass, dialogBuilders);
		if (currentView != null) {
			Dialog dialog = currentView.getDialog(args);
			dialog.show(getViewsContainer().getStage());
			dialog.pack();
			dialog.center();
		}
	}

	/**
	 * Show the given context menu in the given coordinates
	 */
	public void showContextMenu(Actor contextMenu, float x, float y) {
		if (contextMenu instanceof WidgetGroup) {
			((WidgetGroup) contextMenu).pack();
		}

		contextMenu.setPosition(x,
				y + CONTEXT_MENU_OFFSET - contextMenu.getHeight());

		if (currentContextMenu != null) {
			currentContextMenu.remove();
		}

		modalsContainer.addActor(contextMenu);
		setKeyboardFocus(contextMenu);
		setScrollFocus(contextMenu);
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
			changeView(viewUpdate);
		}
	}

	/**
	 * Goes to the next view
	 */
	public void next() {
		ViewUpdate viewUpdate = viewsHistory.next();
		if (viewUpdate != null) {
			changeView(viewUpdate);
		}
	}

	private void changeView(ViewUpdate viewUpdate) {
		Object[] args = new Object[1 + viewUpdate.getArgs().length];
		args[0] = viewUpdate.getViewClass();
		System.arraycopy(viewUpdate.getArgs(), 0, args, 1,
				viewUpdate.getArgs().length);
		controller.action(ChangeView.class, args);
	}

	@Override
	public void modelChanged(LoadEvent event) {
		if (event.getType() == Type.UNLOADED) {
			viewsHistory.clear();
		}
	}
}
