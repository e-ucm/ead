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

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.builders.mockup.edition.EditionWindow;
import es.eucm.ead.editor.view.widgets.mockup.panels.HiddenPanel;
import es.eucm.ead.engine.I18N;

/**
 * Represents a panel that will be displayed in {@link EditionWindow}.
 */
public abstract class EditionComponent extends HiddenPanel {

	protected final Button button;
	private final EditionWindow parent;

	protected Skin skin;
	protected final I18N i18n;
	protected Vector2 viewport;

	/**
	 * A panel that will be displayed in edition view.
	 * 
	 * @param controller
	 * @param parent
	 */
	public EditionComponent(EditionWindow parent, Controller controller,
			Skin skin) {
		super(skin);

		this.i18n = controller.getApplicationAssets().getI18N();
		this.skin = skin;
		this.viewport = controller.getPlatform().getSize();

		this.parent = parent;
		this.setVisible(false);
		super.stageBackground = null;
		this.button = createButton(viewport, controller);
		this.button.addListener(new ClickListener() {
			final @Override
			public void clicked(InputEvent event, float x, float y) {
				if (!EditionComponent.this.isVisible()) {
					EditionComponent.this.show();
				} else {
					EditionComponent.this.hide();
				}
			}
		});
	}

	@Override
	public void show() {
		if (this.parent.getCurrentVisible() != null) {
			this.parent.getCurrentVisible().hide();
		}
		super.show();
		this.parent.changeCurrentVisibleTo(this);
	}

	@Override
	public void hide() {
		super.hide();
		if (this.parent.getCurrentVisible() == this) {
			this.parent.changeCurrentVisibleTo(null);
		}
	}

	/**
	 * Returns the button that will be associated to this panel.
	 * 
	 * @param viewport
	 * @param skin
	 * @param i18n
	 * @return the button that will be linked to this panel.
	 */
	protected abstract Button createButton(Vector2 viewport,
			Controller controller);

	public Button getButton() {
		return this.button;
	}

	/**
	 * @return extra {@link Actor actors} that you might want to add to this
	 *         edition panel.
	 */
	public Array<Actor> getExtras() {
		return null;
	}
}
