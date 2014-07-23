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
package es.eucm.ead.editor.view.ui.effects;

import com.badlogic.gdx.graphics.Color;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.schema.editor.components.Documentation;
import es.eucm.ead.schema.effects.GoScene;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.entities.ResourceCategory;

public class GoSceneWidget extends EffectWidget<GoScene> {

	@Override
	protected String getIcon() {
		return "scene24x24";
	}

	@Override
	protected String effectToString() {
		GoScene effect = getEffect();
		String sceneName = "";
		ModelEntity scene = (ModelEntity) model.getResourceObject(
				effect.getSceneId(), ResourceCategory.SCENE);

		if (scene != null) {
			Documentation doc = Q.getComponent(scene, Documentation.class);
			sceneName = doc.getName() == null || "".equals(doc.getName()) ? effect
					.getSceneId() : doc.getName();
		}
		return i18N.m("goscene.tostring", sceneName);
	}

	@Override
	protected boolean hasTarget() {
		return false;
	}

	@Override
	protected Color getBackgroundColor() {
		return Color.PINK;
	}

	@Override
	protected Class<GoScene> getEffectClass() {
		return GoScene.class;
	}
}
