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
package es.eucm.ead.editor.view.widgets.editionview.elementcontext;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import es.eucm.ead.editor.assets.ApplicationAssets;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.editor.LaunchExternalEditor;
import es.eucm.ead.editor.control.actions.model.Clone;
import es.eucm.ead.editor.control.actions.model.RemoveSceneElementSelection;
import es.eucm.ead.editor.control.actions.model.ReplaceEntity;
import es.eucm.ead.editor.control.actions.model.SetSelection;
import es.eucm.ead.editor.control.actions.model.scene.ReorderSelection;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.view.widgets.IconButton;
import es.eucm.ead.editor.view.widgets.ToolbarIcon;
import es.eucm.ead.editor.view.widgets.editionview.MockupSceneEditor;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.components.controls.Label;
import es.eucm.ead.schema.editor.components.Parent;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schema.renderers.Image;

public class ElementContext extends Table {

	private static final String IC_DELETE = "remove80x80",
			IC_DUPLICATE = "clone80x80", IC_TOFRONT = "tofront80x80",
			IC_TOBACK = "toback80x80", IC_IAMGEEFFECTS = "effects80x80",
			IC_CHANGETEXT = "pencil80x80";

	private static final float ROTATION_HANDLE_SPACE = 50f;
	private static final float X_SPACE = 10f;
	private static final Vector2 TEMP = new Vector2();
	private static final float ICON_SIZE = .065F;
	private static final float PAD_SIZE = .01F;

	private Table commonContext;

	private Table labelContext;

	private Table imageContext;

	private MockupSceneEditor sceneEditor;

