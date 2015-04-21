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
package es.eucm.ead.editor.control.actions.editor;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.EditorAction;
import es.eucm.ead.editor.control.actions.model.AddSceneElement;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.components.controls.Label;
import es.eucm.ead.schema.data.Color;
import es.eucm.ead.schema.entities.ModelEntity;

/**
 * 
 * <p>
 * A text is requested to create a {@link Label} in the current scene.
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0] (Optional)</strong> <em>{@link Label}</em> The
 * {@link Label} to add.</dd>
 * <dd><strong>args[1] (Optional)</strong> <em>boolean</em> false to set the
 * default font.</dd>
 * </dl>
 * </p>
 * 
 */
public class AddLabel extends EditorAction implements Input.TextInputListener {

	private GlyphLayout glyphLayout = new GlyphLayout();

	private Color color;

	public AddLabel() {
		super(true, false, new Class[] {}, new Class[] { Label.class },
				new Class[] { Label.class, Boolean.class });
		setTrackable(true);
	}

	@Override
	public void initialize(Controller controller) {
		super.initialize(controller);
		color = new Color();
		color.setR(0);
		color.setG(0);
		color.setB(0);
		color.setA(1);
	}

	@Override
	public void perform(Object... args) {
		if (args.length == 0) {
			I18N i18n = controller.getApplicationAssets().getI18N();
			controller.getPlatform().getMultilineTextInput(this,
					i18n.m("toolbar.text.input"), "", i18n);
		} else {
			addText((Label) args[0], (args.length == 2 && (Boolean) args[1]));
		}
	}

	private void addText(Label label, boolean keepStyle) {

		Skin skin = controller.getEditorGameAssets().getSkin();
		if (!keepStyle) {
			setDefultFont(label);
		}

		LabelStyle labelStyle = skin.get(label.getStyle(), LabelStyle.class);
		glyphLayout.setText(labelStyle.font, label.getText());

		ModelEntity textLabel = Q.createCenteredEntity(glyphLayout.width * .5f,
				glyphLayout.height * .5f, label);

		controller.action(AddSceneElement.class, textLabel);
	}

	@Override
	public void input(String text) {
		if (text != null && !text.replace(" ", "").isEmpty()) {
			Label label = new Label();
			label.setText(text);
			addText(label, false);
		}
	}

	@Override
	public void canceled() {

	}

	private void setDefultFont(Label label) {
		label.setColor(color);
		label.setStyle("roboto-small"); // default style
	}
}
