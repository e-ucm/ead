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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.text.InputType;
import android.widget.EditText;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.google.android.gms.analytics.Tracker;
import es.eucm.ead.android.EditorActivity.ActivityResultListener;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.platform.MokapPlatform;
import es.eucm.ead.editor.platform.MokapPlatform.ImageCapturedListener.Result;
import es.eucm.ead.editor.utils.ProjectUtils;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.engine.android.AndroidImageUtils;
import es.eucm.ead.engine.assets.GameAssets.ImageUtils;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AndroidPlatform extends MokapPlatform {

	private static final String IMAGE_TO_EDIT_MIME_TYPE = "image/*";
	private static final String PLATFORM_TAG = "AndroidPlatform";

	private static final int PICK_FILE = 0;
	private static final int EDIT_FILE = 1;
	private static final int CAPTURE_PHOTO = 2;
	private static final int SEND_PROJECT = 3;

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

	private Context context;

	private Tracker tracker;

	private final String[] names;

	private ImageUtils imageUtils;

	public AndroidPlatform(Context context, Tracker tracker) {
		this.tracker = tracker;
		this.context = context;

		Editor[] values = Editor.values();
		names = new String[values.length];
		for (int i = 0; i < values.length; ++i)
			names[i] = values[i].name;
	}

	@Override
	public void setBatch(Batch batch) {
		super.setBatch(batch);
		imageUtils = new AndroidImageUtils();
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
							if (resultCode == EditorActivity.RESULT_OK) {
								if (data != null) {
									String path = getStringFromIntent(activity,
											data, MediaStore.Images.Media.DATA);
									if (path != null) {
										FileHandle file = Gdx.files
												.absolute(path);
										if (file.exists()) {
											new DecodePictureTask(listener,
													file).execute();
										} else {
											listener.fileChosen(null);
										}
									} else {
										listener.fileChosen(null);
									}
								}
							}
						}
					});
		}
	}

	@Override
	public void askForAudio(final Controller controller,
			final FileChooserListener listener) {
		I18N i18n = controller.getApplicationAssets().getI18N();
		final EditorActivity activity = (EditorActivity) Gdx.app;
		// final Intent intent = new Intent(Intent.ACTION_PICK,
		// android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_GET_CONTENT);
		intent.setType("audio/*");
		if (intent.resolveActivity(activity.getPackageManager()) != null) {
			activity.startActivityForResult(
					Intent.createChooser(intent,
							i18n.m("edition.selectionAudio")), PICK_FILE,
					new ActivityResultListener() {

						@Override
						public void result(int resultCode, final Intent data) {
							if (resultCode == EditorActivity.RESULT_OK) {
								if (data != null) {
									String path = getStringFromIntent(
											controller, activity, data,
											MediaStore.Audio.Media.DATA);
									listener.fileChosen(path);
								} else {
									listener.fileChosen(null);
								}
							} else {
								listener.fileChosen(null);
							}
						}
					});
		} else {
			listener.fileChosen(null);
		}
	}

	@Override
	public void editImage(final I18N i18n, final String image,
			final FileChooserListener listener) {
		final EditorActivity activity = (EditorActivity) Gdx.app;

		activity.handler.post(new Runnable() {

			@Override
			public void run() {
				showItemsAlert(activity, i18n.m("edition.chooseEditor"), names,
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
												activity, data,
												MediaStore.Images.Media.DATA);
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
			showMessageDialog(activity, i18n.m("edition.editorNotFound"),
					i18n.m("edition.installEditor"), i18n.m("accept"),
					i18n.m("cancel"), new OnClickListener() {

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

	private void showItemsAlert(EditorActivity activity, String text,
			String[] items, DialogInterface.OnClickListener listener) {

		AlertDialog.Builder builder = new AlertDialog.Builder(activity)
				.setTitle(text).setItems(items, listener);

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

	private String getStringFromIntent(Context context, Intent data,
			String pathColumn) {
		return this.getStringFromIntent(null, context, data, pathColumn);
	}

	private String getStringFromIntent(Controller controller, Context context,
			Intent data, String pathColumn) {
		try {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { pathColumn };
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
		} catch (Exception e) {
			Gdx.app.error(PLATFORM_TAG, "Path could not be resolved", e);
			return null;
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
							public void result(int resultCode, Intent data) {
								if (resultCode == Activity.RESULT_OK) {
									new DecodePictureTask(listener, photoFile)
											.execute();
								} else {
									photoFile.delete();
								}
							}
						});
			} else {
				listener.imageCaptured(Result.NO_APP);
			}
		} else {
			listener.imageCaptured(Result.NO_CAMERA);
		}
	}

	public class DecodePictureTask extends AsyncTask<Void, Void, Boolean> {

		private final PostRunnable postRunnable = new PostRunnable();
		private ProgressDialog mPleaseWaitDialog = null;
		private ImageCapturedListener captureListener;
		private FileChooserListener fileListener;
		private FileHandle file;

		public DecodePictureTask(FileChooserListener listener, FileHandle file) {
			this.fileListener = listener;
			this.file = file;
		}

		public DecodePictureTask(ImageCapturedListener listener, FileHandle file) {
			this.captureListener = listener;
			this.file = file;
		}

		public void showDecodingDialog() {
			if (mPleaseWaitDialog != null) {
				return;
			}

			mPleaseWaitDialog = new ProgressDialog((EditorActivity) Gdx.app);
			mPleaseWaitDialog.setIndeterminate(true);
			mPleaseWaitDialog.show();
		}

		public void cancelDecodingDialog() {
			if (mPleaseWaitDialog != null) {
				mPleaseWaitDialog.dismiss();
				mPleaseWaitDialog = null;
			}
		}

		@Override
		protected void onPostExecute(Boolean success) {
			super.onPostExecute(success);
			cancelDecodingDialog();
			postRunnable.setSuccess(success);
			Gdx.app.postRunnable(postRunnable);
		}

		private class PostRunnable implements Runnable {

			private boolean success;

			public void setSuccess(boolean success) {
				this.success = success;
			}

			@Override
			public void run() {
				if (captureListener != null) {
					Result result = Result.SUCCES;
					if (!success) {
						result = Result.UNKOWN;
					}
					captureListener.imageCaptured(result);
				} else if (fileListener != null) {
					fileListener.fileChosen(file.file().getAbsolutePath());
					if (file.exists()) {
						file.delete();
					}
				}
				captureListener = null;
				fileListener = null;
				file = null;
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showDecodingDialog();
		}

		@Override
		protected Boolean doInBackground(Void... args) {
			FileHandle sourceImage = file;
			file = null;
			if (captureListener == null) {
				file = ProjectUtils.getNonExistentFile(sourceImage.parent(),
						sourceImage.nameWithoutExtension(),
						sourceImage.extension());
			}

			return getImageUtils().scale(sourceImage, file) != -1;
		}
	};

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

	public void sendProject(FileHandle projectHandle, I18N i18n,
			final ProjectSentListener listener) {
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("*/*");
		shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
				i18n.m("send.subject"));
		shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,
				i18n.m("send.message"));
		shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		shareIntent.putExtra(Intent.EXTRA_STREAM,
				Uri.fromFile(projectHandle.file()));

		final EditorActivity activity = (EditorActivity) Gdx.app;
		if (shareIntent.resolveActivity(activity.getPackageManager()) != null) {
			activity.startActivityForResult(
					Intent.createChooser(shareIntent, i18n.m("send.share")),
					SEND_PROJECT, new ActivityResultListener() {

						@Override
						public void result(int resultCode, final Intent data) {
							if (resultCode != EditorActivity.RESULT_OK) {
								listener.projectSent(false);
							} else {
								listener.projectSent(true);
							}
						}
					});
		} else {
			listener.projectSent(false);
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
}
