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

import com.badlogic.gdx.backends.lwjgl.LwjglFrame;
import com.badlogic.gdx.math.Vector2;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.platform.AbstractPlatform;
import es.eucm.ead.engine.utils.SwingEDTUtils;
import es.eucm.network.JavaRequestHelper;
import es.eucm.network.requests.RequestHelper;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DesktopPlatform extends AbstractPlatform {

	private JFileChooser fileChooser;
	private LwjglFrame frame;
	private Vector2 screenDimensions;
	private Controller controller;
	private RequestHelper requestHelper;

	public DesktopPlatform() {
		requestHelper = new JavaRequestHelper();
		SwingEDTUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				fileChooser = new JFileChooser();
			}
		});
		screenDimensions = new Vector2();
	}

	public void setController(Controller controller) {
		this.controller = controller;
	}

	public void setFrame(LwjglFrame frame) {
		this.frame = frame;
	}

	@Override
	public void askForFile(FileChooserListener listener) {
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		showFileChooser(listener);
		// String file = showFileChooser();
		// listener.string(file);
	}

	@Override
	public void askForFolder(FileChooserListener listener) {
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		showFileChooser(listener);
	}

	/** Shows the file chooser **/
	private void showFileChooser(final FileChooserListener stringListener) {
		CutreFileChooser cutreFileChooser = new CutreFileChooser(stringListener);

		/*
		 * SwingEDTUtils.invokeLater(new Runnable() {
		 * 
		 * @Override public void run() { if
		 * (fileChooser.showOpenDialog(DesktopPlatform.this.frame) ==
		 * JFileChooser.APPROVE_OPTION) { String s =
		 * fileChooser.getSelectedFile().getAbsolutePath(); s =
		 * s.replaceAll("\\\\", "/"); stringListener.fileChosen(s); } else {
		 * stringListener.fileChosen(null); } } });
		 */
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
		screenDimensions.set(d.width, d.height);
		return screenDimensions;
	}

	@Override
	public RequestHelper getRequestHelper() {
		return requestHelper;
	}

	public LwjglFrame getFrame() {
		return frame;
	}

	public JFileChooser getFileChooser() {
		return fileChooser;
	}

	private class CutreFileChooser extends JFrame {
		private JTextField textField;
		private JButton ok;
		private FileChooserListener listener;

		public CutreFileChooser(FileChooserListener l) {
			this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			this.listener = l;
			this.setLayout(new BorderLayout());

			textField = new JTextField("File path here");
			add(textField, BorderLayout.CENTER);

			JPanel container = new JPanel();
			container.setLayout(new BorderLayout());
			ok = new JButton("Ok");
			ok.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// XXX FIXME this should be included in a method in a
					// "UTILS" class in ordcer to avoid repeat that code
					// again and again
					String s = textField.getText().replaceAll("\\\\", "/");
					String[] allS = s.split(",");
					System.out.println(s);
					for (String str : allS) {
						listener.fileChosen(str);
					}
					CutreFileChooser.this.dispose();
				}
			});
			container.add(ok, BorderLayout.EAST);
			add(container, BorderLayout.SOUTH);

			pack();
			setLocation(
					(Toolkit.getDefaultToolkit().getScreenSize().width - getWidth()) / 2,
					(Toolkit.getDefaultToolkit().getScreenSize().height - getHeight()) / 2);
			SwingEDTUtils.invokeLater(new Runnable() {
				@Override
				public void run() {
					setVisible(true);
				}
			});
		}
	}
}
