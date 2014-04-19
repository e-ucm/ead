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
package es.eucm.ead.editor.view.builders.mockup.menu;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.forever;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.TimeUtils;
import com.esotericsoftware.tablelayout.Cell;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.editor.ChangeView;
import es.eucm.ead.editor.control.actions.editor.Save;
import es.eucm.ead.editor.control.actions.model.ChangeProjectTitle;
import es.eucm.ead.editor.control.background.BackgroundExecutor;
import es.eucm.ead.editor.control.background.BackgroundExecutor.BackgroundTaskListener;
import es.eucm.ead.editor.control.background.BackgroundTask;
import es.eucm.ead.editor.model.FieldNames;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Model.FieldListener;
import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.events.FieldEvent;
import es.eucm.ead.editor.model.events.LoadEvent;
import es.eucm.ead.editor.view.builders.ViewBuilder;
import es.eucm.ead.editor.view.builders.mockup.camera.Picture;
import es.eucm.ead.editor.view.builders.mockup.camera.Video;
import es.eucm.ead.editor.view.builders.mockup.gallery.ElementGallery;
import es.eucm.ead.editor.view.builders.mockup.gallery.Gallery;
import es.eucm.ead.editor.view.builders.mockup.gallery.SceneGallery;
import es.eucm.ead.editor.view.listeners.ActionForTextFieldListener;
import es.eucm.ead.editor.view.listeners.ChangeNoteFieldListener;
import es.eucm.ead.editor.view.widgets.mockup.Options;
import es.eucm.ead.editor.view.widgets.mockup.buttons.BottomProjectMenuButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.IconButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.MenuButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.MenuButton.Position;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.editor.components.Note;
import es.eucm.ead.schema.editor.game.EditorGame;

public class ProjectScreen implements ViewBuilder {

	public static final String NAME = "mockup_project";
	private static final String IC_EDITELEMENT = "ic_element",
			IC_EDITSTAGE = "ic_scene", IC_PLAYGAME = "ic_playgame",
			IC_GALLERY = "ic_gallery", IC_PHOTOCAMERA = "ic_photocamera",
			IC_VIDEOCAMERA = "ic_videocamera", IC_GO_BACK = "ic_goback";

	private static final float INITIALSCENEBUTTON_FONT_SCALE = .6F;
	private static final float PREF_BOTTOM_BUTTON_WIDTH = .25F;
	private static final float PREF_BOTTOM_BUTTON_HEIGHT = .2F;
	private static final float TEXT_WIDTH_SCALAR = 1.4F;
	/**
	 * Saving interval in seconds.
	 */
	private static final float SAVE_DELAY = 30f;

	private static final int MAX_PROJ_TITLE_CHARACTERS = 30;
	private TextField projectTitleField;
	/**
	 * Cell that holds the {@link projectTitleField} TextField. Used to change
	 * its size when we change project's title.
	 */
	private Cell<?> projectTitleCell;
	private BottomProjectMenuButton initialSceneButton;
	private I18N i18n;
	private boolean addListeners = true, updateInitialSceneName;
	private Controller controller;
	private boolean saving = false;

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public Actor build(final Controller controller) {
		this.controller = controller;
		final Skin skin = controller.getApplicationAssets().getSkin();
		i18n = controller.getApplicationAssets().getI18N();
		final Vector2 viewport = controller.getPlatform().getSize();

		final Button backButton = new IconButton(viewport, skin, IC_GO_BACK,
				controller, ChangeView.class, InitialScreen.NAME);

		this.projectTitleField = new TextField("", skin);
		final String msg = i18n.m("project.untitled");
		this.projectTitleField.setMessageText(msg);
		this.projectTitleField.setMaxLength(MAX_PROJ_TITLE_CHARACTERS);
		this.projectTitleField
				.setTextFieldListener(new ActionForTextFieldListener(
						new ActionForTextFieldListener.TextChangedListener() {
							@Override
							public void onTextChanged() {
								resizeTextField(skin);
							}
						}, controller, ChangeProjectTitle.class));
		final Table topLeftWidgets = new Table().left().top().debug();
		topLeftWidgets.setFillParent(true);
		topLeftWidgets.add(backButton);
		this.projectTitleCell = topLeftWidgets
				.add(this.projectTitleField)
				.width(skin.getFont("default-font").getBounds(msg).width
						* TEXT_WIDTH_SCALAR).expandX().left();

		final Button scene, element, play, gallery, takePictureButton, recordVideoButton;

		scene = new MenuButton(viewport, i18n.m("general.mockup.scenes"), skin,
				IC_EDITSTAGE, Position.BOTTOM, controller, ChangeView.class,
				SceneGallery.NAME);
		element = new MenuButton(viewport, i18n.m("general.mockup.elements"),
				skin, IC_EDITELEMENT, Position.BOTTOM, controller,
				ChangeView.class, ElementGallery.NAME);
		gallery = new MenuButton(viewport, i18n.m("general.mockup.gallery"),
				skin, IC_GALLERY, Position.BOTTOM, controller,
				ChangeView.class, Gallery.NAME);
		play = new MenuButton(viewport, i18n.m("general.mockup.play"), skin,
				IC_PLAYGAME, Position.BOTTOM);

		takePictureButton = new BottomProjectMenuButton(viewport,
				i18n.m("general.mockup.photo"), skin, IC_PHOTOCAMERA,
				PREF_BOTTOM_BUTTON_WIDTH, PREF_BOTTOM_BUTTON_HEIGHT,
				Position.BOTTOM, controller, ChangeView.class, Picture.NAME);
		initialSceneButton = new BottomProjectMenuButton(viewport,
				i18n.m("general.mockup.initial-scene"), skin, "icon-blitz",
				PREF_BOTTOM_BUTTON_WIDTH * 1.8f, PREF_BOTTOM_BUTTON_HEIGHT,
				Position.BOTTOM);
		initialSceneButton.getLabel().setFontScale(
				INITIALSCENEBUTTON_FONT_SCALE);

		recordVideoButton = new BottomProjectMenuButton(viewport,
				i18n.m("general.mockup.video"), skin, IC_VIDEOCAMERA,
				PREF_BOTTOM_BUTTON_WIDTH, PREF_BOTTOM_BUTTON_HEIGHT,
				Position.BOTTOM, controller, ChangeView.class, Video.NAME);
		final Table bottomButtons = new Table().debug().bottom();
		bottomButtons.setFillParent(true);
		bottomButtons.add(takePictureButton);
		bottomButtons.add(initialSceneButton).expandX();
		bottomButtons.add(recordVideoButton);

		this.updateInitialSceneName = true;
		final Options opt = new Options(viewport, controller, skin);

		final Table window = new Table().debug();
		window.setFillParent(true);
		window.addActor(topLeftWidgets);
		window.row();
		window.add(scene, element, gallery, play);
		window.row();
		window.addActor(bottomButtons);
		window.addActor(opt);
		return window;
	}

