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
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.AddSceneElement;
import es.eucm.ead.editor.control.actions.EditorAction.EditorActionListener;
import es.eucm.ead.editor.control.actions.Redo;
import es.eucm.ead.editor.control.actions.Undo;
import es.eucm.ead.editor.view.builders.mockup.edition.EditionWindow;
import es.eucm.ead.editor.view.listeners.ActionOnClickListener;
import es.eucm.ead.editor.view.listeners.ActionOnDownListener;
import es.eucm.ead.editor.view.widgets.mockup.ToolBar;
import es.eucm.ead.editor.view.widgets.mockup.buttons.ToolbarButton;
import es.eucm.ead.engine.I18N;

public class AddElementComponent extends EditionComponent {

	private static final String IC_ADD = "tree_plus";
	private Table canvas;

	private ToolBar topToolbar;

	private EraserComponent eraser;
	private PaintComponent paint;

	private static final String IC_GO_BACK = "ic_goback", IC_UNDO = "ic_undo",
			IC_REDO = "ic_redo";

	public AddElementComponent(final EditionWindow parent,
			Controller controller, Skin skin) {
		super(parent, controller, skin);

		eraser = new EraserComponent(parent, controller, skin);
		paint = new PaintComponent(parent, controller, skin);

		createTopToolbar(parent, controller);

		Button draw = new TextButton(i18n.m("edition.tool.add-paint-element"),
				skin);
		this.add(draw).fillX().expandX();
		this.row();

		this.add(
				new TextButton(i18n.m("edition.tool.add-recent-element"), skin))
				.fillX().expandX();
		this.row();
		this.add(new TextButton(i18n.m("edition.tool.add-photo-element"), skin))
				.fillX().expandX();
		this.row();

		final Button addFromGalleryButton = new TextButton(
				i18n.m("edition.tool.add-gallery-element"), skin);
		addFromGalleryButton.addListener(new ActionOnDownListener(controller,
				AddSceneElement.class));
		this.add(addFromGalleryButton).fillX().expandX();

		// TODO this.canvas will be a component in which it can paint
		this.canvas = new Table(skin);
		this.canvas.add("Canvas para dibujar");
		this.canvas.setVisible(false);
		// END TODO

		draw.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (!AddElementComponent.this.topToolbar.isVisible()) {
					AddElementComponent.this.hide();
					AddElementComponent.this.topToolbar.setVisible(true);
					AddElementComponent.this.canvas.setVisible(true);
					parent.getTop().setVisible(false);
				} else {
					parent.getTop().setVisible(true);
				}
			}
		});
	}

	@Override
	protected Button createButton(Vector2 viewport, Skin skin, I18N i18n) {
		return new ToolbarButton(viewport, skin.getDrawable(IC_ADD),
				i18n.m("edition.add"), skin);
	}

	@Override
	public Array<Actor> getExtras() {
		final Array<Actor> actors = new Array<Actor>(false, 3);
		actors.add(this.canvas);
		actors.add(this.paint);
		actors.add(this.eraser);
		return actors;
	}

	private void createTopToolbar(final EditionWindow parent,
			Controller controller) {

		this.topToolbar = new ToolBar(viewport, skin);
		this.topToolbar.setVisible(false);

		final Button backButton = new ToolbarButton(viewport, IC_GO_BACK,
				this.i18n.m("general.cancel"), skin);
		backButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				AddElementComponent.this.topToolbar.setVisible(false);
				AddElementComponent.this.canvas.setVisible(false);
				parent.getTop().setVisible(true);
			}
		});

		final Button saveButton = new ToolbarButton(viewport, IC_GO_BACK,
				this.i18n.m("general.save"), skin); // TODO change the icon, now
													// we dont have a icon to
													// save
		saveButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				AddElementComponent.this.topToolbar.setVisible(false);
				AddElementComponent.this.canvas.setVisible(false);
				parent.getTop().setVisible(true);
				// TODO save the draw
			}
		});

		/* Undo & Redo buttons */
		final Button undo = new ToolbarButton(viewport,
				skin.getDrawable(IC_UNDO), i18n.m("general.undo"), skin);
		undo.addListener(new ActionOnClickListener(controller, Undo.class));

		final TextureRegion redoRegion = new TextureRegion(
				skin.getRegion(IC_UNDO));
		redoRegion.flip(true, true);
		final TextureRegionDrawable redoDrawable = new TextureRegionDrawable(
				redoRegion);
		final Button redo = new ToolbarButton(viewport, redoDrawable,
				i18n.m(IC_REDO), skin);
		redo.addListener(new ActionOnClickListener(controller, Redo.class));

		undo.setVisible(false);
		redo.setVisible(false);
		controller.getActions().addActionListener(Undo.class,
				new EditorActionListener() {
					@Override
					public void enabledChanged(Class actionClass, boolean enable) {
						undo.setVisible(enable);
					}
				});
		controller.getActions().addActionListener(Redo.class,
				new EditorActionListener() {
					@Override
					public void enabledChanged(Class actionClass, boolean enable) {
						redo.setVisible(enable);
					}
				});

		this.topToolbar.add(backButton).left();
		this.topToolbar.add("").expandX();
		this.topToolbar.add(saveButton).left();
		this.topToolbar.add("").expandX();
		this.topToolbar.add(undo, redo);
		this.topToolbar.add(this.paint.getButton());
		this.topToolbar.add(this.eraser.getButton());

		new ButtonGroup(undo, redo);
	}

	public ToolBar getToolbar() {
		return this.topToolbar;
	}

}
