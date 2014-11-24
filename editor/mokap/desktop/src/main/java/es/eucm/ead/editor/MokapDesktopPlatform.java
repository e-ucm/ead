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
package es.eucm.ead.editor;

import java.awt.FlowLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.backends.lwjgl.LwjglFrame;
import com.badlogic.gdx.files.FileHandle;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.platform.MokapPlatform;
import es.eucm.ead.editor.platform.MokapPlatform.ImageCapturedListener.Result;
import es.eucm.ead.engine.I18N;

public class MokapDesktopPlatform extends MokapPlatform {

	@Override
	public void setTitle(String title) {

	}

	@Override
	public void setSize(int width, int height) {

	}

	public LwjglFrame getFrame() {
		return null;
	}

	@Override
	public void askForFile(final FileChooserListener listener) {
		Gdx.input.getTextInput(new TextInputListener() {

			@Override
			public void input(final String text) {
				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						listener.fileChosen(text);

					}
				});
			}

			@Override
			public void canceled() {
			}

		}, "File path!", "", "");
	}

	@Override
	public void askForAudio(Controller controller,
			final FileChooserListener listener) {
		Gdx.input.getTextInput(new TextInputListener() {

			@Override
			public void input(String text) {
				listener.fileChosen(text);
			}

			@Override
			public void canceled() {
			}

		}, "File path!", "", "");
	}

	@Override
	public void captureImage(FileHandle photoFile,
			final ImageCapturedListener listener) {
		Gdx.app.postRunnable(new Runnable() {

			@Override
			public void run() {
				listener.imageCaptured(Result.NO_CAMERA);
			}
		});
	}

	@Override
	public void getMultilineTextInput(final TextInputListener listener,
			final String title, final String text, I18N i18n) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JPanel panel = new JPanel(new FlowLayout());

				final JTextArea textArea = new JTextArea(text);
				panel.add(textArea);

				JOptionPane pane = new JOptionPane(panel,
						JOptionPane.QUESTION_MESSAGE,
						JOptionPane.OK_CANCEL_OPTION, null, null, null);

				pane.setInitialValue(null);
				pane.setComponentOrientation(JOptionPane.getRootFrame()
						.getComponentOrientation());

				JDialog dialog = pane.createDialog(null, title);
				pane.selectInitialValue();

				dialog.addWindowFocusListener(new WindowFocusListener() {

					@Override
					public void windowLostFocus(WindowEvent arg0) {
					}

					@Override
					public void windowGainedFocus(WindowEvent arg0) {
						textArea.requestFocusInWindow();
					}
				});

				dialog.setVisible(true);
				dialog.dispose();

				Object selectedValue = pane.getValue();

				if (selectedValue != null
						&& (selectedValue instanceof Integer)
						&& ((Integer) selectedValue).intValue() == JOptionPane.OK_OPTION) {
					Gdx.app.postRunnable(new Runnable() {

						@Override
						public void run() {
							listener.input(textArea.getText());
						}
					});
				} else {
					Gdx.app.postRunnable(new Runnable() {

						@Override
						public void run() {
							listener.canceled();
						}
					});
				}

			}
		});
	}

	@Override
	public boolean scaleImage(FileHandle imageFile, int maxWidth,
			int maxHeight, FileHandle resultImage) {
		imageFile.copyTo(resultImage);
		return true;
	}
}
