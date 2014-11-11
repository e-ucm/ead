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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;

import es.eucm.ead.editor.control.MokapController.BackListener;
import es.eucm.ead.editor.control.actions.editor.ChangeView;
import es.eucm.ead.editor.control.transitions.TransitionManager;
import es.eucm.ead.editor.control.transitions.TransitionManager.Transition;
import es.eucm.ead.editor.control.transitions.Transitions;
import es.eucm.ead.editor.view.builders.ViewBuilder;
import es.eucm.ead.editor.view.builders.home.HomeView;
import es.eucm.ead.editor.view.builders.project.ProjectView;
import es.eucm.ead.editor.view.builders.scene.SceneView;

public class MokapViews extends Views implements BackListener, Disposable {
	private static final Transition DEFAULT_TRANSITION = Transitions
			.getFadeTransition(true);

	private TransitionManager transitionManager;
	private ObjectMap<Class<?>, ObjectMap<Class<?>, Transition>> transitions;

	public MokapViews(Controller controller, Group viewsContainer,
			Group modalsContainer) {
		super(controller, viewsContainer, modalsContainer);
		resendTouch = false;
		transitionManager = new TransitionManager(controller, viewsContainer,
				modalsContainer);

		transitions = new ObjectMap<Class<?>, ObjectMap<Class<?>, Transition>>(
				10);

		ObjectMap<Class<?>, Transition> homeViewTransitions = new ObjectMap<Class<?>, Transition>(
				3);
		homeViewTransitions.put(ProjectView.class,
				Transitions.getScaleAndFadeTransition(true));
		transitions.put(HomeView.class, homeViewTransitions);

		ObjectMap<Class<?>, Transition> projectViewTransitions = new ObjectMap<Class<?>, Transition>(
				3);
		projectViewTransitions.put(HomeView.class,
				Transitions.getScaleAndFadeTransition(false));
		projectViewTransitions.put(SceneView.class,
				Transitions.getScaleAndFadeTransition(true));
		transitions.put(ProjectView.class, projectViewTransitions);

		ObjectMap<Class<?>, Transition> sceneViewTransitions = new ObjectMap<Class<?>, Transition>(
				3);
		sceneViewTransitions.put(ProjectView.class,
				Transitions.getScaleAndFadeTransition(false));
		transitions.put(SceneView.class, sceneViewTransitions);

	}

	@Override
	public void onBackPressed() {
		if (!hideModalIfNeeded()) {
			if (currentView instanceof BackListener) {
				((BackListener) currentView).onBackPressed();
			} else {
				ViewBuilder nextView = currentView;
				back();
				if (nextView == currentView) {
					controller.action(ChangeView.class, ProjectView.class);
				}
			}
		}
	}

	public <T extends ViewBuilder> void setView(Class<T> viewClass,
			Object... args) {
		controller.getTracker().changeView(viewClass.getSimpleName());
		if (currentView != null) {
			currentView.release(controller);
		}
		Class currentClass = currentView != null ? currentView.getClass()
				: null;
		currentView = getBuilder(viewClass, viewsBuilders);
		if (currentView != null) {
			Actor next = currentView.getView(args);
			if (next != null) {

				Transition transition = null;
				if (currentClass != null) {
					transition = transitions.get(currentClass).get(
							currentView.getClass());
				}
				if (transition == null) {
					transition = DEFAULT_TRANSITION;
				}
				transitionManager.prepateTransition(transition, next);
				currentArgs = args;
				viewsHistory.viewUpdated(currentView.getClass(), currentArgs);
			}
		}
	}

	public void hideOnscreenKeyboard() {
		Gdx.input.setOnscreenKeyboardVisible(false);
		Stage stage = modalsContainer.getStage();
		if (stage != null) {
			stage.setKeyboardFocus(null);
			stage.unfocusAll();
		}
	}

	public void dispose() {
		transitionManager.dispose();
	}

}
