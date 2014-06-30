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
package es.eucm.ead.editor.ui.perspectives;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.editor.ChangeView;
import es.eucm.ead.editor.control.actions.editor.ShowContextMenu;
import es.eucm.ead.editor.control.views.DebugView;
import es.eucm.ead.editor.control.views.HomeView;
import es.eucm.ead.editor.control.views.InterfaceView;
import es.eucm.ead.editor.control.views.SceneView;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Model.FieldListener;
import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.events.FieldEvent;
import es.eucm.ead.editor.model.events.LoadEvent;
import es.eucm.ead.editor.view.builders.ViewBuilder;
import es.eucm.ead.editor.view.listeners.ActionOnClickListener;
import es.eucm.ead.editor.view.listeners.ActionOnDownListener;
import es.eucm.ead.editor.view.widgets.IconButton;
import es.eucm.ead.editor.view.widgets.Separator;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.editor.view.widgets.menu.ContextMenu;
import es.eucm.ead.editor.view.widgets.menu.ContextMenuItem;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.editor.components.Documentation;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.FieldName;
import es.eucm.ead.schemax.entities.ResourceCategory;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Contains all the perspective buttons
 */
public class PerspectiveButtons extends LinearLayout {

	public static final int BUTTONS_IMAGE_PADDING = 5;

	public static final int BUTTONS_PADDING = 5;

	private Controller controller;

	private ContextMenu scenesContextMenu;

	private IconButton scenesButton;

	private Map<ModelEntity, ContextMenuItem> items;

	private NameListener nameListener = new NameListener();

	public PerspectiveButtons(Controller controller) {
		super(true);
		this.controller = controller;
		items = new IdentityHashMap<ModelEntity, ContextMenuItem>();

		Skin skin = controller.getApplicationAssets().getSkin();
		I18N i18N = controller.getApplicationAssets().getI18N();

		add(new Separator(false, skin));
		add(createButton("home32x32", i18N.m("perspectives.home"), skin,
				HomeView.class));
		add(createScenesButton(skin));
		add(new Separator(false, skin));
		add(createButton("interface32x32", i18N.m("perspectives.interface"),
				skin, InterfaceView.class));
		add(new Separator(false, skin));
		add(createButton("education32x32", i18N.m("perspectives.education"),
				skin, null));
		add(new Separator(false, skin));
		add(createButton("testgame32x32", i18N.m("perspectives.testgame"),
				skin, DebugView.class));
		add(new Separator(false, skin));
		add(createButton("export32x32", i18N.m("perspectives.export"), skin,
				null));

		scenesContextMenu = new ContextMenu(skin);

		controller.getModel().addLoadListener(new LoadListener());
	}

	private <T extends ViewBuilder> Actor createButton(String drawable,
			String text, Skin skin, Class<T> viewClass) {
		IconButton button = new IconButton(drawable, BUTTONS_IMAGE_PADDING,
				skin);
		button.add(new Label(text, skin, "title")).padLeft(BUTTONS_PADDING)
				.padRight(BUTTONS_PADDING);
		button.addListener(new ActionOnClickListener(controller,
				ChangeView.class, viewClass));
		button.setName(text);
		return button;
	}

	private Actor createScenesButton(Skin skin) {
		return scenesButton = new IconButton("arrow-down-dark", skin);
	}

	private void refreshScenes(Model model) {
		items.clear();
		scenesContextMenu.clearChildren();
		model.removeListenerFromAllTargets(nameListener);

		for (Entry<String, Object> sceneEntry : model.getResources(
				ResourceCategory.SCENE).entrySet()) {

			ModelEntity scene = (ModelEntity) sceneEntry.getValue();
			Documentation doc = Model.getComponent(scene, Documentation.class);

			ContextMenuItem item = scenesContextMenu
					.item(doc.getName() == null ? sceneEntry.getKey() : doc
							.getName());

			item.addListener(new ActionOnDownListener(controller,
					ChangeView.class, SceneView.class, sceneEntry.getKey()));

			items.put(scene, item);

			model.addFieldListener(scene, nameListener);
		}

	}

	private class LoadListener implements ModelListener<LoadEvent> {

		@Override
		public void modelChanged(LoadEvent event) {
			switch (event.getType()) {
			case UNLOADED:
				ClickListener clickListener = scenesButton.getClickListener();
				scenesButton.clearListeners();
				scenesButton.addListener(clickListener);
				scenesContextMenu.clear();
				break;
			case LOADED:
				scenesButton
						.addListener(new ActionOnClickListener(controller,
								ShowContextMenu.class, scenesButton,
								scenesContextMenu));
				refreshScenes(event.getModel());
				break;
			}
		}
	}

	private class NameListener implements FieldListener {

		@Override
		public boolean listenToField(FieldName fieldName) {
			return fieldName == FieldName.NAME;
		}

		@Override
		public void modelChanged(FieldEvent event) {
			ContextMenuItem item = items.get(event.getTarget());
			item.label(event.getValue() + "");
		}
	}
}
