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
package es.eucm.ead.editor.view.builders.mockup.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;

import es.eucm.ead.editor.assets.ApplicationAssets;
import es.eucm.ead.editor.assets.EditorGameAssets;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.editor.ChangeView;
import es.eucm.ead.editor.control.actions.model.AddSceneElement;
import es.eucm.ead.editor.control.actions.model.EditScene;
import es.eucm.ead.editor.control.actions.model.scene.NewScene;
import es.eucm.ead.editor.control.actions.model.scene.SetEditionContext;
import es.eucm.ead.editor.platform.DevicePictureControl;
import es.eucm.ead.editor.view.builders.ViewBuilder;
import es.eucm.ead.editor.view.builders.mockup.edition.ElementEdition;
import es.eucm.ead.editor.view.builders.mockup.edition.SceneEdition;
import es.eucm.ead.editor.view.builders.mockup.gallery.ElementGallery;
import es.eucm.ead.editor.view.builders.mockup.gallery.Gallery;
import es.eucm.ead.editor.view.builders.mockup.gallery.SceneGallery;
import es.eucm.ead.editor.view.widgets.mockup.Navigation;
import es.eucm.ead.editor.view.widgets.mockup.Notification;
import es.eucm.ead.editor.view.widgets.mockup.Scenes;
import es.eucm.ead.editor.view.widgets.mockup.buttons.IconButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.SceneButton;
import es.eucm.ead.editor.view.widgets.mockup.panels.HiddenPanel;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.engine.assets.Assets.AssetLoadedCallback;
import es.eucm.ead.schema.editor.components.RepoElement;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.GameStructure;

