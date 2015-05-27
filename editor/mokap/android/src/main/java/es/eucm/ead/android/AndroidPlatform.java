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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.InputType;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.Toast;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.google.android.gms.analytics.Tracker;

import es.eucm.ead.android.EditorActivity.ActivityResultListener;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.DownloadManager;
import es.eucm.ead.editor.control.actions.editor.ShowToast;
import es.eucm.ead.editor.platform.MokapPlatform;
import es.eucm.ead.editor.platform.MokapPlatform.ImageCapturedListener.Result;
import es.eucm.ead.editor.utils.ProjectUtils;
import es.eucm.i18n.I18N;
import es.eucm.ead.engine.android.AndroidImageUtils;
import es.eucm.ead.engine.assets.GameAssets.ImageUtils;
import es.eucm.ead.schemax.ModelStructure;

public class AndroidPlatform extends MokapPlatform {

	private static final String IMAGE_TO_EDIT_MIME_TYPE = "image/*";
	private static final String PLATFORM_TAG = "AndroidPlatform";

	private static final int PICK_FILE = 0;
	private static final int EDIT_FILE = 1;
	private static final int CAPTURE_PHOTO = 2;
	private static final int SEND_PROJECT = 3;

	private enum Editor {

		PIXLREXPRESS("Pixlr Express", "com.pixlr.express");

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

	private Context context;

	private Tracker tracker;

	private ImageUtils imageUtils;

	public AndroidPlatform(Context context, Tracker tracker) {
		this.tracker = tracker;
		this.context = context;
	}

	@Override
	public void setBatch(Batch batch) {
		super.setBatch(batch);
		imageUtils = new AndroidImageUtils();
	}

	@Override
	public void askForFile(Controller controller, FileChooserListener listener) {
		String pathColumn = MediaStore.Images.Media.DATA;
		Intent intent = new Intent(android.content.Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		selectFile(controller, listener, pathColumn, intent);
	}

	@Override
	public void askForAudio(Controller controller, FileChooserListener listener) {
		String pathColumn = MediaStore.Audio.Media.DATA;
		I18N i18n = controller.getApplicationAssets().getI18N();
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_GET_CONTENT);
		intent.setType("audio/*");
		intent = Intent.createChooser(intent, i18n.m("sound"));
		selectFile(controller, listener, pathColumn, intent);
	}

	private void selectFile(Controller controller,
			FileChooserListener listener, String pathColumn, Intent intent) {

		EditorActivity activity = (EditorActivity) Gdx.app;
		if (intent.resolveActivity(activity.getPackageManager()) != null) {
			activity.startActivityForResult(intent, PICK_FILE,
					new FileResultListener(controller, listener, pathColumn,
							this));
		} else {
			listener.fileChosen(null, FileChooserListener.Result.NOT_FOUND);
		}
	}

	@Override
	public void editImage(Controller controller, String imagePath,
			FileChooserListener listener) {
		Editor editor = Editor.PIXLREXPRESS;
		checkPackageInstalledAndStart(editor, controller, imagePath, listener);
	}

	private void checkPackageInstalledAndStart(Editor editor,
			Controller controller, String imagePath,
			FileChooserListener listener) {

		final EditorActivity activity = (EditorActivity) Gdx.app;
		final I18N i18n = controller.getApplicationAssets().getI18N();

		if (isPackageInstalled(editor.packageName, activity)) {
			// The user has the selected editor installed, so
			// let's start the edition

			Uri imageToEditUri = Uri.fromFile(Gdx.files.absolute(imagePath)
					.file());
			Intent editIntent = new Intent(Intent.ACTION_EDIT, imageToEditUri);

			editIntent.setDataAndType(imageToEditUri, IMAGE_TO_EDIT_MIME_TYPE);

			PackageManager packageMgr = activity.getPackageManager();
			List<ResolveInfo> activityList = packageMgr.queryIntentActivities(
					editIntent, 0);
			boolean componentFound = false;
			for (ResolveInfo app : activityList) {

				Gdx.app.log(PLATFORM_TAG,
						app.activityInfo.applicationInfo.toString() + " "
								+ app.activityInfo.name);

				if ((app.activityInfo.name).contains(editor.editPackage)) {
					ActivityInfo editActivity = app.activityInfo;
					ComponentName name = new ComponentName(
							editActivity.applicationInfo.packageName,
							editActivity.name);
					editIntent.setComponent(name);
					componentFound = true;
					break;
				}
			}
			if (!componentFound) {
				Gdx.app.log(PLATFORM_TAG,
						"the Activity for the requested aplication was not found ");
				listener.fileChosen(null, FileChooserListener.Result.NOT_FOUND);
				return;
			}

			if (imagePath.endsWith(".png")) {
				activity.post(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(activity, i18n.m("save.as.png"),
								Toast.LENGTH_LONG).show();
					}
				});
			}

