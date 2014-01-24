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
package es.eucm.ead.editor.io;

import com.badlogic.gdx.files.FileHandle;
import es.eucm.ead.editor.Editor;
import es.eucm.ead.editor.io.serializers.EImageSerializer;
import es.eucm.ead.engine.Factory;
import es.eucm.ead.engine.io.SchemaIO;
import es.eucm.ead.schema.actors.Scene;
import es.eucm.ead.schema.renderers.AtlasImage;
import es.eucm.ead.schema.renderers.Image;

public class EditorIO extends SchemaIO {

	private FileHandle temp;

	private boolean optimize;

	public EditorIO(Factory factory) {
		super(null, factory);
	}

	public boolean isOptimize() {
		return optimize;
	}

	public void setSerializers() {
		super.setSerializers(null, null);
		setSerializer(Image.class, new EImageSerializer(this));
	}

	public void save(Scene scene, String name, boolean optimize) {
		this.optimize = optimize;
		if (!name.endsWith(".json")) {
			name += ".json";
		}
		FileHandle fh = Editor.assets.resolve(name);
		FileHandle parent = fh.parent();
		temp = parent.child("temp/");
		temp.mkdirs();
		toJson(scene, fh);
		FileHandle atlas = parent.child("atlas/");
		atlas.mkdirs();
		/*
		 * Settings settings = new Settings(); settings.useIndexes = false;
		 * TexturePacker2.process(settings, temp.path(), atlas.path(),
		 * "scene.atlas");
		 */
		temp.deleteDirectory();
	}

	@Override
	public void writeValue(Object value, Class knownType, Class elementType) {
		if (isOptimize()) {
			value = Editor.conversor.convert(value);
		}
		super.writeValue(value, knownType, elementType);
	}

	public AtlasImage addToAtlas(Image object) {
		AtlasImage atlasImage = new AtlasImage();
		FileHandle image = Editor.assets.resolve(object.getUri());
		image.copyTo(temp);
		atlasImage.setName(image.nameWithoutExtension());
		atlasImage.setUri("atlas/scene.atlas");
		return atlasImage;
	}
}
