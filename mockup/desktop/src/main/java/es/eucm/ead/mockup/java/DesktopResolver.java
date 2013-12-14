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
package es.eucm.ead.mockup.java;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import es.eucm.ead.core.io.Platform.StringListener;
import es.eucm.ead.mockup.core.facade.IActionResolver;
import es.eucm.ead.mockup.core.facade.IAnswerListener;

public class DesktopResolver implements IActionResolver {


	private JFileChooser fileChooser;
	@Override
	public void showDecisionBox(final int questionNumber,
			final String alertBoxTitle, final String alertBoxQuestion,
			final String answerA, final String answerB, final IAnswerListener ql) {
		if (questionNumber == IAnswerListener.QUESTION_EXIT) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					int result = JOptionPane.showConfirmDialog(null,
							alertBoxQuestion, alertBoxTitle,
							JOptionPane.YES_NO_OPTION);
					if (result == JOptionPane.YES_OPTION) {
						ql.onReceiveAnswer(questionNumber,
								IAnswerListener.QUESTION_EXIT_ANSWER_YES);
					} else if (result == JOptionPane.NO_OPTION) {
						ql.onReceiveAnswer(questionNumber,
								IAnswerListener.QUESTION_EXIT_ANSWER_NO);
					}
				}
			});
		}
	}

	@Override
	public void askForFile(final StringListener stringListener) {
		if( fileChooser == null ){
			 fileChooser = new JFileChooser();
		}
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					String s = fileChooser.getSelectedFile().getAbsolutePath();
					s = s.replaceAll("\\\\", "/");
					stringListener.string(s);
				} else {
					stringListener.string(null);
				}
			}
		});
	}
}
