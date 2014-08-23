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
package es.eucm.ead.editor.view.widgets.editionview.prefabs;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.view.widgets.IconButton;
import es.eucm.ead.editor.view.widgets.iconwithpanel.IconWithFadePanel;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.engine.I18N;

public abstract class PrefabPanel extends IconWithFadePanel {

	private static final float SEPARATION = 5, PAD_TITLE = 100, PAD = 20;

	protected Skin skin;
	protected I18N i18n;
	protected Controller controller;

	protected Selection selection;

	public PrefabPanel(String icon, float size, String panelName,
			Controller controller, Actor touchable) {
		super(icon, 0, SEPARATION, size, controller.getApplicationAssets()
				.getSkin());
		this.controller = controller;
		this.skin = controller.getApplicationAssets().getSkin();
		this.i18n = controller.getApplicationAssets().getI18N();

		selection = controller.getModel().getSelection();

		panel.addTouchableActor(touchable);

		IconButton trash = new IconButton("recycle24x24", 0, skin);
		InputListener listener = trashListener();
		if (listener != null) {
			trash.addListener(listener);
		}

		LinearLayout top = new LinearLayout(true);

		top.add(new Label(i18n.m(panelName), skin)).expand(true, true)
				.margin(PAD, PAD, PAD_TITLE, PAD);
		top.add(trash).margin(PAD);
		panel.add(top);
		panel.row().padBottom(PAD);
	}

	protected abstract InputListener trashListener();

}
