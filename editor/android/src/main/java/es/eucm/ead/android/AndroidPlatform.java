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
package es.eucm.ead.android;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import es.eucm.ead.android.EditorActivity.ActivityResultListener;
import es.eucm.ead.editor.platform.AbstractPlatform;
import es.eucm.network.requests.RequestHelper;

public class AndroidPlatform extends AbstractPlatform {

	private final Vector2 screenDimensions;

	public AndroidPlatform() {
		this.screenDimensions = new Vector2(1280f, 800f);
	}

	@Override
	public void askForFile(final FileChooserListener listener) {
		final EditorActivity activity = (EditorActivity) Gdx.app;
		final Intent intent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		if (intent.resolveActivity(activity.getPackageManager()) != null) {
			activity.startActivityForResult(intent, 0,
					new ActivityResultListener() {
						@Override
						public void result(int resultCode, final Intent data) {
							if (resultCode != EditorActivity.RESULT_OK)
								return;
							Gdx.app.postRunnable(new Runnable() {
								@Override
								public void run() {
									Uri selectedImage = data.getData();
									String[] filePathColumn = { MediaStore.Images.Media.DATA };
									Cursor cursor = activity
											.getContentResolver().query(
													selectedImage,
													filePathColumn, null, null,
													null);
									cursor.moveToFirst();
									int columnIndex = cursor
											.getColumnIndex(filePathColumn[0]);
									if (columnIndex == -1) {
										cursor.close();
										return;
									}
									String picturePath = cursor
											.getString(columnIndex);
									cursor.close();
									listener.fileChosen(picturePath);
								}
							});
						}
					});
		}

	}

	@Override
	public void askForFolder(FileChooserListener listener) {
		// Do nothing, never used in Android
	}

	@Override
	public void setTitle(String title) {
		// Do nothing
	}

	@Override
	public void setSize(int width, int height) {
		// Do nothing
	}

	@Override
	public Vector2 getSize() {
		return this.screenDimensions;
	}

	@Override
	public RequestHelper getRequestHelper() {
		// Do nothing
		return null;
	}

	@Override
	public boolean browseURL(String URL) {
		// Do nothing
		return false;
	}
}
