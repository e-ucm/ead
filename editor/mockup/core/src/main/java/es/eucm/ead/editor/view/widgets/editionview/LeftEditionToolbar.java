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
package es.eucm.ead.editor.view.widgets.editionview;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.editor.elementeditstate.SetAllUnlocked;
import es.eucm.ead.editor.control.actions.editor.elementeditstate.SetAllVisible;
import es.eucm.ead.editor.view.widgets.IconButton;
import es.eucm.ead.editor.view.widgets.Toolbar;
import es.eucm.ead.editor.view.widgets.editionview.prefabs.ChangeVariablePanel;
import es.eucm.ead.editor.view.widgets.editionview.prefabs.DeparturePanel;
import es.eucm.ead.editor.view.widgets.editionview.prefabs.ShowTextPanel;
import es.eucm.ead.editor.view.widgets.editionview.prefabs.SoundPanel;
import es.eucm.ead.editor.view.widgets.editionview.prefabs.TouchabilityPanel;
import es.eucm.ead.editor.view.widgets.editionview.prefabs.TweensPanel;
import es.eucm.ead.editor.view.widgets.editionview.prefabs.VisibilityPanel;

public class LeftEditionToolbar extends ScrollPane {

	public LeftEditionToolbar(final Controller controller, String style,
			float smallPad, float normalPad) {
		super(null);

		Toolbar toolbar = new Toolbar(controller.getApplicationAssets()
				.getSkin(), style);
		setWidget(toolbar);
		setScrollingDisabled(true, false);
		setOverscroll(false, false);

		toolbar.align(Align.center);
		DeparturePanel departurePanel = new DeparturePanel(controller);
		ChangeVariablePanel changeVariablePanel = new ChangeVariablePanel(
				controller);
		VisibilityPanel visibilityPanel = new VisibilityPanel(controller);
		TweensPanel tweensPanel = new TweensPanel(controller);
		TouchabilityPanel touchabilityPanel = new TouchabilityPanel(controller);
		SoundPanel sound = new SoundPanel(controller);
		ShowTextPanel conversations = new ShowTextPanel(controller);

		toolbar.defaults().padBottom(smallPad).fill().expandX();

		toolbar.add(departurePanel);
		toolbar.row();
		toolbar.add(changeVariablePanel);
		toolbar.row();
		toolbar.add(sound);
		toolbar.row();
		toolbar.add(conversations).padBottom(normalPad);
		toolbar.row();
		toolbar.add(visibilityPanel);
		toolbar.row();
		toolbar.add(touchabilityPanel);
		toolbar.row();
		toolbar.add(tweensPanel);

		// By the moment the unlock and set visible all actors buttons are in
		// leftEditionToolbar
		Skin skin = controller.getApplicationAssets().getSkin();
		final IconButton invisible = new IconButton("visibility80x80", 0f, skin);
		final IconButton lock = new IconButton("lock80x80", 0f, skin);
		toolbar.row();
		toolbar.add(invisible).padTop(normalPad);
		toolbar.row();
		toolbar.add(lock);

		ClickListener listener = new ClickListener() {
			public void clicked(
					com.badlogic.gdx.scenes.scene2d.InputEvent event, float x,
					float y) {
				Actor listener = event.getListenerActor();
				if (listener == invisible) {
					controller.action(SetAllVisible.class);
				} else if (listener == lock) {
					controller.action(SetAllUnlocked.class);
				}
			};
		};

		lock.addListener(listener);
		invisible.addListener(listener);
	}

}