			activity.startActivityForResult(editIntent, EDIT_FILE,
					new FileResultListener(controller, listener,
							MediaStore.Images.Media.DATA, this));
		} else {
			// The user doesn't have the selected editor
			// installed, so let's ask him if he wants to install
			// it

			Gdx.app.log(PLATFORM_TAG,
					"the user doesn't have the editor intalled");

			activity.post(new AskForInstall(editor, i18n, this));
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

	private void showMessageDialog(Context ctx, String title, String text,
			String ok, String cancel, OnClickListener okListener) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx)
				.setTitle(title).setMessage(text)
				// set positive button: Yes message
				.setPositiveButton(ok, okListener)
				.setNegativeButton(cancel, null);

		alertDialogBuilder.create().show();
	}

	private String getPathFromIntent(Context context, Intent data,
			String pathColumn) {

		Uri uri = data.getData();
		if ("content".equalsIgnoreCase(uri.getScheme())) {

			// Return the remote address
			if (isGooglePhotosUri(uri))
				return null;

			return getDataColumn(context, uri, null, null);
		} else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	private boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri
				.getAuthority());
	}

	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 * 
	 * @param context
	 *            The context.
	 * @param uri
	 *            The Uri to query.
	 * @param selection
	 *            (Optional) Filter used in the query.
	 * @param selectionArgs
	 *            (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 */
	private String getDataColumn(Context context, Uri uri, String selection,
			String[] selectionArgs) {

		Cursor cursor = null;
		String column = "_data";
		String[] projection = { column };

		try {
			cursor = context.getContentResolver().query(uri, projection,
					selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {

				int column_index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(column_index);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return null;
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
	public es.eucm.ead.editor.control.Tracker createTracker(
			Controller controller) {
		return new GATracker(context, controller, tracker);
	}

	@Override
	public void captureImage(final FileHandle photoFile,
			final ImageCapturedListener listener) {
		final EditorActivity activity = (EditorActivity) Gdx.app;
		if (activity.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			Intent takePictureIntent = new Intent(
					MediaStore.ACTION_IMAGE_CAPTURE);

			// Ensure that there's a camera activity to handle the intent
			if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
				// We need to create an empty file if it doesn't exist in order
				// to avoid a known bug in some devices with the camera intent
				if (!photoFile.exists()) {
					photoFile.parent().mkdirs();
					try {
						photoFile.file().createNewFile();
					} catch (IOException ioex) {
						listener.imageCaptured(Result.UNKOWN);
						Gdx.app.error(PLATFORM_TAG,
								"Failed to create an empty photo file:  "
										+ ioex);
						return;
					}
				}
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(photoFile.file()));
				activity.startActivityForResult(takePictureIntent,
						CAPTURE_PHOTO, new ActivityResultListener() {

							@Override
							public void result(final int resultCode, Intent data) {
								Gdx.app.postRunnable(new Runnable() {

									@Override
									public void run() {
										if (resultCode == Activity.RESULT_OK) {
											listener.imageCaptured(Result.SUCCES);
										} else {
											photoFile.delete();
											listener.imageCaptured(Result.UNKOWN);
										}
									}
								});
							}
						});
			} else {
				listener.imageCaptured(Result.NO_APP);
			}
		} else {
			listener.imageCaptured(Result.NO_CAMERA);
		}
	}

	@Override
	public void getMultilineTextInput(final TextInputListener listener,
			final String title, final String text, final I18N i18n) {
		final EditorActivity activity = (EditorActivity) Gdx.app;
		activity.post(new Runnable() {
			public void run() {
				AlertDialog.Builder alert = new AlertDialog.Builder(activity);
				alert.setTitle(title);
				final EditText input = new EditText(activity);
				input.setText(text);
				input.setSingleLine(false);
				input.setHorizontalScrollBarEnabled(false);
				input.setVerticalScrollBarEnabled(true);
				input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
						| InputType.TYPE_TEXT_FLAG_MULTI_LINE
						| InputType.TYPE_CLASS_TEXT);
				alert.setView(input);
				alert.setPositiveButton(i18n.m("accept"),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								Gdx.app.postRunnable(new Runnable() {
									@Override
									public void run() {
										listener.input(input.getText()
												.toString());
									}
								});
							}
						});
				alert.setOnCancelListener(new OnCancelListener() {
					@Override
					public void onCancel(DialogInterface arg0) {
						Gdx.app.postRunnable(new Runnable() {
							@Override
							public void run() {
								listener.canceled();
							}
						});
					}
				});
				alert.show();
			}
		});

	}

	@Override
	public void sendMail(FileHandle projectHandle, Controller controller) {
		I18N i18n = controller.getApplicationAssets().getI18N();
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setData(Uri.parse("mailto:"));
		intent.setType("message/rfc822"); // e-mail MIME-type
		intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "android@e-ucm.es" });
		intent.putExtra(Intent.EXTRA_SUBJECT, i18n.m("about.emailSubject"));
		intent.setClassName("com.google.android.gm",
				"com.google.android.gm.ComposeActivityGmail");
		if (projectHandle != null) {
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			intent.putExtra(Intent.EXTRA_STREAM,
					Uri.fromFile(projectHandle.file()));
		}
		final EditorActivity activity = (EditorActivity) Gdx.app;
		if (intent.resolveActivity(activity.getPackageManager()) != null) {
			activity.startActivity(intent);
		}

	}

	@Override
	public void sendMail(Controller controller) {
		sendMail(null, controller);
	}

	@Override
	public boolean isConnected() {
		Context context = ((EditorActivity) Gdx.app).getContext();
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo state = connectivity.getActiveNetworkInfo();

		return state != null && state.isConnected();
	}

	public void sendProject(FileHandle projectHandle, I18N i18n) {

		EditorActivity activity = (EditorActivity) Gdx.app;
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		MimeTypeMap mime = MimeTypeMap.getSingleton();
		String type = mime.getMimeTypeFromExtension(projectHandle.extension());
		shareIntent.setType(type);
		shareIntent.putExtra(
				android.content.Intent.EXTRA_SUBJECT,
				i18n.m("send.subject") + ": "
						+ projectHandle.nameWithoutExtension());
		shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,
				i18n.m("send.message"));
		shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		shareIntent.putExtra(Intent.EXTRA_STREAM,
				Uri.fromFile(projectHandle.file()));

		if (shareIntent.resolveActivity(activity.getPackageManager()) != null) {
			activity.startActivityForResult(
					Intent.createChooser(shareIntent, i18n.m("send.share")),
					SEND_PROJECT);
		}

	}

	@Override
	public String getLocale() {
		return Locale.getDefault().toString();
	}

	@Override
	public boolean isDebug() {
		return (0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
	}

	@Override
	public ImageUtils getImageUtils() {
		return imageUtils;
	}

	private static class FileResultListener implements ActivityResultListener {

		private AndroidPlatform androidPlatform;
		private FileChooserListener listener;
		private String pathColumn;
		private Controller controller;
		private I18N i18N;

		public FileResultListener(Controller controller,
				FileChooserListener listener, String pathColumn,
				AndroidPlatform androidPlatform) {
			this.controller = controller;
			this.i18N = controller.getApplicationAssets().getI18N();
			this.listener = listener;
			this.pathColumn = pathColumn;
			this.androidPlatform = androidPlatform;
		}

		@Override
		public void result(int resultCode, final Intent intent) {
			if (resultCode == EditorActivity.RESULT_OK) {
				if (intent != null) {
					Gdx.app.postRunnable(new Runnable() {

						@Override
						public void run() {
							EditorActivity activity = (EditorActivity) Gdx.app;
							String path = androidPlatform.getPathFromIntent(
									activity, intent, pathColumn);
							if (path == null) {

								if (!((MokapPlatform) controller.getPlatform())
										.isConnected()) {
									showToast(FileChooserListener.Result.NO_CONNECTION
											.getI18nKey());
								} else {
									String url = intent.getDataString();
									try {
										Context context = ((EditorActivity) Gdx.app)
												.getContext();
										InputStream input = context
												.getContentResolver()
												.openInputStream(Uri.parse(url));
										final FileHandle file = ProjectUtils.getNonExistentFile(
												controller
														.getApplicationAssets()
														.absolute(
																controller
																		.getLoadingPath()
																		+ ModelStructure.IMAGES_FOLDER),
												i18N.m("image"), ".png");
										controller
												.getDownloadManager()
												.download(
														new DownloadManager.DownloadWork(
																createDownloadListener(file),
																input, file));
									} catch (FileNotFoundException e) {
										showToast(FileChooserListener.Result.NOT_FOUND
												.getI18nKey());
									}
								}

							} else {
								listener.fileChosen(path,
										FileChooserListener.Result.SUCCESS);
							}
						}
					});
				} else {
					showToast(FileChooserListener.Result.NOT_FOUND.getI18nKey());
				}
			} else {
				showToast(FileChooserListener.Result.NOT_FOUND.getI18nKey());
			}
		}

		private void showToast(String i18nKey) {
			controller.action(ShowToast.class, i18N.m(i18nKey));
		}

		private DownloadManager.DownloadListener createDownloadListener(
				final FileHandle file) {
			return new DownloadManager.DownloadListener() {
				@Override
				public void queued() {

				}

				@Override
				public void started() {

				}

				@Override
				public void completion(float completion) {

				}

				@Override
				public void downloaded() {
					listener.fileChosen(file.path(),
							FileChooserListener.Result.SUCCESS);
				}

				@Override
				public void cancelled() {
					showToast("download.cancelled");
				}

				@Override
				public void error() {
					showToast("download.error");
				}
			};
		}
	}

	private static class AskForInstall implements Runnable {

		private I18N i18n;
		private Editor editor;
		private AndroidPlatform androidPlatform;

		public AskForInstall(Editor editor, I18N i18n,
				AndroidPlatform androidPlatform) {
			this.androidPlatform = androidPlatform;
			this.editor = editor;
			this.i18n = i18n;
		}

		@Override
		public void run() {
			final EditorActivity activity = (EditorActivity) Gdx.app;
			androidPlatform.showMessageDialog(activity, editor.name + " "
					+ i18n.m("not.found").toLowerCase(), i18n.m("install.it"),
					i18n.m("accept"), i18n.m("cancel"), new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							try {
								// This URI will start GooglePlay
								// application
								activity.startActivity(new Intent(
										Intent.ACTION_VIEW, Uri
												.parse("market://details?id="
														+ editor.packageName)));
							} catch (android.content.ActivityNotFoundException anfe) {
								// If the user doesn't have Google Play
								// application installed this will start
								// the
								// default browser
								activity.startActivity(new Intent(
										Intent.ACTION_VIEW,
										Uri.parse("http://play.google.com/store/apps/details?id="
												+ editor.packageName)));
							}
						}
					});
		}
	}
}
