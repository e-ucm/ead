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
package es.eucm.ead.editor.view.widgets.mockup.edition;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.Action.ActionListener;
import es.eucm.ead.editor.control.actions.editor.AddSceneElementFromResource;
import es.eucm.ead.editor.control.actions.editor.Redo;
import es.eucm.ead.editor.control.actions.editor.Undo;
import es.eucm.ead.editor.control.background.BackgroundExecutor;
import es.eucm.ead.editor.control.background.BackgroundExecutor.BackgroundTaskListener;
import es.eucm.ead.editor.control.background.BackgroundTask;
import es.eucm.ead.editor.view.builders.mockup.edition.EditionWindow;
import es.eucm.ead.editor.view.listeners.ActionOnClickListener;
import es.eucm.ead.editor.view.listeners.ActionOnDownListener;
import es.eucm.ead.editor.view.widgets.mockup.ToolBar;
import es.eucm.ead.editor.view.widgets.mockup.buttons.BottomProjectMenuButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.MenuButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.ToolbarButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.MenuButton.Position;
import es.eucm.ead.editor.view.widgets.mockup.edition.draw.PaintComponent;
import es.eucm.ead.editor.view.widgets.mockup.edition.draw.BrushStrokes;
import es.eucm.ead.engine.I18N;

public class AddElementComponent extends EditionComponent {

	private static final String LOGTAG = "AddElementComponent";
	private static final String IC_GO_BACK = "ic_goback", IC_UNDO = "ic_undo",
			IC_ADD = "ic_addelement", IC_PAINT_ELEMENT = "ic_editelement",
			IC_LAST_ELEMENT = "ic_lastelement",
			IC_PHOTO_ELEMENT = "ic_photoelement",
			IC_GALLERY_ELEMENT = "ic_galleryelement";

	private static final float PREF_BOTTOM_BUTTON_WIDTH = .5F;
	private static final float PREF_BOTTOM_BUTTON_HEIGHT = .2F;

	private ToolBar topToolbar;

	private final EraserComponent eraser;
	private final PaintComponent paint;
	private BrushStrokes brushStrokes;

