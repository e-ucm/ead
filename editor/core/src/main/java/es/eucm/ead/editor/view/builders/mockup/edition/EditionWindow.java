/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2013 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          C Profesor Jose Garcia Santesmases sn,
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

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.builders.ViewBuilder;
import es.eucm.ead.editor.view.widgets.mockup.Navigation;
import es.eucm.ead.editor.view.widgets.mockup.ToolBar;
import es.eucm.ead.editor.view.widgets.mockup.editionComponents.EditionComponent;
import es.eucm.ead.editor.view.widgets.mockup.editionComponents.EraserComponent;
import es.eucm.ead.editor.view.widgets.mockup.editionComponents.PaintComponent;
import es.eucm.ead.editor.view.widgets.mockup.editionComponents.TextComponent;
import es.eucm.ead.engine.I18N;

public class EditionWindow implements ViewBuilder {

	public static final String NAME = "edition";

	private Navigation navigation;

	private Array<EditionComponent> components;
	private EditionComponent currentVisible;

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public Actor build(Controller controller) {
		// I18N i18n = controller.getEditorAssets().getI18N();
		Skin skin = controller.getEditorAssets().getSkin();
		final Vector2 viewport = controller.getPlatform().getSize();

		components = editionComponents(viewport, controller);

		Table window = new Table();
		window.setFillParent(true);

		navigation = new Navigation(viewport, controller, skin);

		ToolBar top = toolbar(viewport, skin);

		Container navWrapper = new Container(navigation.getPanel());
		navWrapper.setFillParent(true);
		navWrapper.top().left().fillY();

		Table center = new Table() {
			@Override
			public void layout() {
				super.layout();
				for (Actor children : getChildren()) {
					if (children instanceof EditionComponent) {
						EditionComponent edit = (EditionComponent) children;

						edit.pack();
						final Button button = edit.getButton();
						button.pack();
						float prefX = button.getX() + button.getWidth() / 2f
								- edit.getWidth() / 2f;
						if (prefX + edit.getWidth() > getStage().getWidth()) {
							prefX = getStage().getWidth() - edit.getWidth();
						}
						children.setPosition(prefX,
								getHeight() - edit.getHeight());
					}
				}
			}
		}.debug();
		for (EditionComponent i : components) {
			center.addActor(i);
		}
		center.addActor(navWrapper);
		window.add(top).fillX().expandX();
		window.row();
		window.add(center).fill().expand();

		return window;
	}

	private ToolBar toolbar(Vector2 viewport, Skin skin) {
		ToolBar top = new ToolBar(viewport, skin);
		top.add(navigation.getButton()).left().expandX();
		top.left();

		for (EditionComponent component : components) {
			top.add(component.getButton());
		}

		return top;
	}

	// TODO add all components
	protected Array<EditionComponent> editionComponents(Vector2 viewport,
			Controller controller) {
		I18N i18n = controller.getEditorAssets().getI18N();
		Skin skin = controller.getEditorAssets().getSkin();
		Array<EditionComponent> components = new Array<EditionComponent>();

		components.add(new PaintComponent(this, viewport, i18n, skin));
		components.add(new EraserComponent(this, viewport, i18n, skin));
		components.add(new TextComponent(this, viewport, i18n, skin));

		final ButtonGroup buttonGroup = new ButtonGroup();
		for (EditionComponent component : components) {
			buttonGroup.add(component.getButton());
		}
		return components;
	}

	public void changeCurrentVisible(EditionComponent component) {
		this.currentVisible = component;
	}

	public EditionComponent getCurrentVisible() {
		return this.currentVisible;
	}

	@Override
	public void initialize(Controller controller) {
		// TODO Auto-generated method stub

	}

	@Override
	public void release(Controller controller) {
		// TODO Auto-generated method stub

	}

}
