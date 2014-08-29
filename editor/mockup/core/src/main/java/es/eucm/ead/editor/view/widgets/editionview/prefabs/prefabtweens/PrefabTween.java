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
package es.eucm.ead.editor.view.widgets.editionview.prefabs.prefabtweens;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Scaling;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.AddTween;
import es.eucm.ead.editor.control.actions.RemoveTween;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.components.tweens.Tween;

public abstract class PrefabTween extends Button {

	private static final float SEPARATION = 5;

	protected Controller controller;

	private Label label;

	private Image image;

	private Tween tween;

	public PrefabTween(String icon, String name, Controller controller,
			Skin skin) {
		super(skin, "white");

		I18N i18n = controller.getApplicationAssets().getI18N();

		this.controller = controller;

		image = new Image(skin.getDrawable(icon));
		image.setScaling(Scaling.fit);

		label = new Label(i18n.m(name), skin);

		add(image);
		row();
		add(label).expand().center().padTop(SEPARATION);

		tween = createTween();
	}

	public void setState(boolean isChecked) {
		super.setChecked(isChecked);
	}

	@Override
	public void setChecked(boolean isChecked) {
		super.setChecked(isChecked);
		if (isChecked) {
			controller.action(AddTween.class, tween);
		} else {
			controller.action(RemoveTween.class, tween);
		}
	}

	public Tween getTween() {
		return tween;
	}

	public void setTween(Tween tween) {
		this.tween = tween;
	}

	protected abstract Tween createTween();
}