	public AddElementComponent(final EditionWindow parent,
			Controller controller, Skin skin) {
		super(parent, controller, skin);

		this.eraser = new EraserComponent(parent, controller, skin);
		this.paint = new PaintComponent(parent, controller, skin);

		createTopToolbar(parent, controller);

		Button draw = new BottomProjectMenuButton(viewport,
				i18n.m("edition.tool.add-paint-element"), skin,
				IC_PAINT_ELEMENT, PREF_BOTTOM_BUTTON_WIDTH,
				PREF_BOTTOM_BUTTON_HEIGHT, Position.RIGHT);
		this.add(draw).fillX().expandX();
		this.row();

		this.add(new BottomProjectMenuButton(viewport, i18n
				.m("edition.tool.add-recent-element"), skin, IC_LAST_ELEMENT,
				PREF_BOTTOM_BUTTON_WIDTH, PREF_BOTTOM_BUTTON_HEIGHT,
				Position.RIGHT));
		this.row();
		this.add(new BottomProjectMenuButton(viewport, i18n
				.m("edition.tool.add-photo-element"), skin, IC_PHOTO_ELEMENT,
				PREF_BOTTOM_BUTTON_WIDTH, PREF_BOTTOM_BUTTON_HEIGHT,
				Position.RIGHT));
		this.row();

		final Button addFromGalleryButton = new BottomProjectMenuButton(
				viewport, i18n.m("edition.tool.add-gallery-element"), skin,
				IC_GALLERY_ELEMENT, PREF_BOTTOM_BUTTON_WIDTH,
				PREF_BOTTOM_BUTTON_HEIGHT, Position.RIGHT);
		addFromGalleryButton.addListener(new ActionOnDownListener(controller,
				AddSceneElementFromResource.class));
		this.add(addFromGalleryButton).fillX().expandX();

		draw.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (!topToolbar.isVisible()) {
					hide();
					paint.show();
					topToolbar.setVisible(true);
					brushStrokes.setVisible(true);
					parent.getTop().setVisible(false);
				} else {
					parent.getTop().setVisible(true);
					brushStrokes.setVisible(false);
					brushStrokes.release();
					brushStrokes.clearMesh();
				}
			}
		});
	}

	public void setBrushStrokes(BrushStrokes brushStrokes) {
		this.brushStrokes = brushStrokes;
		this.paint.setBrushStrokes(brushStrokes);
		this.eraser.setBrushStrokes(brushStrokes);
	}

	@Override
	protected Button createButton(Vector2 viewport, Skin skin, I18N i18n) {
		return new ToolbarButton(viewport, skin.getDrawable(IC_ADD),
				i18n.m("edition.add"), skin);
	}

	@Override
	public Array<Actor> getExtras() {
		Array<Actor> actors = new Array<Actor>(false, 2);
		actors.add(this.paint);
		actors.add(this.eraser);
		return actors;
	}

	private void createTopToolbar(final EditionWindow parent,
			final Controller controller) {

		this.topToolbar = new ToolBar(this.viewport, this.skin);
		this.topToolbar.setVisible(false);

		final Button backButton = new ToolbarButton(this.viewport, IC_GO_BACK,
				this.i18n.m("general.cancel"), false, this.skin);
		backButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				topToolbar.setVisible(false);
				brushStrokes.setVisible(false);
				brushStrokes.release();
				brushStrokes.clearMesh();
				eraser.hide();
				paint.hide();
				parent.getTop().setVisible(true);
			}
		});

		final Button saveButton = new ToolbarButton(this.viewport, IC_GO_BACK,
				this.i18n.m("general.save"), false, this.skin); // TODO change
		// the
		// icon, now
		// we dont have a icon to
		// save
		saveButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				controller.getBackgroundExecutor().submit(saveTask,
						saveListener);
				topToolbar.setVisible(false);
				brushStrokes.setVisible(false);
				eraser.hide();
				paint.hide();
				parent.getTop().setVisible(true);
			}

			private final BackgroundTaskListener<Boolean> saveListener = new BackgroundTaskListener<Boolean>() {

				@Override
				public void completionPercentage(float percentage) {
				}

				@Override
				public void done(BackgroundExecutor backgroundExecutor,
						Boolean result) {
					Gdx.app.log(LOGTAG, "done saving, result is: " + result);
					if (result) {
						brushStrokes.createSceneElement();
					}
					brushStrokes.clearMesh();
				}

				@Override
				public void error(Throwable e) {
					Gdx.app.error(LOGTAG, "error saving", e);
				}
			};

			private final BackgroundTask<Boolean> saveTask = new BackgroundTask<Boolean>() {
				@Override
				public Boolean call() throws Exception {

					boolean saved = brushStrokes.save();
					setCompletionPercentage(.5f);
					brushStrokes.release();
					setCompletionPercentage(1f);

					return saved;
				}
			};
		});

		/* Undo & Redo buttons */
		final Button undo = new ToolbarButton(this.viewport,
				this.skin.getDrawable(IC_UNDO), this.i18n.m("general.undo"),
				this.skin);
		undo.addListener(new ActionOnClickListener(controller, Undo.class));

		final TextureRegion redoRegion = new TextureRegion(
				this.skin.getRegion(IC_UNDO));
		redoRegion.flip(true, true);
		final TextureRegionDrawable redoDrawable = new TextureRegionDrawable(
				redoRegion);
		final Button redo = new ToolbarButton(this.viewport, redoDrawable,
				this.i18n.m("general.redo"), this.skin);
		redo.addListener(new ActionOnClickListener(controller, Redo.class));

		undo.setVisible(false);
		redo.setVisible(false);
		controller.getActions().addActionListener(Undo.class,
				new ActionListener() {
					@Override
					public void enableChanged(Class actionClass, boolean enable) {
						undo.setVisible(enable);
					}
				});
		controller.getActions().addActionListener(Redo.class,
				new ActionListener() {
					@Override
					public void enableChanged(Class actionClass, boolean enable) {
						redo.setVisible(enable);
					}
				});

		this.topToolbar.add(backButton).left().expandX();
		this.topToolbar.add(saveButton).left().expandX();
		this.topToolbar.add(undo, redo, this.paint.getButton(),
				this.eraser.getButton());

		new ButtonGroup(undo, redo);
		new ButtonGroup(this.paint.getButton(), this.eraser.getButton(),
				saveButton, backButton);
	}

	public ToolBar getToolbar() {
		return this.topToolbar;
	}

}
