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
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import es.eucm.ead.editor.control.MockupController.BackListener;
import es.eucm.ead.editor.control.actions.editor.ChangeMockupView;
import es.eucm.ead.editor.control.actions.editor.ForceSave;
import es.eucm.ead.editor.view.builders.ViewBuilder;
import es.eucm.ead.editor.view.builders.gallery.ProjectsView;
import es.eucm.ead.editor.view.builders.gallery.ScenesView;
import es.eucm.ead.editor.view.widgets.HiddenPanel;
import es.eucm.ead.editor.view.widgets.Notification;
import es.eucm.ead.editor.view.widgets.helpmessage.sequence.HelpSequence;

public class MockupViews extends Views implements BackListener {

	private Toasts toasts;
	private ObjectMap<ViewBuilder, HelpSequence> helpMessages;
	private static final Array<HiddenPanel> showingPanels = new Array<HiddenPanel>(
			3);
	private static final InputListener handleHit = new InputListener() {

		@Override
		public boolean touchDown(InputEvent event, float x, float y,
				int pointer, int button) {
			if (showingPanels.size > 0) {
				HiddenPanel showingPanel = showingPanels.peek();
				boolean hide = !showingPanel.isAscendantOf(event.getTarget());
				if (hide) {
					if (showingPanel.isModal()) {
						event.cancel();
					}
					showingPanel.hide();
				}
			}
			return false;
		}
	};

	public static void setUpHiddenPanel(HiddenPanel panel, Stage stage) {
		if (!(panel instanceof Notification)) {
			showingPanels.add(panel);
			stage.addCaptureListener(handleHit);
		}
	}

	public static void removeHitListener(HiddenPanel panel, Stage stage) {
		if (!(panel instanceof Notification)) {
			showingPanels.removeValue(panel, true);
			if (showingPanels.size == 0) {
				stage.removeCaptureListener(handleHit);
			}
		}
	}

	public MockupViews(Controller controller, Group viewsContainer) {
		super(controller, viewsContainer, viewsContainer);
		helpMessages = new ObjectMap<ViewBuilder, HelpSequence>(8);
		toasts = new Toasts(controller);
	}

	@Override
	public void onBackPressed() {
		if (showingPanels.size > 0) {
			showingPanels.pop().hide();
		} else {
			if (currentView instanceof BackListener) {
				((BackListener) currentView).onBackPressed();
			} else {
				ViewBuilder nextView = currentView;
				back();
				if (nextView == currentView) {
					controller.action(ChangeMockupView.class, ScenesView.class);
				}
			}
		}
	}

	protected Class getChangeViewClass() {
		return ChangeMockupView.class;
	}

	@Override
	public <T extends ViewBuilder> void setView(Class<T> viewClass,
			Object... args) {
		while (showingPanels.size > 0) {
			showingPanels.pop().hide();
		}

		hideOnscreenKeyboard();
		super.setView(viewClass, args);

		modalsContainer
				.addAction(com.badlogic.gdx.scenes.scene2d.actions.Actions
						.delay(0.3f, Actions.run(showHelpMessage)));
	}

	public void hideOnscreenKeyboard() {
		Gdx.input.setOnscreenKeyboardVisible(false);
		Stage stage = modalsContainer.getStage();
		if (stage != null) {
			stage.setKeyboardFocus(null);
			stage.unfocusAll();
		}
	}

	private final Runnable showHelpMessage = new Runnable() {

		@Override
		public void run() {
			HelpSequence helpSequence = helpMessages.get(currentView);
			if (helpSequence != null) {
				boolean helpMessages = controller.getPreferences().getBoolean(
						Preferences.ENABLE_HELP_MSGS, true);
				if (helpMessages && helpSequence.getCondition()) {
					helpSequence.show();
				}

			}
		}
	};

	public void registerHelpMessage(HelpSequence seq) {
		helpMessages.put(seq.getViewBuilder(), seq);
	}

	public void pause() {
		if (currentView.getClass() != ProjectsView.class) {
			controller.action(ForceSave.class);
		}
	}

	public Toasts getToasts() {
		return toasts;
	}

}
