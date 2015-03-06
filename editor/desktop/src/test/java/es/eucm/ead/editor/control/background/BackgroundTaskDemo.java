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
package es.eucm.ead.editor.control.background;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.background.BackgroundExecutor.BackgroundTaskListener;
import es.eucm.ead.engine.gdx.AbstractWidget;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.editor.widgets.AbstractWidgetTest;

/**
 * Created by angel on 25/03/14.
 */
public class BackgroundTaskDemo extends AbstractWidgetTest {

	private static Label label;

	private Runnable launchBackgroundProcess = new Runnable() {
		@Override
		public void run() {
			controller.getBackgroundExecutor().submit(
					new BackgroundTask<String>() {
						@Override
						public String call() throws Exception {
							int loops = 10;
							for (int i = 0; i < loops; i++) {
								Thread.sleep(1000);
							}
							return "done";
						}
					}, new BackgroundTaskListener<String>() {

						@Override
						public void done(BackgroundExecutor backgroundExecutor,
								String result) {
							label.setText("Done.");
						}

						@Override
						public void error(Throwable e) {
							Gdx.app.error("BackgroundTaskDemo", "Error", e);
						}
					});
		}
	};

	@Override
	public AbstractWidget createWidget(Controller controller) {
		Skin skin = controller.getApplicationAssets().getSkin();
		LinearLayout bottomLayout = new LinearLayout(false);
		label = new Label("Stopped", skin);

		bottomLayout.add(label);

		SelectBox<String> selectBox = new SelectBox<String>(skin);
		selectBox.setItems("Just", "a", "drop", "down", "list", "to", "check",
				"that", "UI", "doesn't", "freeze");

		TextButton textButton = new TextButton("Run", skin);
		textButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.postRunnable(launchBackgroundProcess);
			}
		});

		bottomLayout.add(selectBox);
		bottomLayout.add(textButton);
		return bottomLayout;
	}

	public static void main(String args[]) {
		new LwjglApplication(new BackgroundTaskDemo(), "Test for TextArea",
				1000, 600);

	}
}
