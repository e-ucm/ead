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
package es.eucm.ead.editor.view.builders.mockup.edition;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.listeners.ActionListener;
import es.eucm.ead.editor.control.actions.editor.Redo;
import es.eucm.ead.editor.control.actions.editor.Undo;
import es.eucm.ead.editor.view.builders.ViewBuilder;
import es.eucm.ead.editor.view.listeners.ActionOnClickListener;
import es.eucm.ead.editor.view.widgets.mockup.Navigation;
import es.eucm.ead.editor.view.widgets.mockup.ToolBar;
import es.eucm.ead.editor.view.widgets.mockup.buttons.ToolbarButton;
import es.eucm.ead.editor.view.widgets.mockup.edition.EditionComponent;
import es.eucm.ead.editor.view.widgets.mockup.edition.ElementSelectedComponent;
import es.eucm.ead.editor.view.widgets.mockup.edition.MoreComponent;
import es.eucm.ead.editor.view.widgets.mockup.edition.MoreElementComponent;
import es.eucm.ead.editor.view.widgets.mockup.edition.MoreSceneComponent;
import es.eucm.ead.editor.view.widgets.mockup.edition.TextComponent;
import es.eucm.ead.editor.view.widgets.mockup.scenes.MockupSceneEditor;
import es.eucm.ead.engine.I18N;

/**
 * A view that can either be editing a {@link ElementEdition} or an
 * {@link SceneEdition}.
 */
public abstract class EditionWindow implements ViewBuilder {

	private static final String IC_UNDO = "ic_undo";

	private static final int DEFAULT_PAD = 8;

	protected Controller controller;

	private Navigation navigation;

	private Array<EditionComponent> components;
	private EditionComponent currentVisible;

	private MoreComponent moreComponent;

	/**
	 * Top actor. The bar with the buttons tools
	 */
	protected ToolBar top;

	/**
	 * Center actor. The Table with center elements
	 */
	protected Table center;

	/**
	 * The window with ToolBar top and Table center separated by rows
	 */
	private Table window;

	private MockupSceneEditor engineView;

	@Override
	public void initialize(Controller controller) {
		this.controller = controller;

		final I18N i18n = controller.getApplicationAssets().getI18N();
		final Skin skin = controller.getApplicationAssets().getSkin();
		final Vector2 viewport = controller.getPlatform().getSize();

		this.window = new Table();
		this.window.setFillParent(true);
		this.center = new Table() {
			@Override
			public void layout() {
				super.layout();
				for (final Actor children : getChildren()) {
					if (children instanceof EditionComponent) {
						final EditionComponent edit = (EditionComponent) children;
						edit.pack();
						final Button button = edit.getButton();
						button.pack();
						float prefX = button.getX() + button.getWidth() / 2f
								- edit.getWidth() / 2f;
						if (prefX + edit.getWidth() > getStage().getWidth()) {
							prefX = getStage().getWidth() - edit.getWidth();
						}
						if (edit.getHeight() > getHeight()) {
							edit.setHeight(getHeight());
						}
						children.setPosition(prefX,
								getHeight() - edit.getHeight());
					}
				}
				toBack();
			}
		}.debug();
		engineView = new MockupSceneEditor(controller);
		this.center.addActor(engineView);

		this.navigation = new Navigation(viewport, controller, skin);
		this.top = toolbar(viewport, controller, skin, i18n);
		this.components = editionComponents(viewport, controller, center,
				engineView);

		addToolbarComponents();

		final Container navWrapper = new Container(this.navigation.getPanel());
		navWrapper.setFillParent(true);
		navWrapper.top().left();

		this.center.addActor(navWrapper);

		this.window.add(top).fillX().expandX();
		this.window.row();
		this.window.add(center).fill().expand();

		for (final EditionComponent editionComponent : this.components) {
			this.center.addActor(editionComponent);
			if (editionComponent.getExtras() != null) {
				for (final Actor actor : editionComponent.getExtras()) {
					if (actor instanceof EditionComponent) {
						this.center.addActor(actor);
					} else {
						final Container extrasWrapper = new Container(actor);
						extrasWrapper.setFillParent(true);
						this.window.addActor(extrasWrapper);
					}
				}
			}
		}
	}

	@Override
	public Actor getView(Object... args) {
		this.moreComponent.initialize(controller);
		controller.getCommands().pushContext();

		return window;
	}

	public ToolBar getTop() {
		return top;
	}

	protected ToolBar toolbar(Vector2 viewport, Controller controller,
			Skin skin, I18N i18n) {
		final ToolBar top = new ToolBar(viewport, skin);
		top.add(this.navigation.getButton()).left();
		top.add(getTitle(i18n)).expandX().left().padLeft(DEFAULT_PAD);
		top.left();

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
				i18n.m("general.redo"), skin);
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

		top.add(undo).padRight(DEFAULT_PAD);
		top.add(redo).padRight(DEFAULT_PAD * 2f);
		new ButtonGroup(undo, redo);

		return top;
	}

	protected abstract String getTitle(I18N i18n);

	private void addToolbarComponents() {
		final ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.setMinCheckCount(0);
		for (final EditionComponent component : components) {
			buttonGroup.add(component.getButton());
			top.add(component.getButton()).padLeft(DEFAULT_PAD);
		}
		buttonGroup.setMinCheckCount(1);
	}

	private Array<EditionComponent> editionComponents(Vector2 viewport,
			Controller controller, Table center, MockupSceneEditor scaledView) {
		final Skin skin = controller.getApplicationAssets().getSkin();
		final Array<EditionComponent> components = new Array<EditionComponent>();

		components.add(new ElementSelectedComponent(this, controller, skin));

		components.add(new TextComponent(this, controller, skin));

		editionComponents(components, viewport, controller, skin, center,
				scaledView);

		this.moreComponent = null;
		if (this instanceof SceneEdition) {
			this.moreComponent = new MoreSceneComponent(this, controller, skin);
		} else {
			this.moreComponent = new MoreElementComponent(this, controller,
					skin);
		}
		components.add(this.moreComponent);
		return components;
	}

	/**
	 * Add the {@link EditionComponent}s that are not shared between
	 * {@link SceneEdition} and {@link ElementEdition}.
	 */
	protected abstract void editionComponents(
			Array<EditionComponent> editionComponents, Vector2 viewport,
			Controller controller, Skin skin, Table center,
			MockupSceneEditor scaledView);

	public Table getCenter() {
		return this.center;
	}

	public Table getRoot() {
		return this.window;
	}

	public void changeCurrentVisibleTo(EditionComponent component) {
		this.currentVisible = component;
	}

	public EditionComponent getCurrentVisible() {
		return this.currentVisible;
	}

	@Override
	public void release(Controller controller) {
		this.moreComponent.release(controller);

		// FIXME By the moment to avoid problems when change the language
		// repeatedly.
		if (!controller.getCommands().getCommandsStack().isEmpty()) {
			controller.getCommands().popContext(false);
		}
	}

	public Stage getStage() {
		return this.window.getStage();
	}
}
