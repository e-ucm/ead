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

package es.eucm.ead.editor.picture;

import com.badlogic.gdx.Gdx;

import es.eucm.ead.editor.platform.mockup.DevicePictureControl;


public class DesktopDevicePictureController implements DevicePictureControl{

	@Override
	public void takePicture() {
		Gdx.app.log("Picture", "takePicture()");		
	}
	
	@Override
	public void startPreviewAsync() {
		Gdx.app.log("Picture", "startPreviewAsync()");			
	}

	@Override
	public void stopPreviewAsync() {
		Gdx.app.log("Picture", "stopPreviewAsync()");			
	}

	@Override
	public void takePictureAsync() {
		Gdx.app.log("Picture", "takePictureAsync()");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isReady() {
		Gdx.app.log("Picture", "isReady()");
		return true;
	}

	@Override
	public void prepareCameraAsync() {
		Gdx.app.log("Picture", "prepareCameraAsync()");
		
	}
}
