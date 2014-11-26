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
package es.eucm.ead.repobuilder;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.engine.utils.ZipUtils;
import es.eucm.ead.engine.EngineDesktop;
import es.eucm.ead.engine.utils.SwingEDTUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Simple class meant to be used after
 * {@link es.eucm.ead.repobuilder.BuildRepoLibs} just to preview how the
 * produced libraries will look on mokap.
 * 
 * It opens a small window where a specific directory can be picked for scanning
 * library preview games (usually are stored under the preview/ subfolder in the
 * output repo folder). After scanning, it allows selecting one of the libraries
 * in the repo and preview it.
 * 
 * Created by jtorrente on 19/11/14.
 */
public class LibPreview extends JFrame {

	public static final int WIDTH = 500;
	public static final int HEIGHT = 800;
	public static final boolean RECURSIVELY_DEFAULT = true;

	private Array<FileHandle> availableLibs;
	private FileHandle selectedDir;

	private JComboBox libSelector;

	private JButton preview;

	private JButton directorySelector;

	private JFileChooser chooser;

	private boolean scanRecursively;

	private String startDir;

	public LibPreview(String startDir) {
		super("Lib preview selector");
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		scanRecursively = RECURSIVELY_DEFAULT;
		this.startDir = startDir;

		availableLibs = new Array<FileHandle>();
		libSelector = new JComboBox();
		libSelector.setEditable(false);
		libSelector.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateLibSelected();
			}
		});

		directorySelector = new JButton("Select dir to scan");
		directorySelector.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectDirToScan();
			}
		});

		preview = new JButton("Preview");
		preview.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				previewLib();
			}
		});
		updateOptionsAvailable();

		this.setLayout(new BorderLayout(10, 10));
		JPanel selectorPanel = new JPanel();
		selectorPanel.add(libSelector);
		add(selectorPanel, BorderLayout.NORTH);

		JPanel centralPanel = new JPanel();
		centralPanel.setLayout(new GridLayout(2, 1, 5, 0));
		centralPanel.add(directorySelector);
		add(centralPanel, BorderLayout.CENTER);

		JPanel runPanel = new JPanel();
		runPanel.add(preview);
		add(runPanel, BorderLayout.SOUTH);

		updateLibSelected();

		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		pack();
		setLocation(
				(Toolkit.getDefaultToolkit().getScreenSize().width - WIDTH) / 2,
				(Toolkit.getDefaultToolkit().getScreenSize().height - HEIGHT) / 2);
		SwingEDTUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				setVisible(true);
			}
		});
	}

	private void selectDirToScan() {
		// Open File chooser
		if (chooser == null) {
			chooser = new JFileChooser();
			chooser.setDialogTitle("Choose the directory to scan");
			if (startDir != null) {
				chooser.setCurrentDirectory(new File(startDir));
			}
		}
		if (chooser.showDialog(this, "Scan this directory") == JFileChooser.APPROVE_OPTION) {
			FileHandle selectedFile = new FileHandle(chooser.getSelectedFile());
			if (selectedFile.exists()) {
				if (selectedFile.isDirectory()) {
					selectedDir = selectedFile;
				} else {
					selectedDir = selectedFile.parent();
				}
				scanDirectory(selectedDir);
			}
			updateOptionsAvailable();
		}
	}

	private void scanDirectory(FileHandle selectedDirectory) {
		for (FileHandle child : selectedDirectory.list()) {
			if (child.isDirectory() && scanRecursively) {
				scanDirectory(child);
			} else if (child.extension().toLowerCase().endsWith("zip")) {
				availableLibs.add(child);
			}
		}
	}

	private void updateOptionsAvailable() {
		if (availableLibs.size > 0) {
			String[] optionNames = new String[availableLibs.size];
			for (int i = 0; i < availableLibs.size; i++) {
				optionNames[i] = availableLibs.get(i).name();
			}
			libSelector.setModel(new DefaultComboBoxModel(optionNames));
			libSelector.setEnabled(true);
		} else {
			libSelector.setModel(new DefaultComboBoxModel(
					new String[] { "-- Select directory to scan --" }));
			libSelector.setSelectedIndex(0);
			libSelector.setEnabled(false);
		}
		updateLibSelected();
	}

	private void updateLibSelected() {
		FileHandle selectedLib = (libSelector.getSelectedIndex() >= 0 && libSelector
				.getSelectedIndex() < availableLibs.size) ? availableLibs
				.get(libSelector.getSelectedIndex()) : null;
		if (selectedLib == null) {
			preview.setEnabled(false);
		} else {
			preview.setEnabled(true);
		}
	}

	private void previewLib() {
		final FileHandle libToPreview = availableLibs.get(libSelector
				.getSelectedIndex());
		if (libToPreview != null) {
			FileHandle temp = FileHandle.tempDirectory("preview");
			temp.mkdirs();
			ZipUtils.unzip(libToPreview, temp);

			EngineDesktop engine = new EngineDesktop(1800, 900) {
				@Override
				protected void dispose() {
					super.dispose();
					libToPreview.deleteDirectory();
				}
			};
			engine.run(temp.path(), false);
			Gdx.app.setLogLevel(Application.LOG_DEBUG);
			SwingEDTUtils.invokeLater(new Runnable() {
				@Override
				public void run() {
					dispose();
				}
			});
		}
	}

	public static void main(String[] args) {

		String startDir = null;
		if (args.length > 0) {
			startDir = args[0];
		}

		LibPreview libPreview = new LibPreview(startDir);

	}

}
