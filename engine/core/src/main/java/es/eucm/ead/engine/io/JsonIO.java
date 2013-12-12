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
package es.eucm.ead.engine.io;

import com.badlogic.gdx.utils.Json;

import es.eucm.ead.engine.BindingsLoader.BindingListener;
import es.eucm.ead.engine.Engine;
import es.eucm.ead.engine.io.serializers.AtlasImageSerializer;
import es.eucm.ead.engine.io.serializers.ImageSerializer;
import es.eucm.ead.engine.io.serializers.SceneElementSerializer;
import es.eucm.ead.schema.actors.SceneElement;
import es.eucm.ead.schema.renderers.AtlasImage;
import es.eucm.ead.schema.renderers.Image;

public class JsonIO extends Json implements BindingListener {

	public JsonIO() {
		setSerializers();
	}

	protected Object newInstance(Class type) {
		return Engine.factory.newInstance(type);
	}

	public void setSerializers() {
		setSerializer(AtlasImage.class, new AtlasImageSerializer());
		setSerializer(Image.class, new ImageSerializer());
		setSerializer(SceneElement.class, new SceneElementSerializer());
	}

	@Override
	public void bind(String alias, Class schemaClass, Class engineClass) {
		addClassTag(alias, schemaClass);
	}
}
