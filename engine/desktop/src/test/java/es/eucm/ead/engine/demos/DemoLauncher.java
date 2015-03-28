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
package es.eucm.ead.engine.demos;

import es.eucm.ead.engine.demobuilder.ExecutableDemoBuilder;
import es.eucm.ead.engine.utils.SwingEDTUtils;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * Simple Swing-based demo launcher. To register new demos, just add a line to
 * {@link #registerDemos()}
 * 
 * Created by Javier Torrente on 7/07/14.
 */
public class DemoLauncher extends JFrame {

	public static final int WIDTH = 500;
	public static final int HEIGHT = 800;

	private HashMap<String, ExecutableDemoBuilder> availableDemos;

	private JLabel snapshotImage;

	private JTextArea description;

	private JComboBox demoSelector;

	private JButton run;

	public DemoLauncher() {
		super("Demo launcher");
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		availableDemos = new HashMap<String, ExecutableDemoBuilder>();
		registerDemos();
		demoSelector = new JComboBox();
		demoSelector.setModel(new DefaultComboBoxModel(getDemoNames()));
		demoSelector.setSelectedIndex(0);
		demoSelector.setEnabled(true);
		demoSelector.setEditable(false);
		demoSelector.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateDemoSelected();
			}
		});
		snapshotImage = new JLabel();
		snapshotImage.setHorizontalAlignment(JLabel.CENTER);
		snapshotImage.setBorder(BorderFactory.createEtchedBorder());
		snapshotImage.setIcon(new ImageIcon());
		run = new JButton("Run");
		run.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ExecutableDemoBuilder demoBuilder = availableDemos
						.get(demoSelector.getSelectedItem());
				if (demoBuilder != null) {
					demoBuilder.run();
					SwingEDTUtils.invokeLater(new Runnable() {
						@Override
						public void run() {
							dispose();
						}
					});
				}
			}
		});
		description = new JTextArea();
		description.setEditable(false);
		description.setBackground(this.getBackground());
		description.setWrapStyleWord(true);
		description.setLineWrap(true);

		this.setLayout(new BorderLayout(10, 10));
		JPanel selectorPanel = new JPanel();
		selectorPanel.add(demoSelector);
		add(selectorPanel, BorderLayout.NORTH);

		JPanel centralPanel = new JPanel();
		centralPanel.setLayout(new GridLayout(2, 1, 5, 0));
		centralPanel.add(snapshotImage);
		centralPanel.add(description);
		add(centralPanel, BorderLayout.CENTER);

		JPanel runPanel = new JPanel();
		runPanel.add(run);
		add(runPanel, BorderLayout.SOUTH);

		updateDemoSelected();

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

	/**
	 * Modify this method to add new demos to the launcher
	 */
	private void registerDemos() {
		registerDemo(new CoolDemo());
		registerDemo(new PlanesDemo());
		registerDemo(new MeetingAFriendDemo());
		registerDemo(new SpineDemo());
		registerDemo(new ShadersDemo());
		registerDemo(new URLImageDemo());
		registerDemo(new RenderersDemo());
	}

	private void registerDemo(ExecutableDemoBuilder demoBuilder) {
		availableDemos.put(demoBuilder.getName(), demoBuilder);
	}

	private String[] getDemoNames() {
		String[] names = new String[availableDemos.size() + 1];
		names[0] = "- Not selected -";
		int i = 1;
		for (String name : availableDemos.keySet()) {
			names[i++] = name;
		}
		return names;
	}

	private Image loadSnapshot(ExecutableDemoBuilder demoBuilder) {
		try {
			InputStream inputStream = demoBuilder.getSnapshotInputStream();
			if (inputStream != null) {
				return ImageIO.read(inputStream);
			} else {
				return null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void updateDemoSelected() {
		ExecutableDemoBuilder demoBuilder = availableDemos.get(demoSelector
				.getSelectedItem());
		if (demoBuilder == null) {
			snapshotImage.setIcon(null);
			snapshotImage.setText("Not selected");
			description.setText("");
			run.setEnabled(false);
		} else {
			Image image = loadSnapshot(demoBuilder);
			if (image != null) {
				ImageIcon imageIcon = new ImageIcon(loadSnapshot(demoBuilder));
				snapshotImage.setIcon(imageIcon);
				snapshotImage.setText(null);
			}
			description.setText(demoBuilder.getDescription());
			run.setEnabled(true);
		}
	}

	public static void main(String[] args) {
		new DemoLauncher();
	}
}