public class Picture implements ViewBuilder,
		DevicePictureControl.CameraPreparedListener,
		DevicePictureControl.PictureTakenListener {

	private static final String IC_PHOTO = "ic_photocamera";
	private static final String PIC_EXTENSION = ".jpeg";
	private static final float DEFAULT_PAD = 10f;
	private static final float DEFAULT_TIMEOUT = 2F;

	private FileHandle pictureFile, thumbnailFile;
	private DevicePictureControl pictureControl;
	private SelectScenePanel selectScenePanel;
	private SelectBox<String> resolution;
	private String previousResolution;
	private Notification errorNotif;
	private boolean cameraPrepared;
	private Controller controller;
	private Button takePicButton;
	private Class<?> arg;
	private Actor view;

	private final Runnable resolutionSelectedRunnable = new Runnable() {
		@Override
		public void run() {
			if (!Picture.this.cameraPrepared)
				return;
			final String selected = Picture.this.resolution.getSelected();
			if (Picture.this.previousResolution.equals(selected))
				return;
			final String[] sels = selected.split("x");
			Picture.this.pictureControl.setPictureSize(
					Integer.valueOf(sels[0]), Integer.valueOf(sels[1]));
			Picture.this.resolution.setDisabled(true);
			Picture.this.controller.action(ChangeView.class, Picture.class);
		}
	};

	@Override
	public Actor getView(Object... args) {
		this.pictureControl.prepareCameraAsync(this);
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		this.takePicButton.setDisabled(false);
		this.cameraPrepared = false;
		if (args.length == 1) {
			// We receive an argument when the previous view was one of the
			// following:
			//
			// - SceneEdition <- user wants to import an element by taking a
			// picture
			//
			// - ElementGallery <- user wants to take a picture and go to
			// ElementEdition
			//
			// - Gallery or SceneGallery <- user wants to take a picture and go
			// to SceneEdition
			arg = (Class<?>) args[0];
		}
		return view;
	}

	private void takePic() {
		Picture.this.takePicButton.setDisabled(true);

		ApplicationAssets appAssets = controller.getApplicationAssets();
		EditorGameAssets gameAssets = controller.getEditorGameAssets();

		I18N i18n = appAssets.getI18N();

		String imgFolder = this.controller.getLoadingPath()
				+ GameStructure.IMAGES_FOLDER;
		FileHandle imagesHandle = gameAssets.resolve(imgFolder);
		if (!imagesHandle.exists()) {
			imagesHandle.mkdirs();
		}
		// Find the first picture path that doesn't exist, the pictures are
		// saved with a localized name
		String picturePath = imgFolder + i18n.m("picture.pictureName");
		pictureFile = null;
		int i = 0;
		do {
			pictureFile = gameAssets.resolve(picturePath + " " + ++i
					+ PIC_EXTENSION);
		} while (pictureFile.exists());

		String thumbsFolder = this.controller.getLoadingPath()
				+ GameStructure.THUMBNAILS_PATH;
		FileHandle thumbsHandle = gameAssets.resolve(thumbsFolder);
		if (!thumbsHandle.exists()) {
			thumbsHandle.mkdirs();
		}
		// Find the first thumbnail path that doesn't exist, the thumbnails are
		// saved with a localized name
		String thumbnailPath = thumbsFolder + i18n.m("picture.thumbnailName");
		thumbnailFile = null;
		i = 0;
		do {
			thumbnailFile = gameAssets.resolve(thumbnailPath + " " + ++i
					+ PIC_EXTENSION);
		} while (thumbnailFile.exists());

		this.pictureControl.takePictureAsync(pictureFile.file()
				.getAbsolutePath(), thumbnailFile.file().getAbsolutePath(),
				this);
	}

	@Override
	public void initialize(Controller controller) {
		this.controller = controller;

		ApplicationAssets appAssets = controller.getApplicationAssets();
		Skin skin = appAssets.getSkin();
		pictureControl = this.controller.getPlatform().getPicture();
		Vector2 viewport = this.controller.getPlatform().getSize();

		errorNotif = new Notification(skin).text(appAssets.getI18N().m(
				"notification.error"));

		this.takePicButton = new IconButton(viewport, skin, IC_PHOTO);
		this.takePicButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				takePic();
			}
		});

		resolution = new SelectBox<String>(skin);
		resolution.setDisabled(true);
		resolution.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Gdx.app.postRunnable(resolutionSelectedRunnable);
			}
		});

		selectScenePanel = new SelectScenePanel(skin);
		selectScenePanel.setVisible(false);
		Container selectScenePanelContainer = new Container(selectScenePanel);
		selectScenePanelContainer.setFillParent(true);

		Table window = new Table(skin).debug().pad(DEFAULT_PAD);
		window.setFillParent(true);
		window.add(resolution).right().top();
		window.row();
		window.add(takePicButton).bottom().expand().padBottom(DEFAULT_PAD);
		window.addActor(new Navigation(viewport, controller, skin));
		window.addActor(selectScenePanelContainer);
		view = window;
	}

	@Override
	public void release(Controller controller) {
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
		this.pictureControl.stopPreviewAsync();
		selectScenePanel.release();
	}

	@Override
	public void onCameraPrepared() {
		this.cameraPrepared = true;

		final Array<Vector2> sizes = this.pictureControl
				.getSupportedPictureSizes();
		final String[] sizesStr = new String[sizes.size];
		int i = 0;
		for (final Vector2 size : sizes) {
			sizesStr[i] = String.valueOf((int) size.x) + "x"
					+ String.valueOf((int) size.y);
			++i;
		}
		this.resolution.setItems(sizesStr);
		final Vector2 pictureSize = this.pictureControl.getCurrentPictureSize();
		final String currRes = String.valueOf((int) pictureSize.x) + "x"
				+ String.valueOf((int) pictureSize.y);
		this.resolution.setSelected(currRes);
		this.previousResolution = currRes;
		this.resolution.setDisabled(false);
	}

	@Override
	public void onPictureTaken(boolean success) {
		if (success && pictureFile.exists() && thumbnailFile.exists()) {
			if (arg == SceneEdition.class) {
				ModelEntity element = createAndAddElementWithThumbnail();
				controller.action(EditScene.class, controller.getModel()
						.getIdFor(element));
				controller.action(ChangeView.class, arg);
			} else {
				selectScenePanel.show();
			}
		} else {
			errorNotif.show(takePicButton.getStage(), DEFAULT_TIMEOUT);
			this.takePicButton.setDisabled(false);
		}
	}

	private ModelEntity createAndAddElementWithThumbnail() {
		ModelEntity element = controller.getTemplates().createSceneElement(
				pictureFile.path());

		RepoElement repoElem = new RepoElement();
		repoElem.setThumbnail(thumbnailFile.name());
		element.getComponents().add(repoElem);

		controller.action(AddSceneElement.class, element);

		return element;
	}

	private class SelectScenePanel extends HiddenPanel {

		private static final float PREF_WIDTH = .85f;

		private final float defaultPad = 10f;

		private boolean needsUpdate;
		private Image thumbImage;
		private Scenes scenes;

		public SelectScenePanel(Skin skin) {
			super(skin);
			setModal(true);
			pad(defaultPad);
			needsUpdate = true;
			defaults().space(defaultPad);
			scenes = new Scenes(controller);

			ApplicationAssets appAssets = controller.getApplicationAssets();
			I18N i18n = appAssets.getI18N();

			Button cancel = new TextButton(i18n.m("general.cancel"), skin);
			final Button accept = new TextButton(i18n.m("general.accept"), skin);
			ClickListener clickListener = new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					Actor button = event.getListenerActor();
					if (button == accept) {
						SceneButton selected = scenes.getSelected();

						String selectedKey = selected.getKey();
						if (selectedKey.isEmpty()) {
							// If the selected key is empty then the user
							// selected the first option which is new scene
							controller.action(NewScene.class);
						} else {
							controller.action(EditScene.class, selectedKey);
						}

						ModelEntity element = createAndAddElementWithThumbnail();

						Class<?> nextView = null;
						// Depending on which view we came from we will go to a
						// different view
						if (arg == SceneGallery.class || arg == Gallery.class) {
							nextView = SceneEdition.class;
						} else if (arg == ElementGallery.class) {
							controller.action(SetEditionContext.class, element);
							nextView = ElementEdition.class;
						}
						controller.action(ChangeView.class, nextView);

						hideWithoutAnimation();
					} else {
						hide();
					}
				}
			};
			accept.addListener(clickListener);
			cancel.addListener(clickListener);

			Label chooseScene = new Label(i18n.m("picture.chooseScene"), skin);
			chooseScene.setWrap(true);
			chooseScene.setAlignment(Align.center);

			thumbImage = new Image();
			thumbImage.setScaling(Scaling.fit);

			Table leftTable = new Table(skin);
			leftTable.add(i18n.m("picture.succeeded")).colspan(2);
			leftTable.row();
			leftTable.add(thumbImage).expandY().fill();
			leftTable.add(chooseScene).expand().fill();
			leftTable.row();
			leftTable.add(accept).left();
			leftTable.add(cancel).right();

			add(leftTable).expand().fill();
			add(scenes).expandY().fillY();
		}

		@Override
		public float getPrefWidth() {
			return getStage().getWidth() * PREF_WIDTH;
		}

		public void release() {
			this.needsUpdate = true;
			if (isVisible()) {
				hideWithoutAnimation();
			}
		}

		@Override
		protected void onFadedOut() {
			takePicButton.setDisabled(false);
		}

		public void show() {
			if (needsUpdate) {
				needsUpdate = false;
				scenes.refresh();
			}
			controller.getEditorGameAssets().get(thumbnailFile.path(),
					Texture.class, thumbnailLoadedCallback);
			super.show();
		}

		private final AssetLoadedCallback<Texture> thumbnailLoadedCallback = new AssetLoadedCallback<Texture>() {

			@Override
			public void loaded(String fileName, Texture asset) {
				thumbImage.setDrawable(new TextureRegionDrawable(
						new TextureRegion(asset)));
			}
		};
	}
}
