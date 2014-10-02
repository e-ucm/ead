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

import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.assets.ApplicationAssets;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.MockupViews;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.editor.Copy;
import es.eucm.ead.editor.control.actions.editor.LaunchExternalEditor;
import es.eucm.ead.editor.control.actions.model.ChangeSelectionText;
import es.eucm.ead.editor.control.actions.model.RemoveSceneElementSelection;
import es.eucm.ead.editor.control.actions.model.scene.ReorderSelection;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.platform.MockupPlatform;
import es.eucm.ead.editor.view.widgets.DropDown;
import es.eucm.ead.editor.view.widgets.IconButton;
import es.eucm.ead.editor.view.widgets.editionview.MockupSceneEditor;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.components.controls.Label;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schema.renderers.Image;

public class ElementContext extends Table {

	private static final String IC_DELETE = "remove80x80",
			IC_DUPLICATE = "clone80x80", IC_TOFRONT = "tofront80x80",
			IC_TOBACK = "toback80x80", IC_IAMGEEFFECTS = "effects80x80",
			IC_CHANGETEXT = "pencil80x80";

	private static final float COPY_NOTIFICATION_TIME = 2f;
	private static final float X_SPACE = 10f;
	private static final Vector2 TEMP = new Vector2();

	private Table commonContext;

	private Table labelContext;

	private Table imageContext;

	private MockupSceneEditor sceneEditor;

	private LabelColorPicker colorPicker;

	private Array<Actor> fontTypeActors, fontSizeActors;
	private final DropDown fontType, fontSize;

	public ElementContext(final Controller controller,
			MockupSceneEditor sceneEditor) {
		this.sceneEditor = sceneEditor;
		ApplicationAssets assets = controller.getApplicationAssets();
		Skin skin = assets.getSkin();

		// Common context
		final IconButton toFront = new IconButton(IC_TOFRONT, 0f, skin,
				"white_left");
		final IconButton toBack = new IconButton(IC_TOBACK, 0f, skin,
				"white_center");
		final IconButton copy = new IconButton(IC_DUPLICATE, 0f, skin,
				"white_center");
		final IconButton delete = new IconButton(IC_DELETE, 0f, skin,
				"white_right");
		delete.getIcon().setColor(Color.WHITE);

		commonContext = new ContextBar(skin, toFront, toBack, copy, delete);

		// Image context
		final IconButton imageEffects = new IconButton(IC_IAMGEEFFECTS, 0f,
				skin, "white_single");

		imageContext = new ContextBar(skin, imageEffects);

		// Label context
		final IconButton changeText = new IconButton(IC_CHANGETEXT, 0f, skin,
				"white_single");
		colorPicker = new LabelColorPicker(controller, true, skin, this);

		fontType = new DropDown(skin);
		Actor roboto = new com.badlogic.gdx.scenes.scene2d.ui.Label("Ro", skin);
		roboto.setUserObject("roboto-");
		Actor comfortaa = new com.badlogic.gdx.scenes.scene2d.ui.Label("Co",
				skin);
		comfortaa.setUserObject("comfortaa-");
		Actor mockup = new com.badlogic.gdx.scenes.scene2d.ui.Label("Ra", skin);
		mockup.setUserObject("rabanera-");
		fontTypeActors = new Array<Actor>(3);
		fontTypeActors.add(roboto);
		fontTypeActors.add(comfortaa);
		fontTypeActors.add(mockup);
		fontType.setItems(fontTypeActors);

		fontSize = new DropDown(skin);
		Actor big = new com.badlogic.gdx.scenes.scene2d.ui.Label("Big", skin);
		big.setUserObject("big");
		Actor small = new com.badlogic.gdx.scenes.scene2d.ui.Label("Small",
				skin);
		small.setUserObject("small");
		fontSizeActors = new Array<Actor>(2);
		fontSizeActors.add(big);
		fontSizeActors.add(small);
		fontSize.setItems(fontSizeActors);

		ChangeListener dropdownChangeListener = new DropDown.DropdownChangeListener() {
			@Override
			public void changed(Actor selected, DropDown listener) {
				String newStyle = null;
				if (listener == fontType) {
					newStyle = getSelectedStyle(selected,
							fontSize.getSelected());
				} else {
					newStyle = getSelectedStyle(fontType.getSelected(),
							selected);
				}
				controller.action(ChangeSelectionText.class, newStyle, false);
			}
		};
		fontType.addListener(dropdownChangeListener);
		fontSize.addListener(dropdownChangeListener);

		labelContext = new ContextBar(skin, changeText, colorPicker, fontType,
				fontSize);

		final TextInputListener changeTextListener = new TextInputListener() {

			@Override
			public void input(String text) {
				if (text != null && !text.isEmpty() && !text.trim().isEmpty()) {
					controller.action(ChangeSelectionText.class, text);
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
				} else if (listener == copy) {
					controller.action(Copy.class);
					((MockupViews) controller.getViews()).getToasts()
							.showNotification(i18n.m("edition.copied"),
									COPY_NOTIFICATION_TIME);
				} else if (listener == changeText) {
					Object elemObj = controller.getModel().getSelection()
							.getSingle(Selection.SCENE_ELEMENT);
					if (elemObj instanceof ModelEntity) {

						ModelEntity elem = (ModelEntity) elemObj;
						if (Q.hasComponent(elem, Label.class)) {
							MockupPlatform platform = (MockupPlatform) controller
									.getPlatform();
							platform.getMultilineTextInput(
									changeTextListener,
									i18n.m("edition.newText"),
									Q.getComponent(elem, Label.class).getText(),
									i18n);
						}
					}
				}
			}
		};
		imageEffects.addListener(listener);
		toFront.addListener(listener);
		toBack.addListener(listener);
		delete.addListener(listener);
		copy.addListener(listener);
		changeText.addListener(listener);

	}

