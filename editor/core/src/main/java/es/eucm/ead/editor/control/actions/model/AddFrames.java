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
package es.eucm.ead.editor.control.actions.model;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.assets.EditorGameAssets;
import es.eucm.ead.editor.control.actions.ModelAction;
import es.eucm.ead.editor.control.commands.Command;
import es.eucm.ead.editor.control.commands.CompositeCommand;
import es.eucm.ead.editor.control.commands.ListCommand.AddToListCommand;
import es.eucm.ead.schema.renderers.Frame;
import es.eucm.ead.schema.renderers.Image;

/**
 * Adds {@link Frame frames} to an {@link Array} from a given file. If the file
 * name ends with "01" it will also try to add the next frames. (e.g. 02, 03,
 * 04...).
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> <em>{@link String}</em> The path to the file
 * (.png, .jpg...)</dd>
 * <dd><strong>args[1]</strong> <em>{@link Object}</em> Owner of the list, may
 * be null</dd>
 * <dd><strong>args[2]</strong> <em>{@link Array}</em> The list</dd>
 * </dl>
 */
public class AddFrames extends ModelAction {

	/**
	 * 0.4 seconds as default duration.
	 */
	private static final float DEFAULT_TIME = .4F;

	public AddFrames() {
		super(true, true, String.class, Object.class, Array.class);
	}

	@Override
	public Command perform(Object... args) {
		String path = args[0].toString();
		Object owner = args[1];
		Array list = (Array) args[2];

		EditorGameAssets gameAssets = controller.getEditorGameAssets();
		FileHandle file = gameAssets.resolve(path);

		CompositeCommand composite = new CompositeCommand();
		String name = null;
		int i = 0, j = 1;
		do {

			name = file.nameWithoutExtension();
			path = gameAssets.copyToProjectIfNeeded(file.path(), Texture.class);

			Frame frame = new Frame();
			Image image = new Image();
			image.setUri(path);
			frame.setRenderer(image);
			frame.setTime(DEFAULT_TIME);

			composite.addCommand(new AddToListCommand(owner, list, frame));

			if (name.endsWith("" + i + j)) {
				if (++j % 10 == 0) {
					++i;
					j = 0;
				}
				String nextSuffix = "" + i + j;

				name = name.substring(0, name.length() - nextSuffix.length());
				file = file.sibling(name + nextSuffix + "." + file.extension());
			} else {
				break;
			}
		} while (file.exists());

		return composite;
	}

}
