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
package es.eucm.ead.android;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.Display;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import es.eucm.ead.android.EditorActivity.ActivityResultListener;
import es.eucm.ead.editor.platform.Platform;
import es.eucm.ead.editor.platform.Platform.StringListener;

import java.io.File;

public class AndroidPlatform implements Platform, StringListener {

	private Vector2 screenDimensions;

	private StringListener folderStringListener;

	public AndroidPlatform() {
		screenDimensions = new Vector2();
	}

	@Override
	public void askForFile(final StringListener listener) {
		EditorActivity activity = (EditorActivity) Gdx.app;
		Intent intent = new Intent();
		intent.setType("*/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		activity.startActivityForResult(intent, 0,
				new ActivityResultListener() {
					@Override
					public void result(int resultCode, final Intent data) {
						Gdx.app.postRunnable(new Runnable() {
							@Override
							public void run() {
								if (data.getDataString().startsWith(
										"content://")) {
									listener.string(getRealPathFromURI(data
											.getData()));
								} else {
									File f = new File(data.getData().getPath());
									listener.string(f.getAbsolutePath());
								}
							}
						});
					}
				});

	}

	@Override
	public void askForFolder(StringListener listener) {
		this.folderStringListener = listener;
		askForFile(this);
	}

	@Override
	public void string(String result) {
		if (result != null) {
			// Check if selected file is a folder. If it's not, return null
			FileHandle fh = Gdx.files.absolute(result);
			if (!fh.exists() || !fh.isDirectory()) {
				result = null;
			}
		}
		folderStringListener.string(result);
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
		AndroidApplication app = (AndroidApplication) Gdx.app;
		Display display = app.getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		screenDimensions.set(size.x, size.y);
		return screenDimensions;
	}

	public String getRealPathFromURI(Uri uri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = ((EditorActivity) Gdx.app).getContentResolver().query(
				uri, proj, null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		String path = cursor.getString(column_index);
		cursor.close();
		return path;
	}

}
