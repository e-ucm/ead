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
import com.badlogic.gdx.utils.SnapshotArray;
import es.eucm.ead.editor.control.MockupController.BackListener;
import es.eucm.ead.editor.control.transitions.TransitionManager;
import es.eucm.ead.editor.control.transitions.TransitionManager.Transition;
import es.eucm.ead.editor.view.builders.ViewBuilder;

public class MockupViews extends Views implements BackListener, Disposable {

	private TransitionManager transitionManager;

	public MockupViews(Controller controller, Group viewsContainer) {
		super(controller, viewsContainer, viewsContainer);
		transitionManager = new TransitionManager(controller, viewsContainer,
				this);
	}

	public <T extends ViewBuilder> void transition(Class<T> viewClass,
			Transition transition, Object... args) {
		controller.getTracker().changeView(viewClass.getSimpleName());
		if (currentView != null) {
			currentView.release(controller);
		}
		currentView = getBuilder(viewClass, viewsBuilders);
		if (currentView != null) {
			SnapshotArray<Actor> children = viewsContainer.getChildren();
			Actor current = children.size > 0 ? children.first() : null;
			Actor[] actors = children.begin();
			for (int i = 0, n = children.size; i < n; i++) {
				Actor child = actors[i];
				if (current != child) {
					child.remove();
				}
			}
			children.end();
			Actor next = currentView.getView(args);
			if (next != null) {
				transitionManager.prepateTransition(transition, current, next);
				currentArgs = args;
				viewsHistory.viewUpdated(currentView.getClass(), currentArgs);
			}
		}

	}

	@Override
	public void onBackPressed() {

	}

	protected Class getChangeViewClass() {
		return null;
	}

	@Override
	public <T extends ViewBuilder> void setView(Class<T> viewClass,
			Object... args) {
		super.setView(viewClass, args);
	}

	public void hideOnscreenKeyboard() {
		Gdx.input.setOnscreenKeyboardVisible(false);
		Stage stage = modalsContainer.getStage();
		if (stage != null) {
			stage.setKeyboardFocus(null);
			stage.unfocusAll();
		}
	}

	public void pause() {
		/*
		 * if (currentView.getClass() != ProjectsView.class) {
		 * controller.action(ForceSave.class); }
		 */
	}

	public void dispose() {
		transitionManager.dispose();
	}

}
