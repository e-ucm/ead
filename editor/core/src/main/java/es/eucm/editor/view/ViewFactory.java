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
package es.eucm.editor.view;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.XmlReader.Element;
import es.eucm.editor.view.builders.CircularGroupBuilder;
import es.eucm.editor.view.builders.FPSCounterBuilder;
import es.eucm.editor.view.builders.ScenePreviewBuilder;
import es.eucm.editor.view.builders.TableBuilder;
import es.eucm.editor.view.builders.TextButonBuilder;
import es.eucm.editor.view.builders.WindowBuilder;

import java.util.HashMap;
import java.util.Map;

public class ViewFactory {

	private Map<String, ViewBuilder> builders;

	public ViewFactory() {
		builders = new HashMap<String, ViewBuilder>();
		builders.put("window", new WindowBuilder(this));
		builders.put("table", new TableBuilder(this));
		builders.put("textbutton", new TextButonBuilder(this));
		builders.put("circulargroup", new CircularGroupBuilder(this));
		builders.put("fpscounter", new FPSCounterBuilder(this));
		builders.put("scenepreview", new ScenePreviewBuilder(this));

	}

	public Actor build(Element element, Skin skin) {
		ViewBuilder viewBuilder = builders.get(element.getName());
		if (viewBuilder != null) {
			return viewBuilder.build(element, skin);
		}
		return null;
	}
}
