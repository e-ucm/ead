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
package es.eucm.ead.editor.view.builders.scene.fx;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.builders.scene.interaction.ComponentEditor;
import es.eucm.ead.schema.components.tweens.AlphaTween;
import es.eucm.ead.schema.components.tweens.Timeline;
import es.eucm.ead.schema.components.tweens.Timeline.Mode;

public class EnterAnimationEdtior extends ComponentEditor<Timeline> {

	public EnterAnimationEdtior(Controller controller) {
		super(SkinConstants.IC_BLUR_LINEAR, controller.getApplicationAssets()
				.getI18N().m("animation.in"), "_animation_in", controller);
	}

	@Override
	protected void buildContent() {

	}

	@Override
	protected void read(Timeline component) {

	}

	@Override
	protected Timeline buildNewComponent() {
		AlphaTween alphaTween = new AlphaTween();
		alphaTween.setDuration(0.0f);
		alphaTween.setAlpha(0.0f);

		AlphaTween alphaTween2 = new AlphaTween();
		alphaTween2.setDuration(1.0f);
		alphaTween2.setAlpha(1.0f);

		Timeline timeline = new Timeline();
		timeline.setMode(Mode.SEQUENCE);

		timeline.getChildren().add(alphaTween);
		timeline.getChildren().add(alphaTween2);
		return timeline;
	}
}
