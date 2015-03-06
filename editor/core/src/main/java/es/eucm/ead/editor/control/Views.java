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
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import es.eucm.ead.editor.control.ViewsHistory.ViewUpdate;
import es.eucm.ead.editor.control.actions.editor.ChangeView;
import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.events.LoadEvent;
import es.eucm.ead.editor.model.events.LoadEvent.Type;
import es.eucm.ead.editor.view.Modal;
import es.eucm.ead.editor.view.ModelView;
import es.eucm.ead.editor.view.builders.Builder;
import es.eucm.ead.editor.view.builders.DialogBuilder;
import es.eucm.ead.editor.view.builders.ViewBuilder;
import es.eucm.ead.engine.gdx.AbstractWidget;
import es.eucm.ead.editor.view.widgets.Dialog;

import java.util.HashMap;
import java.util.Map;

/**
 * Controls all the views
 */
public class Views implements ModelListener<LoadEvent> {

	protected Controller controller;

	protected Group viewsContainer;

	protected Group modalsContainer;

	protected Map<Class, ViewBuilder> viewsBuilders;

	private Map<Class, DialogBuilder> dialogBuilders;

	protected ViewBuilder currentView;

	protected Object[] currentArgs;

	private Actor currentModal;

	protected ViewsHistory viewsHistory;

	protected boolean resendTouch = true;

	private Vector2 auxVector = new Vector2();

	private InputEvent lastEvent;

	private Runnable hideModalsContainer = new Runnable() {

		@Override
		public void run() {
			// Resend touch down if user pressed outside the context menu
			boolean resendTouch = lastEvent != null && Views.this.resendTouch
					&& lastEvent.getTarget() != currentModal
					&& !lastEvent.getTarget().isDescendantOf(currentModal);

			if (currentModal != null) {
				currentModal.remove();
				currentModal.setTouchable(Touchable.enabled);
				currentModal = null;
			}

			if (resendTouch) {
				auxVector.set(lastEvent.getStageX(), lastEvent.getStageY());
				lastEvent.getStage().stageToScreenCoordinates(auxVector);
				lastEvent.getStage().touchDown((int) auxVector.x,
						(int) auxVector.y, lastEvent.getPointer(),
						lastEvent.getButton());
			}

			setModalsTouchable(Touchable.disabled);

		}
	};

	private ClickListener closeContextMenu = new ClickListener() {

		@Override
		public void clicked(InputEvent event, float x, float y) {
			if (currentModal != null) {
				hideModal(event);
			}
		}
	};

	public void hideModal() {
		hideModal(null);
	}

	private void hideModal(InputEvent event) {
		lastEvent = event;
		if (currentModal instanceof Modal) {
			Modal modal = ((Modal) currentModal);
			if (modal.hideAlways() || lastEvent == null) {
				modal.hide(hideModalsContainer);
				currentModal.setTouchable(Touchable.disabled);
			} else if (!lastEvent.getTarget().isDescendantOf(currentModal)) {
				modal.hide(hideModalsContainer);
				currentModal.setTouchable(Touchable.disabled);
			}
		} else {
			hideModalsContainer.run();
		}
	}

	/**
	 * 
	 * @return true is a {@link Modal} has been hidden, false otherwise.
	 */
	protected boolean hideModalIfNeeded() {
		if (isModalOpened()) {
			hideModal(null);
			return true;
		}
		return false;
	}

	private boolean isModalOpened() {
		return currentModal != null && currentModal.hasParent();
	}

	private void setModalsTouchable(Touchable touchable) {
		modalsContainer
				.setTouchable(touchable == Touchable.disabled ? Touchable.childrenOnly
						: Touchable.enabled);
	}

	/**
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
		if (modalsContainer instanceof WidgetGroup) {
			((WidgetGroup) modalsContainer).pack();
		}
		modalsContainer.setTouchable(Touchable.childrenOnly);
		modalsContainer.addListener(closeContextMenu);

		viewsBuilders = new HashMap<Class, ViewBuilder>();
		dialogBuilders = new HashMap<Class, DialogBuilder>();
		viewsHistory = new ViewsHistory();
	}

	public Group getViewsContainer() {
		return viewsContainer;
	}

	public Group getModalsContainer() {
		return modalsContainer;
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

	protected <T extends Builder> T getBuilder(Class<T> viewClass,
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
		if (isModalOpened()) {
			setModalsTouchable(Touchable.disabled);
			hideModalsContainer.run();
		}
		modalsContainer.clearChildren();

		controller.getTracker().changeView(viewClass.getSimpleName());
		if (currentView != null) {
			currentView.release(controller);
		}
		currentView = getBuilder(viewClass, viewsBuilders);
		if (currentView != null) {
			Actor view = currentView.getView(args);
			if (view != null) {
				if (viewsContainer.getChildren().size == 1) {
					releaseView(viewsContainer.getChildren().get(0));
				}

				prepareView(view);

				viewsContainer.clearChildren();
				viewsContainer.addActor(view);

				if (view instanceof Layout) {
					((Layout) view).invalidateHierarchy();
					((Layout) view).setFillParent(true);
				}
			}
			this.currentArgs = args;
			viewsHistory.viewUpdated(currentView.getClass(), currentArgs);
		}
	}

	public void reinitializeAllViews() {
		for (ViewBuilder viewBuilder : viewsBuilders.values()) {
			viewBuilder.initialize(controller);
		}
		if (currentView != null) {
			setView(currentView.getClass(), currentArgs);
		}
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
	 * Show the given modal in the given coordinates
	 */
	public void showModal(Actor modal, float x, float y) {
		if (currentModal != null) {
			currentModal.remove();
		}

		modalsContainer.addActor(modal);
		if (modal instanceof Layout) {
			((Layout) modal).pack();
		}

		modal.setSize(
				Math.min(modalsContainer.getWidth(),
						AbstractWidget.getPrefWidth(modal)),
				Math.min(modalsContainer.getHeight(),
						AbstractWidget.getPrefHeight(modal)));

		modal.setPosition(
				Math.max(
						0,
						Math.min(x,
								modalsContainer.getWidth() - modal.getWidth())),
				Math.min(Math.max(0, y),
						modalsContainer.getHeight() - modal.getHeight()));

		modalsContainer.setTouchable(Touchable.enabled);

		if (modal instanceof Modal) {
			((Modal) modal).show(this);
		}

		setKeyboardFocus(modal);
		setScrollFocus(modal);
		currentModal = modal;
	}

	/**
	 * Adds the given actor to the modals container
	 */
	public void addToModalsContainer(Actor actor) {
		modalsContainer.addActor(actor);
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

	private void releaseView(Actor view) {
		if (view instanceof Group) {
			for (Actor actor : ((Group) view).getChildren()) {
				releaseView(actor);
			}
		}

		if (view instanceof ModelView) {
			((ModelView) view).release();
		}
	}

	private void prepareView(Actor view) {
		if (view instanceof ModelView) {
			((ModelView) view).prepare();
		}

		if (view instanceof Group) {
			for (Actor actor : ((Group) view).getChildren()) {
				prepareView(actor);
			}
		}
	}
}
