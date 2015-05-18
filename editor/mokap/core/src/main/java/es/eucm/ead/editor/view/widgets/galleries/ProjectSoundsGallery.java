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
package es.eucm.ead.editor.view.widgets.galleries;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Scaling;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.editor.ExecuteWorker;
import es.eucm.ead.editor.control.actions.editor.ShowToast;
import es.eucm.ead.editor.control.workers.LoadSounds;
import es.eucm.ead.editor.control.workers.Worker;
import es.eucm.ead.editor.platform.MokapPlatform;
import es.eucm.ead.editor.platform.Platform;
import es.eucm.ead.editor.utils.ProjectUtils;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.widgets.Tile;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.editor.view.widgets.galleries.basegalleries.ThumbnailsGallery;
import es.eucm.ead.editor.view.widgets.layouts.Gallery;

public class ProjectSoundsGallery extends ThumbnailsGallery implements
		Worker.WorkerListener, Platform.FileChooserListener {

	private Drawable drawable;
	private ClickListener soundClicked = new ClickListener() {
		@Override
		public void clicked(InputEvent event, float x, float y) {
			gallery.uncheckAll();
			Gallery.Cell cell = (Gallery.Cell) event.getListenerActor()
					.getParent();
			cell.checked(true);
			selected(cell.getName());
		}
	};
	private Controller controller;

	public ProjectSoundsGallery(float rows, int columns, Controller controller) {
		super(rows, columns, null, controller.getApplicationAssets().getSkin(),
				controller.getApplicationAssets().getI18N(), controller
						.getApplicationAssets().getSkin()
						.get(Gallery.GalleryStyle.class));
		this.controller = controller;
		drawable = skin.getDrawable(SkinConstants.IC_MUSIC);
	}

	@Override
	public void loadContents(String checked) {
		clear();
		controller.action(ExecuteWorker.class, LoadSounds.class, this);
	}

	public Gallery.Cell addTile(String path, String title) {
		Tile tile = WidgetBuilder.tile(title, drawable);
		Actor background = tile.getBackground();
		background.setColor(Color.TEAL);
		((Image) background).setScaling(Scaling.none);
		prepareGalleryItem(tile, null);
		Gallery.Cell cell = gallery.add(tile);
		cell.setName(path);
		return cell;
	}

	@Override
	protected void prepareActionButton(Actor actor) {
		actor.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				askForAudio();
			}
		});
	}

	public void askForAudio() {
		MokapPlatform platform = (MokapPlatform) controller.getPlatform();
		platform.askForAudio(controller, this);
	}

	@Override
	protected void prepareGalleryItem(Actor actor, Object id) {
		actor.addListener(soundClicked);
	}

	@Override
	public void start() {

	}

	@Override
	public void result(Object... results) {
		addTile((String) results[0], (String) results[1]);
	}

	@Override
	public void done() {
		if (gallery.getChildren().size == 0) {
			askForAudio();
		}
	}

	@Override
	public void error(Throwable ex) {

	}

	@Override
	public void cancelled() {

	}

	protected void selected(String path) {

	}

	@Override
	public void fileChosen(String path, Result result) {
		if (result == Result.SUCCESS || path == null) {
			if (ProjectUtils.isSupportedAudio(controller.getEditorGameAssets()
					.resolve(path))) {
				String projectPath = controller.getEditorGameAssets()
						.copyToProjectIfNeeded(path, Music.class);
				if (projectPath != null) {
					selected(projectPath);
				} else {
					controller.action(ShowToast.class,
							i18N.m(Result.NOT_FOUND.getI18nKey()));
				}
			} else {
				controller.action(ShowToast.class, i18N.m("invalid.resource"));
			}
		} else if (result == Result.NOT_FOUND) {
			controller.action(ShowToast.class, i18N.m(result.getI18nKey()));
		}
	}
}
