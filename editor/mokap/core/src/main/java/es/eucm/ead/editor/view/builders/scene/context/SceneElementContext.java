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
package es.eucm.ead.editor.view.builders.scene.context;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ObjectMap;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.Model.SelectionListener;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.model.events.ListEvent;
import es.eucm.ead.editor.model.events.SelectionEvent;
import es.eucm.ead.editor.view.ModelView;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.builders.scene.interaction.ComponentEditor;
import es.eucm.ead.editor.view.widgets.IconButton;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.schema.components.ModelComponent;
import es.eucm.ead.schema.entities.ModelEntity;

public abstract class SceneElementContext extends GroupContext implements
		ModelView, SelectionListener {

	protected Controller controller;

	private ObjectMap<String, IconButton> buttons = new ObjectMap<String, IconButton>();

	private ComponentsListener componentsListener = new ComponentsListener();

	private ClickListener openPanel = new ClickListener() {
		@Override
		public void clicked(InputEvent event, float x, float y) {
			((Button) event.getTarget()).setChecked(true);
			Object object = event.getTarget().getUserObject();
			if (object instanceof ComponentEditor) {
				openEditor((ComponentEditor) object);
			}
		}
	};

	private ComponentEditor currentComponentEditor;

	private ImageButton closeButton;

	public SceneElementContext(Controller controller, Skin skin) {
		super(skin);
		this.controller = controller;
		closeButton = new ImageButton(skin, SkinConstants.STYLE_CHECK);
		closeButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				closeEditor();
			}
		});
		closeButton.setVisible(false);
		addActor(closeButton);

		addContent();
	}

	/**
	 * Add content to the context
	 */
	protected abstract void addContent();

	protected void addComponentEditor(ComponentEditor componentEditor) {
		String componentId = componentEditor.getComponentId();
		String icon = componentEditor.getIcon();

		IconButton iconButton = WidgetBuilder.icon(icon,
				componentEditor.getTooltip(), SkinConstants.STYLE_COMPONENT);
		iconButton.setUserObject(componentEditor);
		iconButton.setName(componentId);
		iconButton.addListener(openPanel);

		gallery.add(iconButton);
		buttons.put(componentId, iconButton);
	}

	public void closeEditor() {
		if (currentComponentEditor != null) {
			closeButton.setVisible(false);
			gallery.setTouchable(Touchable.enabled);
			currentComponentEditor.release();
			currentComponentEditor
					.addAction(Actions.sequence(Actions
							.touchable(Touchable.disabled), Actions.moveTo(
							getWidth(), 0, 0.27f, Interpolation.exp5Out)));
		}
	}

	private void openEditor(ComponentEditor componentEditor) {
		currentComponentEditor = componentEditor;
		currentComponentEditor.prepare();
		setBounds(currentComponentEditor, getWidth(), 0, getWidth(),
				getHeight());
		addActor(currentComponentEditor);
		closeButton.toFront();
		gallery.setTouchable(Touchable.disabled);
		currentComponentEditor.addAction(Actions.sequence(
				Actions.touchable(Touchable.enabled),
				Actions.moveTo(0, 0, 0.33f, Interpolation.exp5Out)));
		closeButton.addAction(Actions.sequence(Actions.delay(0.33f),
				Actions.alpha(0), Actions.visible(true),
				Actions.alpha(1.0f, 0.45f, Interpolation.exp5Out)));
	}

	@Override
	public void prepare() {
		readSceneElement();
		controller.getModel().addSelectionListener(this);
	}

	@Override
	public void release() {
		controller.getModel().removeListenerFromAllTargets(componentsListener);
		controller.getModel().removeSelectionListener(this);
	}

	@Override
	public boolean listenToContext(String contextId) {
		return Selection.SCENE_ELEMENT.equals(contextId);
	}

	@Override
	public void modelChanged(SelectionEvent event) {
		readSceneElement();
	}

	private void readSceneElement() {
		ModelEntity sceneElement = (ModelEntity) controller.getModel()
				.getSelection().getSingle(Selection.SCENE_ELEMENT);
		clearIcons();
		closeEditor();
		if (sceneElement != null) {
			gallery.getGrid().setVisible(true);
			for (IconButton button : buttons.values()) {
				ComponentEditor componentEditor = (ComponentEditor) button
						.getUserObject();
				if (Q.getComponentById(sceneElement,
						componentEditor.getComponentId()) != null) {
					button.setChecked(true);
					addIcon(componentEditor.getComponentId(),
							controller.getApplicationAssets().getSkin()
									.getDrawable(componentEditor.getIcon()));
				} else {
					button.setChecked(false);
				}
			}
			controller.getModel().removeListenerFromAllTargets(
					componentsListener);
			controller.getModel().addListListener(sceneElement.getComponents(),
					componentsListener);
		} else {
			gallery.getGrid().setVisible(false);
		}
	}

	@Override
	public void layout() {
		super.layout();
		float width = getPrefWidth(closeButton);
		setBounds(closeButton,
				getWidth() - width - WidgetBuilder.dpToPixels(16),
				WidgetBuilder.dpToPixels(16), width, getPrefHeight(closeButton));
	}

	public class ComponentsListener implements ModelListener<ListEvent> {

		@Override
		public void modelChanged(ListEvent event) {
			ModelComponent component = (ModelComponent) event.getElement();
			if (buttons.containsKey(component.getId())) {
				IconButton button = buttons.get(component.getId());
				switch (event.getType()) {
				case ADDED:
					button.setChecked(true);
					break;
				case REMOVED:
					button.setChecked(false);
					break;
				}
			}

			if (component.getId() != null) {
				switch (event.getType()) {
				case ADDED:
					addIcon(component.getId(),
							controller
									.getApplicationAssets()
									.getSkin()
									.getDrawable(
											((ComponentEditor) buttons.get(
													component.getId())
													.getUserObject()).getIcon()));
					break;
				case REMOVED:
					Actor actor = iconsList.findActor(component.getId());
					if (actor != null) {
						actor.remove();
					}
					break;
				}
			}
		}
	}

}
