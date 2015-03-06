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
package es.eucm.ead.editor.editorui.widgets;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.editorui.UITest;
import es.eucm.ead.editor.view.builders.scene.templates.TemplateEditor;
import es.eucm.ead.engine.gdx.AbstractWidget;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.editor.components.templates.Attribute;
import es.eucm.ead.schema.editor.components.templates.TemplateAttributes;

public class TemplateEditorTest extends UITest {

	@Override
	protected Actor buildUI(Skin skin, I18N i18n) {

		AbstractWidget container = new AbstractWidget();
		container.setFillParent(true);

		TemplateEditor templateEditor = new TemplateEditor(container,
				controller);

		Array<Attribute> attributesArray = new Array<Attribute>();

		Attribute wind = new Attribute();
		wind.setName("wind");
		wind.setMin(-100);
		wind.setMax(100);
		wind.setStep(0.1f);
		wind.setDefaultValue(0);
		attributesArray.add(wind);

		Attribute hour = new Attribute();
		hour.setName("hour");
		hour.setMin(0);
		hour.setMax(24);
		hour.setStep(1);
		hour.setDefaultValue(12);
		attributesArray.add(hour);

		Attribute amount = new Attribute();
		amount.setName("amount");
		amount.setMin(0);
		amount.setMax(100);
		amount.setStep(1);
		amount.setDefaultValue(10);
		attributesArray.add(amount);

		TemplateAttributes template = new TemplateAttributes();
		template.setAttributes(attributesArray);

		templateEditor.addTemplateAttributes(template);

		templateEditor.show();
		return container;
	}

	public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 640;
		config.height = 360;
		config.overrideDensity = 160;
		new LwjglApplication(new TemplateEditorTest(), config);
	}
}