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
package es.eucm.ead.editor.view.builders.scene.components.transformanimations;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.model.generic.SetField;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.widgets.IconButton;
import es.eucm.ead.editor.view.widgets.MultiRangeSlider;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.schema.editor.components.animations.BlinkAnimation;
import es.eucm.ead.schemax.ComponentIds;
import es.eucm.ead.schemax.FieldName;

public class BlinkAnimationEditor extends
		TransformAnimationEditor<BlinkAnimation> {

	private MultiRangeSlider alpha;

	public BlinkAnimationEditor(Controller c) {
		super(c, ComponentIds.BLINK, "blink");

		Skin skin = controller.getApplicationAssets().getSkin();

		LinearLayout alphaRow = new LinearLayout(true);
		alpha = new MultiRangeSlider(0.0f, 1.0f, 0.01f, false, skin);

		IconButton alphaImage = WidgetBuilder.icon(SkinConstants.IC_TONALITY,
				SkinConstants.STYLE_GRAY);

		alphaRow.add(alphaImage).centerY();
		alphaRow.add(alpha).expandX();
		addBodyRow(alphaRow);
		alpha.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				controller.action(SetField.class, animation,
						FieldName.MIN_ALPHA, alpha.getValue());
				controller.action(SetField.class, animation,
						FieldName.MAX_ALPHA, alpha.getValue2());
			}
		});

	}

	@Override
	protected BlinkAnimation newComponent() {
		return new BlinkAnimation();
	}

}
