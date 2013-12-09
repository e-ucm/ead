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
package es.eucm.ead.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import es.eucm.ead.core.io.Platform;
import java.awt.Dimension;

import javax.swing.*;

public class DesktopPlatform implements Platform {

	private JFileChooser fileChooser = new JFileChooser();
    private JFrame frame;
    
    public void setFrame(JFrame frame) {
        this.frame = frame;
    }
    
	@Override
	public void askForFile(StringListener listener) {
		if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			listener.string(fileChooser.getSelectedFile().getAbsolutePath());
		} else {
			listener.string(null);
		}
	}

    @Override
    public void setTitle(String title) {
        frame.setTitle(title);
    }

    @Override
    public void setSize(int width, int height) {
        frame.setSize(width, height);        
    }

    @Override
    public Vector2 getSize() {
        Dimension d = frame.getSize();
        return new Vector2(d.width, d.height);
    }
}
