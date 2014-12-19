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
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.builders.scene.components.ComponentEditor;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.schema.components.ModelComponent;
import es.eucm.ead.schema.editor.components.animations.TransformAnimation;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.ComponentIds;

public class AnimationsEditor extends ComponentEditor<ModelComponent> {

	private ObjectMap<String, TransformAnimationEditor> editors;

	public AnimationsEditor(Controller controller) {
		super(SkinConstants.IC_BLUR_LINEAR, controller.getApplicationAssets()
				.getI18N().m("animations"), ComponentIds.ANIMATIONS, controller);
	}

	@Override
	protected void buildContent() {
		editors = new ObjectMap<String, TransformAnimationEditor>();
		addTransformationAnimationEditor(new MoveAnimationEditor(controller));
		addTransformationAnimationEditor(new BlinkAnimationEditor(controller));

		float pad = WidgetBuilder.dpToPixels(16);
		for (Actor a : editors.values()) {
			list.add(a).expandX().margin(pad, pad * 0.5f, pad, pad * 0.5f);
		}
	}

	private void addTransformationAnimationEditor(
			TransformAnimationEditor editor) {
		editors.put(editor.getComponentId(), editor);
	}

	@Override
	protected void read(ModelEntity entity, ModelComponent component) {
		for (Entry<String, TransformAnimationEditor> entry : editors.entries()) {
			entry.value.setEntity(entity);

			ModelComponent animation = Q.getComponentById(entity, entry.key);
			if (animation == null) {
				entry.value.setEnable(false);
			} else {
				entry.value.setEnable(true);
				entry.value.read((TransformAnimation) animation);
			}
			entry.value.close(false);
		}

	}

	@Override
	protected ModelComponent buildNewComponent() {
		return new ModelComponent();
	}

	@Override
	protected void removeComponent(ModelEntity modelEntity) {
		super.removeComponent(modelEntity);
		for (TransformAnimationEditor editor : editors.values()) {
			editor.removeComponent();
		}
	}
}
