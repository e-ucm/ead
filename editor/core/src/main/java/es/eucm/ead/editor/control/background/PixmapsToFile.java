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
package es.eucm.ead.editor.control.background;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.PixmapIO;

/**
 * Creates a new Pixmap with a set of pixmaps and then writes it to a file.
 * Pixmaps are expected to come slice as columns of the total pixmap
 */
public class PixmapsToFile extends BackgroundTask<String> {

	private Pixmap[] pixmaps;

	private FileHandle path;

	public PixmapsToFile(Pixmap[] pixmaps, FileHandle path) {
		this.pixmaps = pixmaps;
		this.path = path;
	}

	@Override
	public String call() throws Exception {
		int height = 0;
		int width = 0;
		for (Pixmap pixmap : pixmaps) {
			height = Math.max(height, pixmap.getHeight());
			width += pixmap.getWidth();
		}

		Pixmap pixmap = new Pixmap(width, height, Format.RGB888);
		int xOffset = 0;
		for (Pixmap p : pixmaps) {
			pixmap.drawPixmap(p, xOffset, 0);
			xOffset += p.getWidth();
			p.dispose();
		}

		PixmapIO.writePNG(path, pixmap);
		pixmap.dispose();

		return path.path();
	}
}
