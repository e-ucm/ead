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
package es.eucm.ead.editor.view.widgets;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import es.eucm.ead.editor.assets.ApplicationAssets;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.model.generic.SetField;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.builders.scene.SceneEditor;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schema.renderers.EmptyRenderer;
import es.eucm.ead.schemax.FieldName;

public class EmptyRendererEditor extends ContextMenu {

	private CheckBox hitAll;
	private Controller controller;
	private ModelEntity modelEntity;

	public EmptyRendererEditor(Controller control) {
		this.controller = control;
		float pad = WidgetBuilder.dpToPixels(8);
		pad(pad);

		ApplicationAssets applicationAssets = controller.getApplicationAssets();
		Skin skin = applicationAssets.getSkin();
		setBackground(skin.getDrawable(SkinConstants.DRAWABLE_PAGE));

		add(hitAll = new CheckBox(applicationAssets.getI18N().m("hit.all"),
				skin));
		hitAll.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				EmptyRenderer emptyRenderer = Q.getComponent(modelEntity,
						EmptyRenderer.class);
				controller.action(SetField.class, emptyRenderer,
						FieldName.HIT_ALL, hitAll.isChecked());
			}
		});
	}

	public void prepare(SceneEditor sceneEditor) {
		modelEntity = (ModelEntity) controller.getModel().getSelection()
				.getSingle(Selection.SCENE_ELEMENT);
		EmptyRenderer emptyRenderer = Q.getComponent(modelEntity,
				EmptyRenderer.class);
		hitAll.setChecked(emptyRenderer.isHitAll());
	}

	@Override
	public boolean hideAlways() {
		return true;
	}
}
