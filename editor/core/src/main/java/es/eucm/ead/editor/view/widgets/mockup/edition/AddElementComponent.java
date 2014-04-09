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
import es.eucm.ead.editor.view.builders.mockup.edition.EditionWindow;
import es.eucm.ead.editor.view.listeners.ActionOnClickListener;
import es.eucm.ead.editor.view.listeners.ActionOnDownListener;
import es.eucm.ead.editor.view.widgets.mockup.ToolBar;
import es.eucm.ead.editor.view.widgets.mockup.buttons.ToolbarButton;
import es.eucm.ead.editor.view.widgets.mockup.edition.draw.PaintComponent;
import es.eucm.ead.editor.view.widgets.mockup.edition.draw.PaintingWidget;
import es.eucm.ead.engine.I18N;

public class AddElementComponent extends EditionComponent {

	private static final String IC_GO_BACK = "ic_goback", IC_UNDO = "ic_undo";
	private static final String IC_ADD = "tree_plus";

	private ToolBar topToolbar;

	private final EraserComponent eraser;
	private final PaintComponent paint;
	private PaintingWidget painting;

	public AddElementComponent(final EditionWindow parent,
			Controller controller, Skin skin) {
		super(parent, controller, skin);

		this.eraser = new EraserComponent(parent, controller, skin);
		this.paint = new PaintComponent(parent, controller, skin);

		createTopToolbar(parent, controller);

		Button draw = new TextButton(
				this.i18n.m("edition.tool.add-paint-element"), skin);
		this.add(draw).fillX().expandX();
		this.row();

		this.add(
				new TextButton(this.i18n.m("edition.tool.add-recent-element"),
						skin)).fillX().expandX();
		this.row();
		this.add(
				new TextButton(this.i18n.m("edition.tool.add-photo-element"),
						skin)).fillX().expandX();
		this.row();

		final Button addFromGalleryButton = new TextButton(
				this.i18n.m("edition.tool.add-gallery-element"), skin);
		addFromGalleryButton.addListener(new ActionOnDownListener(controller,
				AddSceneElementFromResource.class));
		this.add(addFromGalleryButton).fillX().expandX();

		draw.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (!AddElementComponent.this.topToolbar.isVisible()) {
					AddElementComponent.this.hide();
					AddElementComponent.this.topToolbar.setVisible(true);
					AddElementComponent.this.painting.setVisible(true);
					parent.getTop().setVisible(false);
				} else {
					parent.getTop().setVisible(true);
					AddElementComponent.this.painting.setVisible(false);
				}
			}
		});
	}

	public void setPainting(PaintingWidget painting) {
		this.painting = painting;
		this.paint.setPainting(this.painting);
	}

	@Override
	protected Button createButton(Vector2 viewport, Skin skin, I18N i18n) {
		return new ToolbarButton(viewport, skin.getDrawable(IC_ADD),
				i18n.m("edition.add"), skin);
	}

	@Override
	public Array<Actor> getExtras() {
		final Array<Actor> actors = new Array<Actor>(false, 2);
		actors.add(this.paint);
		actors.add(this.eraser);
		return actors;
	}

	private void createTopToolbar(final EditionWindow parent,
			Controller controller) {

		this.topToolbar = new ToolBar(this.viewport, this.skin);
		this.topToolbar.setVisible(false);

		final Button backButton = new ToolbarButton(this.viewport, IC_GO_BACK,
				this.i18n.m("general.cancel"), false, this.skin);
		backButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				AddElementComponent.this.topToolbar.setVisible(false);
				AddElementComponent.this.painting.setVisible(false);
				AddElementComponent.this.eraser.hide();
				AddElementComponent.this.paint.hide();
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
				AddElementComponent.this.topToolbar.setVisible(false);
				AddElementComponent.this.painting.save();
				AddElementComponent.this.painting.setVisible(false);
				AddElementComponent.this.eraser.hide();
				AddElementComponent.this.paint.hide();
				parent.getTop().setVisible(true);
			}
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
