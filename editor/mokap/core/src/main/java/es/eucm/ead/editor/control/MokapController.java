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
package es.eucm.ead.editor.control;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import es.eucm.ead.editor.assets.ApplicationAssets;
import es.eucm.ead.editor.control.engine.MobileEngineInitializer;
import es.eucm.ead.editor.platform.Platform;
import es.eucm.ead.editor.view.widgets.SpinningLogo;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;
import es.eucm.ead.engine.EngineInitializer;

public class MokapController extends Controller {

	private Group loadingImage;

	public static final String[] SCALES = new String[] { "1.0", "1.5", "2.0",
			"2.5", "3.0", "4.0" };

	public MokapController(Platform platform, Files files,
			final Group rootComponent, final Group modalContainer) {
		super(platform, files, rootComponent, modalContainer);
		WidgetBuilder.setController(this);

		// This allows us to catch events related with
		// the back key in Android.
		Gdx.input.setCatchBackKey(true);
		modalContainer.getStage().addCaptureListener(new InputListener() {
			@Override
			public boolean keyUp(InputEvent event, int keycode) {
				if (keycode == Keys.BACK
						|| (Gdx.app.getType() == Application.ApplicationType.Desktop && keycode == Keys.ESCAPE)) {
					((MokapViews) MokapController.this.views).onBackPressed();
					return true;
				} else if (keycode == Keys.ENTER
						&& !(event.getTarget() instanceof TextArea)) {
					((MokapViews) MokapController.this.views)
							.hideOnscreenKeyboard();
					return true;
				} else if (keycode == Keys.NUM_2) {
					modalContainer.getStage().touchUp(
							Gdx.graphics.getWidth() / 2,
							Gdx.graphics.getHeight() / 2, 1, Buttons.LEFT);
				}
				return false;
			}

			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				if (keycode == Keys.NUM_2) {
					modalContainer.getStage().touchDown(
							Gdx.graphics.getWidth() / 2,
							Gdx.graphics.getHeight() / 2, 1, Buttons.LEFT);
				}
				return super.keyDown(event, keycode);
			}
		});

		loadingImage = new SpinningLogo(getApplicationAssets().getSkin());
		loadingImage.setPosition(WidgetBuilder.dpToPixels(24),
				WidgetBuilder.dpToPixels(24));
	}

	public void pause() {
		getPreferences().flush();
	}

	@Override
	protected void loadPreferences() {
		getApplicationAssets().getI18N().setI18nPath("i18n-mokap");
		super.loadPreferences();
	}

	@Override
	protected ApplicationAssets createApplicationAssets(Files files) {
		// 48px are 0.8cm in scale 1.0
		float scale = (Gdx.graphics.getPpcX() * 0.8f) / 48.0f;
		String scaleString;
		if (scale < Float.parseFloat(SCALES[0])) {
			scaleString = SCALES[0];
		} else {
			scaleString = SCALES[SCALES.length - 1];
			for (int i = 0; i < SCALES.length - 1; i++) {
				float lowerScale = Float.parseFloat(SCALES[i]);
				float greaterScale = Float.parseFloat(SCALES[i + 1]);
				if (scale >= lowerScale && scale <= greaterScale) {
					scaleString = scale - lowerScale < greaterScale - scale ? SCALES[i]
							: SCALES[i + 1];
					break;
				}
			}
		}
		return new ApplicationAssets(files, "skins/mokap/skin.json",
				scaleString);
	}

	@Override
	protected EngineInitializer buildEngineInitializer() {
		return new MobileEngineInitializer(this);
	}

	@Override
	protected Views createViews(Group rootView, Group modalsView) {
		return new MokapViews(this, rootView, modalsView);
	}

	public static interface BackListener {
		/**
		 * Called when the Back key was pressed in Android.
		 */
		boolean onBackPressed();
	}

	@Override
	public boolean act(float delta) {
		if (super.act(delta)) {
			loadingImage.remove();
			return true;
		}

		if (!getViews().modalsContainer.getChildren().contains(loadingImage,
				true)) {
			getViews().addToModalsContainer(loadingImage);
		} else {
			loadingImage.toFront();
		}

		return false;
	}

}