	private void addModelLoadedListenerListener(final Controller controller) {
		controller.getModel().addLoadListener(new ModelListener<LoadEvent>() {
			@Override
			public void modelChanged(LoadEvent event) {
				addInitialSceneListener(controller);
			}
		});
	}

	private void addInitialSceneListener(final Controller controller) {
		final Model model = controller.getModel();
		final EditorGame game = model.getGame();
		model.addFieldListener(game, new FieldListener() {

			@Override
			public void modelChanged(FieldEvent event) {
				Note note = model.getScenes().get(game.getInitialScene())
						.getNotes();
				changeInitialSceneText(note);
				addInitialSceneNoteListener(controller);
			}

			@Override
			public boolean listenToField(FieldNames fieldName) {
				return fieldName == FieldNames.INITIAL_SCENE;
			}

		});
	}

	private void addInitialSceneNoteListener(Controller controller) {
		final Model model = controller.getModel();
		Note targetNote = model.getScenes()
				.get(model.getGame().getInitialScene()).getNotes();

		model.addFieldListener(targetNote, new ChangeNoteFieldListener() {

			@Override
			public void descriptionChanged(FieldEvent event) {

			}

			@Override
			public void titleChanged(FieldEvent event) {
				Note note = model.getScenes()
						.get(model.getGame().getInitialScene()).getNotes();
				changeInitialSceneText(note);
			}
		});
	}

	private void changeInitialSceneText(Note note) {
		String newText = i18n.m("general.mockup.initial-scene") + ": ";
		String newTitle = note.getTitle();
		if (note == null || newTitle == null || newTitle.isEmpty()) {
			newText += i18n.m("scene") + " " + i18n.m("untitled");
		} else {
			newText += newTitle;
		}
		initialSceneButton.getLabel().setText(newText);
	}

	private void resizeTextField(Skin skin) {
		// Now we resize the text field to match it's new text
		String newTitle = this.projectTitleField.getText();
		if (newTitle.isEmpty()) {
			newTitle = this.projectTitleField.getMessageText();
		}
		this.projectTitleField.setCursorPosition(0);
		final float newWidth = skin.getFont("default-font").getBounds(newTitle).width
				* TEXT_WIDTH_SCALAR;
		this.projectTitleCell.width(Math.max(
				this.projectTitleField.getMinWidth(), newWidth));
		this.projectTitleCell.getLayout().invalidateHierarchy();
	}

	@Override
	public void initialize(Controller controller) {
		controller.getEditorGameAssets().finishLoading();

		if (this.updateInitialSceneName) {
			this.updateInitialSceneName = false;
			Model model = controller.getModel();
			EditorGame game = model.getGame();
			Note note = model.getScenes().get(game.getInitialScene())
					.getNotes();
			changeInitialSceneText(note);
		}
		if (this.addListeners) {
			this.addListeners = false;
			addModelLoadedListenerListener(controller);
			addInitialSceneNoteListener(controller);
			addInitialSceneListener(controller);
		}

		this.projectTitleField.setText(controller.getModel().getGame()
				.getNotes().getTitle());
		resizeTextField(controller.getApplicationAssets().getSkin());
	}

	@Override
	public void release(Controller controller) {
		if (!saving) {
			saving = true;
			this.projectTitleField.getStage().addAction(
					forever(delay(SAVE_DELAY, run(saveGame))));
		}
	}

	private final Runnable saveGame = new Runnable() {
		private static final String LOGTAG = "Save";

		private long startTime;

		@Override
		public void run() {
			Gdx.app.log(LOGTAG, " starting...");
			startTime = TimeUtils.millis();
			controller.getBackgroundExecutor().submit(saveTask, saveListener);
		}

		private final BackgroundTaskListener<Boolean> saveListener = new BackgroundTaskListener<Boolean>() {

			@Override
			public void completionPercentage(float percentage) {
			}

			@Override
			public void done(BackgroundExecutor backgroundExecutor,
					Boolean result) {
				Gdx.app.log(LOGTAG, " done saving, elapsed miliseconds: "
						+ TimeUtils.timeSinceMillis(startTime));
			}

			@Override
			public void error(Throwable e) {
				Gdx.app.error(LOGTAG, "error saving, elapsed miliseconds: "
						+ TimeUtils.timeSinceMillis(startTime), e);
			}
		};

		private final BackgroundTask<Boolean> saveTask = new BackgroundTask<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				controller.action(Save.class);
				return true;
			}
		};
	};
}
