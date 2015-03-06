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
package es.eucm.ead.editor.view.builders.scene.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.model.generic.RemoveFromArray;
import es.eucm.ead.editor.control.actions.model.scene.AddComponent;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.view.ModelView;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.builders.scene.context.SceneElementContext;
import es.eucm.ead.engine.gdx.AbstractWidget;
import es.eucm.ead.editor.view.widgets.IconButton;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.components.ModelComponent;
import es.eucm.ead.schema.entities.ModelEntity;

public abstract class ComponentEditor<T extends ModelComponent> extends
		ScrollPane implements ModelView {

	public static final float AREA_CM = 1.0f;

	protected Controller controller;

	protected I18N i18N;

	protected Skin skin;

	private String componentId;

	private String icon;

	protected LinearLayout list;

	public ComponentEditor(String icon, String label, String componentId,
			Controller cont) {
		super(new LinearLayout(false));
		setScrollingDisabled(true, false);
		list = (LinearLayout) getWidget();
		this.icon = icon;
		this.controller = cont;
		this.i18N = controller.getApplicationAssets().getI18N();
		this.skin = controller.getApplicationAssets().getSkin();
		this.componentId = componentId;
		list.background(controller.getApplicationAssets().getSkin()
				.getDrawable(SkinConstants.DRAWABLE_PAGE_RIGHT));

		Table header = new Table();
		header.pad(WidgetBuilder.dpToPixels(8), WidgetBuilder.dpToPixels(8), 0,
				0);
		header.add(WidgetBuilder.icon(icon, SkinConstants.STYLE_GRAY));
		header.add(WidgetBuilder.label(label, SkinConstants.STYLE_EDITION))
				.expandX().width(0).fillX();

		IconButton delete = WidgetBuilder.icon(SkinConstants.IC_DELETE,
				SkinConstants.STYLE_EDITION);
		delete.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				ModelEntity modelEntity = (ModelEntity) controller.getModel()
						.getSelection().getSingle(Selection.SCENE_ELEMENT);
				removeComponent(modelEntity);
			}
		});

		header.add(delete).padRight(WidgetBuilder.dpToPixels(8));

		list.add(header).expandX();
		buildContent();

		list.addSpace(WidgetBuilder.dpToPixels(48));

		addListener(new InputListener() {

			private float lastX;

			private float lastY;

			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				lastX = x;
				lastY = y;
				return true;
			}

			@Override
			public void touchDragged(InputEvent event, float x, float y,
					int pointer) {
				if (x > lastX
						&& Math.abs(y - lastY) < AbstractWidget
								.cmToYPixels(AREA_CM)) {
					setCancelTouchFocus(false);
				} else {
					setCancelTouchFocus(true);
				}
			}

			@Override
			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				setCancelTouchFocus(true);
			}
		});
	}

	@Override
	public void prepare() {
		ModelEntity sceneElement = (ModelEntity) controller.getModel()
				.getSelection().getSingle(Selection.SCENE_ELEMENT);
		if (sceneElement != null) {
			T component = (T) Q.getComponentById(sceneElement, componentId);
			if (component == null) {
				component = createComponent(sceneElement);
			}
			try {
				read(sceneElement, component);
			} catch (Exception e) {
				Gdx.app.error(
						"ComponentEditor",
						"Component impossible to read. Replaced with a fresh one",
						e);
				sceneElement.getComponents().removeValue(component, true);
				read(sceneElement, createComponent(sceneElement));
			}
		}
	}

	private T createComponent(ModelEntity sceneElement) {
		T component = buildNewComponent();
		component.setId(componentId);
		controller.action(AddComponent.class, sceneElement, component);
		return component;
	}

	@Override
	public void release() {
	}

	protected void removeComponent(ModelEntity modelEntity) {
		ModelComponent component = Q.getComponentById(modelEntity,
				getComponentId());
		controller.action(RemoveFromArray.class, modelEntity,
				modelEntity.getComponents(), component);
		((SceneElementContext) getParent()).closeEditor();
	}

	/**
	 * Build the UI insided the component editor
	 */
	protected abstract void buildContent();

	protected abstract void read(ModelEntity entity, T component);

	/**
	 * @return a new component edited by this component. Id for this component
	 *         will be set automatically to {@link #componentId}
	 */
	protected abstract T buildNewComponent();

	@Override
	public float getPrefWidth() {
		return Math.max(super.getPrefWidth(), WidgetBuilder.dpToPixels(200));
	}

	public String getComponentId() {
		return componentId;
	}

	public String getIcon() {
		return icon;
	}

	public String getTooltip() {
		return null;
	}
}