	private String getSelectedStyle(Actor fontType, Actor fontSize) {
		return fontType.getUserObject().toString()
				+ fontSize.getUserObject().toString();
	}

	private void initFontSizeWidget(String styleName) {
		for (Actor actor : fontSizeActors) {
			if (styleName.endsWith(actor.getUserObject().toString())) {
				fontSize.setSelected(actor);
				break;
			}
		}
	}

	private void initFontTypeWidget(String styleName) {
		for (Actor actor : fontTypeActors) {
			if (styleName.startsWith(actor.getUserObject().toString())) {
				fontType.setSelected(actor);
				break;
			}
		}
	}

	public void show(ModelEntity entity, Actor actor) {
		if (entity == null && actor == null && !hasParent()) {
			return;
		}
		if (actor != null) {
			clearContext();
			add(commonContext).left();
			Stage stage = actor.getStage();
			if (stage != null) {

				WidgetGroup secondContext = getContext(entity);
				if (secondContext != null) {
					row();
					add(secondContext).left();
					if (secondContext == labelContext) {
						boolean hasText = false;
						for (Actor child : ((Group) actor).getChildren()) {
							if (child instanceof com.badlogic.gdx.scenes.scene2d.ui.Label) {
								colorPicker.setVisible(true);
								colorPicker
										.setLabel(((com.badlogic.gdx.scenes.scene2d.ui.Label) child));
								hasText = true;
								break;
							}
						}
						if (!hasText) {
							colorPicker.setVisible(false);
						}

						String styleName = Q.getComponent(entity, Label.class)
								.getStyle();
						initFontTypeWidget(styleName);
						initFontSizeWidget(styleName);
					}
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

				x = MathUtils.round(x);
				y = MathUtils.round(y);
				prefW = MathUtils.round(prefW);
				prefH = MathUtils.round(prefH);

				float currX = getX(), currY = getY(), currH = getHeight();

				if (!MathUtils.isEqual(currX, x)
						|| !MathUtils.isEqual(currY, y)
						|| !MathUtils.isEqual(currH, prefH)) {

					clearContext();
					setBounds(x, y, prefW, prefH);
					add(commonContext).left();
					row();
					add(secondContext).left();
					stage.addActor(this);
				}
			}
		} else if (entity == null) {
			clearContext();
		}
	}

	private void clearContext() {
		remove();
		clear();
		labelContext.remove();
		imageContext.remove();
		setPosition(-1000f, -1000f);
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
