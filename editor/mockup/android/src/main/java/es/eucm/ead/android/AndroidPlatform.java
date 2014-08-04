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

import java.io.InputStream;
import java.util.List;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import es.eucm.ead.android.EditorActivity.ActivityResultListener;
import es.eucm.ead.editor.platform.AbstractPlatform;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.data.Dimension;
import es.eucm.network.requests.RequestHelper;

public class AndroidPlatform extends AbstractPlatform {

	private static final String IMAGE_TO_EDIT_MIME_TYPE = "image/*";
	private static final String PLATFORM_TAG = "AndroidPlatform";

	private static final int PICK_FILE = 0;
	private static final int EDIT_FILE = 1;

	private enum Editor {

		PIXLREXPRESS("Pixlr Express", "com.pixlr.express"), PHOTOEDITOR(
				"Photo Editor", "com.iudesk.android.photo.editor",
				"app.activity"), IMAGEEDITOR("Image Editor",
				"com.pcvirt.ImageEditor");

		private static Editor fromName(int idx) {
			return values()[idx];
		}

		private final String name, packageName, editPackage;

		private Editor(String name, String packageName) {
			this(name, packageName, packageName);
		}

		private Editor(String name, String packageName, String editPackage) {
			this.editPackage = editPackage;
			this.packageName = packageName;
			this.name = name;
		}
	}

	private final Vector2 screenDimensions;
	private final String[] names;

	public AndroidPlatform() {

		this.screenDimensions = new Vector2(1280f, 800f);

		Editor[] values = Editor.values();
		names = new String[values.length];
		for (int i = 0; i < values.length; ++i)
			names[i] = values[i].name;
	}

	@Override
	public void askForFile(final FileChooserListener listener) {

		final EditorActivity activity = (EditorActivity) Gdx.app;
		final Intent intent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		if (intent.resolveActivity(activity.getPackageManager()) != null) {
			activity.startActivityForResult(intent, PICK_FILE,
					new ActivityResultListener() {

						@Override
						public void result(int resultCode, final Intent data) {
							if (resultCode != EditorActivity.RESULT_OK)
								return;
							Gdx.app.postRunnable(new Runnable() {
								@Override
								public void run() {
									if (data != null) {
										listener.fileChosen(getStringFromIntent(
												activity, data));
									}
								}
							});
						}
					});
		}
	}

	@Override
	public void editImage(final I18N i18n, final String image,
			final FileChooserListener listener) {
		final EditorActivity activity = (EditorActivity) Gdx.app;

		activity.handler.post(new Runnable() {

			@Override
			public void run() {
				showItemsAlert(activity, i18n.m("edition.tool.editors"),
						i18n.m("edition.tool.chooseEditor"), names,
						new OnClickListener() {

							@Override
							public void onClick(DialogInterface diagIface,
									int idx) {
								Editor editor = Editor.fromName(idx);
								checkPackageInstalledAndStart(activity,
										editor.packageName, editor.editPackage,
										i18n, image, listener);
							}
						});
			}
		});
	}

	private void checkPackageInstalledAndStart(final EditorActivity activity,
			final String editorPackageName, final String editorEditPackage,
			I18N i18n, String image, final FileChooserListener listener) {
		if (isPackageInstalled(editorPackageName, activity)) {
			// The user has the selected editor installed, so
			// let's start the edition

			Uri imageToEditUri = Uri.fromFile(Gdx.files.absolute(image).file());
			final Intent editIntent = new Intent(Intent.ACTION_EDIT,
					imageToEditUri);

			editIntent.setDataAndType(imageToEditUri, IMAGE_TO_EDIT_MIME_TYPE);

			PackageManager packageMgr = activity.getPackageManager();
			List<ResolveInfo> activityList = packageMgr.queryIntentActivities(
					editIntent, 0);
			boolean componentFound = false;
			for (ResolveInfo app : activityList) {

				Gdx.app.log(PLATFORM_TAG,
						app.activityInfo.applicationInfo.toString() + " "
								+ app.activityInfo.name);

				if ((app.activityInfo.name).contains(editorEditPackage)) {
					ActivityInfo editActivity = app.activityInfo;
					ComponentName name = new ComponentName(
							editActivity.applicationInfo.packageName,
							editActivity.name);
					editIntent.setComponent(name);
					componentFound = true;
					break;
				}
			}
			if (activityList.isEmpty() || !componentFound) {
				Gdx.app.log(PLATFORM_TAG,
						"the Activity for the requested aplication was not found ");
				return;
			}

			activity.startActivityForResult(editIntent, EDIT_FILE,
					new ActivityResultListener() {

						@Override
						public void result(int resultCode, final Intent data) {
							Gdx.app.postRunnable(new Runnable() {
								@Override
								public void run() {
									String picturePath = null;
									if (data != null) {
										picturePath = getStringFromIntent(
												activity, data);
									}
									listener.fileChosen(picturePath);
								}
							});
						}
					});
		} else {
			// The user doesn't have the selected editor
			// installed, so let's ask him if he wants to install
			// it

			Gdx.app.log(PLATFORM_TAG,
					"the user doesn't have any supported editors");
			showMessageDialog(activity, i18n.m("edition.tool.editorNotFound"),
					i18n.m("edition.tool.installEditor"),
					i18n.m("general.accept"), i18n.m("general.cancel"),
					new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							try {
								// This URI will start GooglePlay application
								activity.startActivity(new Intent(
										Intent.ACTION_VIEW, Uri
												.parse("market://details?id="
														+ editorPackageName)));
							} catch (android.content.ActivityNotFoundException anfe) {
								// If the user doesn't have Google Play
								// application installed this will start the
								// default browser
								activity.startActivity(new Intent(
										Intent.ACTION_VIEW,
										Uri.parse("http://play.google.com/store/apps/details?id="
												+ editorPackageName)));
							}
						}
					});
		}
	}

	private boolean isPackageInstalled(String packageName, Context context) {
		PackageManager pm = context.getPackageManager();
		try {
			pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
	}

	private void showItemsAlert(final EditorActivity activity,
			final String title, final String text, final String[] items,
			final DialogInterface.OnClickListener listener) {

		AlertDialog.Builder builder = new AlertDialog.Builder(activity)
				.setTitle(title + ". " + text).setItems(items, listener);

		builder.create().show();
	}

	private void showMessageDialog(Context ctx, String title, String text,
			String ok, String cancel, OnClickListener okListener) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx)
				.setTitle(title).setMessage(text)
				// set positive button: Yes message
				.setPositiveButton(ok, okListener)
				.setNegativeButton(cancel, null);

		alertDialogBuilder.create().show();
	}

	private String getStringFromIntent(Context context, Intent data) {
		Uri selectedImage = data.getData();
		String[] filePathColumn = { MediaStore.Images.Media.DATA };
		Cursor cursor = context.getContentResolver().query(selectedImage,
				filePathColumn, null, null, null);
		cursor.moveToFirst();
		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		if (columnIndex == -1) {
			cursor.close();
			return null;
		}
		String picturePath = cursor.getString(columnIndex);
		cursor.close();
		return picturePath;
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
	public Dimension getImageDimension(InputStream imageInputStream) {
		// Do nothing
		return null;
	}
}