	public ElementContext(final Controller controller,
			MockupSceneEditor sceneEditor) {
		this.sceneEditor = sceneEditor;
		float viewportHeight = controller.getPlatform().getSize().y;
		float iconSize = viewportHeight * ICON_SIZE;
		float pad = viewportHeight * PAD_SIZE;
		ApplicationAssets assets = controller.getApplicationAssets();
		Skin skin = assets.getSkin();

		// Common context
		final IconButton toFront = new ToolbarIcon(IC_TOFRONT, pad, iconSize,
				skin, "white_left");
		final IconButton toBack = new ToolbarIcon(IC_TOBACK, pad, iconSize,
				skin, "white_center");
		final IconButton duplicate = new ToolbarIcon(IC_DUPLICATE, pad,
				iconSize, skin, "white_center");
		final ToolbarIcon delete = new ToolbarIcon(IC_DELETE, pad, iconSize,
				skin, "white_right");
		delete.getIcon().setColor(Color.WHITE);

		commonContext = new ContextBar(skin, toFront, toBack, duplicate, delete);

		// Image context
		final IconButton imageEffects = new ToolbarIcon(IC_IAMGEEFFECTS, pad,
				iconSize, skin, "white_single");

		imageContext = new ContextBar(skin, imageEffects);

		// Label context
		final IconButton changeText = new ToolbarIcon(IC_CHANGETEXT, pad,
				iconSize, skin, "white_single");

		labelContext = new ContextBar(skin, changeText);

		final TextInputListener changeTextListener = new TextInputListener() {

			@Override
			public void input(String text) {
				if (text != null && !text.isEmpty() && !text.trim().isEmpty()) {
					Object elemObj = controller.getModel().getSelection()
							.getSingle(Selection.SCENE_ELEMENT);
					if (elemObj instanceof ModelEntity) {
						ModelEntity element = (ModelEntity) elemObj;
						ModelEntity copy = controller.getEditorGameAssets()
								.copy(element);

						Q.getComponent(copy, Parent.class).setParent(
								Q.getComponent(element, Parent.class)
										.getParent());
						Label labelComponent = Q
								.getComponent(copy, Label.class);
						labelComponent.setText(text);
						controller.action(ReplaceEntity.class, element, copy);
						controller.action(SetSelection.class,
								Selection.EDITED_GROUP,
								Selection.SCENE_ELEMENT, copy);
					}
				}
			}

			@Override
			public void canceled() {

			}
		};

		final I18N i18n = assets.getI18N();
		ChangeListener listener = new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Actor listener = event.getListenerActor();
				if (listener == imageEffects) {
					controller.action(LaunchExternalEditor.class);
				} else if (listener == toFront) {
					controller.action(ReorderSelection.class,
							ReorderSelection.Type.TO_FRONT);
				} else if (listener == toBack) {
					controller.action(ReorderSelection.class,
							ReorderSelection.Type.TO_BACK);
				} else if (listener == delete) {
					controller.action(RemoveSceneElementSelection.class);
				} else if (listener == duplicate) {
					controller.action(Clone.class);
				} else if (listener == changeText) {
					Object elemObj = controller.getModel().getSelection()
							.getSingle(Selection.SCENE_ELEMENT);
					if (elemObj instanceof ModelEntity) {

						ModelEntity elem = (ModelEntity) elemObj;
						if (Q.hasComponent(elem, Label.class)) {
							Gdx.input
									.getTextInput(changeTextListener, i18n
											.m("edition.newText"), Q
											.getComponent(elem, Label.class)
											.getText());
						}
					}
				}
			}
		};
		imageEffects.addListener(listener);
		toFront.addListener(listener);
		toBack.addListener(listener);
		delete.addListener(listener);
		duplicate.addListener(listener);
		changeText.addListener(listener);

	}

	public void show(ModelEntity entity, Actor actor) {
		if (entity == null && actor == null && !hasParent()) {
			return;
		}
		remove();
		clear();
		labelContext.remove();
		imageContext.remove();
		add(commonContext).left();
		if (actor != null) {
			Stage stage = actor.getStage();
			if (stage != null) {

				WidgetGroup secondContext = getContext(entity);
				if (secondContext != null) {
					row();
					add(secondContext).left();
				}

				float prefW = getPrefWidth();
				float prefH = getPrefHeight();

				float minX = Float.POSITIVE_INFINITY;
				float minY = Float.POSITIVE_INFINITY;
				float maxX = Float.NEGATIVE_INFINITY;
				float maxY = Float.NEGATIVE_INFINITY;

				actor.localToStageCoordinates(TEMP.set(0, 0));
				float x1 = TEMP.x, y1 = TEMP.y;
				actor.localToStageCoordinates(TEMP.set(actor.getWidth(), 0));
				float x2 = TEMP.x, y2 = TEMP.y;
				actor.localToStageCoordinates(TEMP.set(0, actor.getHeight()));
				float x3 = TEMP.x, y3 = TEMP.y;
				actor.localToStageCoordinates(TEMP.set(actor.getWidth(),
						actor.getHeight()));
				float x4 = TEMP.x, y4 = TEMP.y;

				minX = Math.min(minX,
						Math.min(x1, Math.min(x2, Math.min(x3, x4))));
				minY = Math.min(minY,
						Math.min(y1, Math.min(y2, Math.min(y3, y4))));
				maxX = Math.max(maxX,
						Math.max(x1, Math.max(x2, Math.max(x3, x4))));
				maxY = Math.max(maxY,
						Math.max(y1, Math.max(y2, Math.max(y3, y4))));

				float actorX = minX;
				float actorY = minY;
				float actorW = maxX - minX;
				float actorH = maxY - minY;

				float sceneW = sceneEditor.getWidth();
				float sceneH = sceneEditor.getHeight();
				sceneEditor.localToStageCoordinates(TEMP.set(0f, 0f));
				float sceneX = TEMP.x;
				float sceneY = TEMP.y;

				float y = Math.max(
						0f,
						Math.min(sceneY + sceneH - prefH, actorY
								+ (actorH - prefH) * .5f));
				float rightSpace = X_SPACE;
				float leftSpace = X_SPACE;
				float degrees = actor.getRotation() % 360;
				if (degrees < 0) {
					degrees += 360;
				}
				if (degrees > 240 && degrees < 300) {
					rightSpace += ROTATION_HANDLE_SPACE;
				} else if (degrees > 60 && degrees < 120) {
					leftSpace += ROTATION_HANDLE_SPACE;
				}
				float x;
				if (actorX + actorW + prefW + rightSpace < sceneX + sceneW) {
					// Positioning to the right

					x = actorX + actorW + rightSpace;
				} else if (actorX - prefW - leftSpace > sceneX) {
					// Positioning to the left
					x = actorX - prefW - leftSpace;
				} else {
					// Positioning to the right but inside the actor's bounds
					x = Math.min(sceneX + sceneW, actorX + actorW);
					x = x - X_SPACE - prefW;
				}
				setBounds(x, y, prefW, prefH);
				stage.addActor(this);
			}
		}
	}

	private WidgetGroup getContext(ModelEntity entity) {
		WidgetGroup ret = null;
		if (Q.hasComponent(entity, Image.class)) {
			ret = imageContext;
		} else if (Q.hasComponent(entity, Label.class)) {
			ret = labelContext;
		}
		return ret;
	}
}
