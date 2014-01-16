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
/***************************************************************************\
 *  @author Antonio Calvo Morata & Dan Cristian Rotaru						*
 *  																		*
 *  ************************************************************************\
 * 	This file is a prototype for eAdventure Mockup							*
 *  																		*
 *  ************************************************************************/

package es.eucm.ead.mockup.android;

import java.io.File;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import es.eucm.ead.editor.io.Platform.StringListener;
import es.eucm.ead.mockup.android.MockupActivity.ActivityResultListener;
import es.eucm.ead.mockup.core.facade.IActionResolver;
import es.eucm.ead.mockup.core.facade.IAnswerListener;

public class AndroidResolver implements IActionResolver {

	private MockupActivity activity;

	public AndroidResolver(MockupActivity activity) {
		this.activity = activity;
	}

	@Override
	public void showDecisionBox(final int questionNumber,
			final String alertBoxTitle, final String alertBoxQuestion,
			final String answerA, final String answerB, final IAnswerListener ql) {

		if (questionNumber == IAnswerListener.QUESTION_EXIT) {
			activity.post(new Runnable() {
				public void run() {
					new AlertDialog.Builder(activity).setTitle(alertBoxTitle)
							.setMessage(alertBoxQuestion).setPositiveButton(
									answerA,
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int whichButton) {
											ql
													.onReceiveAnswer(
															questionNumber,
															IAnswerListener.QUESTION_EXIT_ANSWER_YES);
											dialog.cancel();
										}
									}).setNegativeButton(answerB,
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int whichButton) {
											ql
													.onReceiveAnswer(
															questionNumber,
															IAnswerListener.QUESTION_EXIT_ANSWER_NO);
											dialog.cancel();
										}
									}).setCancelable(false).create().show();
				}
			});
		}
	}

	@Override
	public void askForFile(final StringListener stringListener) {
		final Intent intent = new Intent();
		intent.setType("*/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		activity.post(new Runnable() {
			@Override
			public void run() {
				final int askReequestCode = 0;
				activity.startActivityForResult(intent, askReequestCode,
						new ActivityResultListener() {
							@Override
							public void result(int requestCode, int resultCode,
									final Intent data) {
								if (data == null
										|| askReequestCode != requestCode)
									return;
								activity.post(new Runnable() {
									@Override
									public void run() {
										if (data.getDataString().startsWith(
												"content://")) {
											stringListener
													.string(getRealPathFromURI(data
															.getData()));
										} else {
											File f = new File(data.getData()
													.getPath());
											stringListener.string(f
													.getAbsolutePath());
										}
									}
								});
							}
						});
			}
		});
	}

	public String getRealPathFromURI(Uri uri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = activity.getContentResolver().query(uri, proj, null,
				null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		String path = cursor.getString(column_index);
		cursor.close();
		return path;
	}
}
