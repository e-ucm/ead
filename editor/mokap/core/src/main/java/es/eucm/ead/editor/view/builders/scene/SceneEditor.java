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
package es.eucm.ead.editor.view.builders.scene;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.MokapController.BackListener;
import es.eucm.ead.editor.control.Preferences;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.Selection.Context;
import es.eucm.ead.editor.control.actions.editor.*;
import es.eucm.ead.editor.control.actions.editor.ShowInfoPanel.TypePanel;
import es.eucm.ead.editor.control.actions.model.AddInteractiveZone;
import es.eucm.ead.editor.control.actions.model.AddSceneElement;
import es.eucm.ead.editor.control.actions.model.SetSelection;
import es.eucm.ead.editor.control.actions.model.TakePicture;
import es.eucm.ead.editor.model.Model.SelectionListener;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.model.events.SelectionEvent;
import es.eucm.ead.editor.platform.Platform.FileChooserListener;
import es.eucm.ead.editor.view.ModelView;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.builders.ResourcesView;
import es.eucm.ead.editor.view.builders.scene.components.InteractionContext;
import es.eucm.ead.editor.view.builders.scene.context.SceneElementContext;
import es.eucm.ead.editor.view.builders.scene.draw.BrushStrokes;
import es.eucm.ead.editor.view.builders.scene.play.TestGameView;
import es.eucm.ead.engine.gdx.AbstractWidget;
import es.eucm.ead.editor.view.widgets.CirclesMenu;
import es.eucm.ead.editor.view.widgets.MultiWidget;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.editor.view.widgets.baseview.BaseView;
import es.eucm.ead.editor.view.widgets.baseview.Navigation;
import es.eucm.ead.engine.EntitiesLoader;
import es.eucm.ead.schema.editor.components.GameData;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.Layer;

