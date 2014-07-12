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
package es.eucm.ead.editor.ui.scenes.ribbon;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.model.Model.SelectionListener;
import es.eucm.ead.editor.model.events.SelectionEvent;
import es.eucm.ead.editor.model.events.SelectionEvent.Type;
import es.eucm.ead.editor.ui.scenes.ribbon.interaction.InteractionTab;
import es.eucm.ead.editor.view.tabs.TabWidget;
import es.eucm.ead.editor.view.tabs.TabsPanel;
import es.eucm.ead.engine.I18N;

/**
 * Created by angel on 22/05/14.
 */
public class SceneRibbon extends TabsPanel implements SelectionListener {

	private TabWidget insertTab;

	private TabWidget formatTab;

	public SceneRibbon(Controller controller) {
		super(controller.getApplicationAssets().getSkin());
		controller.getModel().addSelectionListener(this);

		setBackground(skin.getDrawable("blank"));

		I18N i18N = controller.getApplicationAssets().getI18N();

		insertTab = addTab(i18N.m("scene.insert").toUpperCase()).setContent(
				new InsertTab(controller));
		formatTab = addTab(i18N.m("scene.format").toUpperCase()).setContent(
				new FormatTab(controller));
		addTab(i18N.m("scene.interaction").toUpperCase()).setContent(
				new InteractionTab(controller));
	}

	@Override
	public boolean listenToContext(String contextId) {
		return Selection.SCENE_ELEMENT.equals(contextId);
	}

	@Override
	public void modelChanged(SelectionEvent event) {
		if (event.getType() == Type.FOCUSED && event.getSelection().size > 0) {
			if (getSelectedTab() == insertTab) {
				setSelectedTab(formatTab);
			}
		}
	}
}
