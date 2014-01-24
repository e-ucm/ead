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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.SerializationException;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;

import es.eucm.ead.engine.Assets;
import es.eucm.ead.engine.Factory;
import es.eucm.ead.engine.io.serializers.AtlasImageSerializer;
import es.eucm.ead.engine.io.serializers.ImageSerializer;
import es.eucm.ead.engine.io.serializers.NinePatchSerializer;
import es.eucm.ead.engine.io.serializers.SceneElementSerializer;
import es.eucm.ead.engine.io.serializers.TextSerializer;
import es.eucm.ead.schema.actors.SceneElement;
import es.eucm.ead.schema.renderers.AtlasImage;
import es.eucm.ead.schema.renderers.Image;
import es.eucm.ead.schema.renderers.NinePatch;
import es.eucm.ead.schema.renderers.Text;

/**
 * This class deals with reading and writing schema objects. By default, maps
 * JSON objects into java classes, but customized serializers can be set to
 * process differently concrete schema classes.
 */
public class SchemaIO extends Json {

	private Factory factory;

	public SchemaIO(Assets assets, Factory factory) {
		this.factory = factory;
		setSerializers(assets, factory);
	}

	/**
	 * Set the customized serializers
	 */
	protected void setSerializers(Assets assets, Factory factory) {
		setSerializer(AtlasImage.class, new AtlasImageSerializer(assets,
				factory));
		setSerializer(Image.class, new ImageSerializer(assets, factory));
		setSerializer(Text.class, new TextSerializer(assets, factory));
		setSerializer(SceneElement.class, new SceneElementSerializer(assets,
				factory));
		setSerializer(NinePatch.class, new NinePatchSerializer(assets, factory));
	}

	@Override
	protected Object newInstance(Class type) {
		// Obtain new instance from factory
		return factory.newInstance(type);
	}

	/**
	 * Loads alias stored in the file
	 * 
	 * @param fileHandle
	 *            file storing the alias
	 * @return if the alias loading was completely correct. It might fail if the
	 *         the file is not a valid or a non existing or invalid class is
	 *         found
	 */
	public boolean loadAlias(FileHandle fileHandle) {
		try {
			Array<Array<String>> bindings = fromJson(Array.class, fileHandle);
			read(bindings);
		} catch (SerializationException e) {
			Gdx.app.error("Factory", fileHandle.path()
					+ " doesn't contain a valid bindings file");
			return false;
		}
		return true;
	}

	private boolean read(Array<Array<String>> bindings) {
		String schemaPackage = "";
		for (Array<String> entry : bindings) {
			if (entry.get(0).contains(".")) {
				schemaPackage = entry.get(0);
			} else {
				try {
					Class schemaClass = ClassReflection.forName(schemaPackage
							+ "." + entry.get(0));
					bind(ClassReflection.getSimpleName(schemaClass)
							.toLowerCase(), schemaClass);
				} catch (ReflectionException e) {
					Gdx.app.error("SchemaIO", "Error loading alias", e);
					return false;
				}
			}
		}
		return true;
	}

	public void bind(String alias, Class schemaClass) {
		addClassTag(alias, schemaClass);
	}
}