public class SceneEditor extends BaseView implements ModelView,
		SelectionListener, BackListener, FileChooserListener {

	public static final float ANIMATION_TIME = 0.3f;

	public static final int INSERT = 0, PAINT = 2;

	public enum Mode {
		COMPOSE, FX, INTERACTION, PLAY, DRAW,
	}

	private Controller controller;

	private Selection selection;

	private EntitiesLoader entitiesLoader;

	private Mode mode;

	private Mode oldMode;

	private MultiWidget toolbar;

	private SceneGroupEditor sceneGroupEditor;

	private TestGameView gameView;

	private BrushStrokes brushStrokes;

	private InteractionContext interactionContext;

	private Button addButton;

	private Button interactiveButton;

	public SceneEditor(Controller controller) {
		super(controller.getApplicationAssets().getSkin());
		this.controller = controller;
		this.selection = controller.getModel().getSelection();
		this.entitiesLoader = controller.getEngine().getEntitiesLoader();

		mode = Mode.COMPOSE;

		setNavigation(new NavigationGallery(controller));

		AbstractWidget container = new AbstractWidget();
		sceneGroupEditor = new SceneGroupEditor(controller, this);
		sceneGroupEditor.setFillParent(true);
		brushStrokes = new BrushStrokes(controller, container, this);
		gameView = new TestGameView(controller.getEngine().getGameLoop());
		gameView.setVisible(false);

		container.addActor(sceneGroupEditor);
		container.addActor(gameView);

		interactiveButton = WidgetBuilder.imageButton(SkinConstants.IC_TOUCH,
				SkinConstants.STYLE_CIRCLE);
		interactiveButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				selectionContext.show();
			}
		});
		interactiveButton.setTransform(true);
		interactiveButton.pack();
		interactiveButton.setOrigin(interactiveButton.getWidth() / 2,
				interactiveButton.getHeight() / 2);
		interactiveButton.setScale(0);
		interactiveButton.setVisible(false);
		container.addActor(interactiveButton);

		CirclesMenu circlesMenu = buildAddButtons();
		addButton = WidgetBuilder.button(SkinConstants.STYLE_ADD);
		addButton.pack();
		circlesMenu.pack();
		WidgetBuilder.actionOnClick(
				addButton,
				ShowContextMenu.class,
				addButton,
				circlesMenu,
				-circlesMenu.getWidth() + addButton.getWidth()
						+ WidgetBuilder.dpToPixels(8), 0f);
		container.addActor(addButton);

		setToolbar(toolbar = new GroupEditorToolbar(controller, this,
				brushStrokes));
		setContent(container);

		interactionContext = new InteractionContext(controller, controller
				.getApplicationAssets().getSkin());
		setContext(interactionContext);
		lockContextOnly(true);
	}

	@Override
	protected Navigation buildNavigation(BaseViewStyle style) {
		return new Navigation(style) {
			@Override
			public void show() {
				super.show();
				createSceneThumbnail();
			}
		};
	}

	public SceneGroupEditor getGroupEditor() {
		return sceneGroupEditor;
	}

	@Override
	public void layout() {
		super.layout();
		addButton.setPosition(
				getWidth() - addButton.getWidth()
						- WidgetBuilder.dpToPixels(32),
				WidgetBuilder.dpToPixels(32));
		interactiveButton.setPosition(
				getWidth() - addButton.getWidth()
						- WidgetBuilder.dpToPixels(32),
				addButton.getY() + addButton.getPrefHeight()
						+ WidgetBuilder.dpToPixels(32));
	}

	@Override
	public void prepare() {
		setMode(mode);
		controller.getEngine().setGameView(gameView);
		controller.getModel().addSelectionListener(this);

		GameData gameData = Q.getComponent(controller.getModel().getGame(),
				GameData.class);
		gameView.updateWorldSize(gameData.getWidth(), gameData.getHeight());
	}

	@Override
	public void release() {
		createSceneThumbnail();
		controller.getModel().removeSelectionListener(this);
	}

	private void createSceneThumbnail() {
		controller.action(CreateSceneThumbnail.class, getGroupEditor()
				.getRootGroup());
	}

	@Override
	public boolean listenToContext(String contextId) {
		return Selection.SCENE_ELEMENT.equals(contextId);
	}

	@Override
	public void modelChanged(SelectionEvent event) {
		interactiveButton.clearActions();
		if (selection.get(Selection.SCENE_ELEMENT).length != 1) {
			lockContextOnly(true);
			interactiveButton
					.addAction(Actions.sequence(Actions
							.touchable(Touchable.disabled), Actions.scaleTo(0,
							0, ANIMATION_TIME, Interpolation.sineIn), Actions
							.visible(false)));
		} else {
			lockContextOnly(false);
			interactiveButton
					.addAction(Actions.sequence(Actions.visible(true),
							Actions.scaleTo(1, 1, ANIMATION_TIME,
									Interpolation.sineIn), Actions
									.touchable(Touchable.enabled)));
		}
		setMode(mode);
	}

	@Override
	public boolean onBackPressed() {
		if (isNavigationVisible()) {
			toggleNavigation();
			return true;
		} else if (mode == Mode.PLAY) {
			setMode(oldMode);
			return true;
		} else if (mode == Mode.DRAW) {
			setMode(Mode.COMPOSE);
			brushStrokes.hide(true);
			return true;
		}
		return false;
	}

	public void setMode(Mode mode) {
		if (this.mode != mode) {
			unsetMode(this.mode);
		}
		this.oldMode = this.mode;
		this.mode = mode;
		Context context = selection.getContext(Selection.SCENE_ELEMENT);
		boolean somethingSelected = context != null
				&& context.getSelection().length > 0;
		switch (mode) {
		case COMPOSE:
			setContext(interactionContext);
			lockPanels(false);
			toolbar.setSelectedWidget(INSERT + (somethingSelected ? 1 : 0));
			sceneGroupEditor.setOnlySelection(false);
			controller.action(ShowInfoPanel.class, TypePanel.COMPOSE,
					Preferences.HELP_MODE_COMPOSE);
			break;
		case DRAW:
			if (selection.getSingle(Selection.SCENE_ELEMENT) != null) {
				controller.action(SetSelection.class, Selection.EDITED_GROUP,
						Selection.SCENE_ELEMENT);
			}
			addButton.setVisible(false);
			lockPanels(true);
			toolbar.setSelectedWidget(PAINT);
			brushStrokes.show();
			sceneGroupEditor.getEditionButtons().setVisible(false);
			break;
		case PLAY:
			addButton.setVisible(false);
			lockPanels(true);
			enterFullScreen();
			gameView.setVisible(true);
			sceneGroupEditor.setVisible(false);
			controller.getEngine().play();
			sceneGroupEditor.release();

			ModelEntity scene = (ModelEntity) selection
					.getSingle(Selection.SCENE);
			gameView.clearLayer(Layer.SCENE_CONTENT, true);
			gameView.addEntityToLayer(Layer.SCENE_CONTENT,
					entitiesLoader.toEngineEntity(scene));

			controller.action(ShowToast.class, controller
					.getApplicationAssets().getI18N().m("play.back"));

			controller.action(ShowInfoPanel.class, TypePanel.PLAY,
					Preferences.HELP_MODE_PLAY);
			break;
		}
	}

	private void unsetMode(Mode mode) {
		switch (mode) {
		case COMPOSE:
			setSelectionContext(null);
			interactionContext.release();
			break;
		case PLAY:
			addButton.setVisible(true);
			controller.getEngine().stop();
			exitFullScreen();
			gameView.setVisible(false);
			sceneGroupEditor.setVisible(true);
			sceneGroupEditor.prepare();
			break;
		case DRAW:
			addButton.setVisible(true);
			sceneGroupEditor.getEditionButtons().setVisible(true);
			break;
		}
	}

	private void setContext(SceneElementContext context) {
		if (this.mode != this.oldMode) {
			controller.getCommands().pushStack();
			context.prepare();
			setSelectionContext(context);
			sceneGroupEditor.setOnlySelection(true);
		}
	}

	@Override
	protected void layoutSelectionContext() {
		super.layoutSelectionContext();
		float height = getHeight() + toolbar.getBackground().getBottomHeight()
				- (toolbar == null ? 0 : getPrefHeight(toolbar));
		selectionContext.setHeight(height);
	}

	private CirclesMenu buildAddButtons() {
		CirclesMenu circlesMenu = WidgetBuilder.circlesMenu(Align.right,
				new String[] { SkinConstants.IC_ZONE, SkinConstants.IC_BRUSH,
						SkinConstants.IC_TEXT, SkinConstants.IC_CAMERA,
						SkinConstants.IC_PHOTO, SkinConstants.IC_CLOUD,
						SkinConstants.IC_CLOSE }, new Class[] {
						AddInteractiveZone.class, null, AddLabel.class,
						TakePicture.class, ChooseFile.class, ChangeView.class,
						null }, new Object[][] { null, null, null, null,
						new Object[] { false, this },
						new Object[] { ResourcesView.class }, null });

		Actor zone = circlesMenu.findActor(SkinConstants.IC_ZONE);
		WidgetBuilder.actionOnClick(zone, ShowInfoPanel.class, TypePanel.ZONES,
				Preferences.HELP_ZONES);

		Actor paint = circlesMenu.findActor(SkinConstants.IC_BRUSH);
		paint.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				setMode(Mode.DRAW);
			}
		});

		return circlesMenu;
	}

	@Override
	public void fileChosen(String path, Result result) {
		if (path != null && !path.trim().isEmpty()) {
			addElement(path);
		}
	}

	private void addElement(String elemPath) {
		ModelEntity sceneElement = controller.getTemplates()
				.createSceneElement(elemPath, false);
		controller.action(AddSceneElement.class, sceneElement);
	}

}
