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
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;

import es.eucm.ead.editor.assets.ApplicationAssets;
import es.eucm.ead.editor.control.engine.MobileEngineInitializer;
import es.eucm.ead.editor.platform.Platform;
import es.eucm.ead.engine.EngineInitializer;

public class MokapController extends Controller {

	public static final String EXPORT_EXTENSION = ".zip";

	public static enum Dpi {
		LDPI(150), MDPI(190), HDPI(270), XHDPI(Float.MAX_VALUE), XXHDPI(0), XXXHDPI(
				0);

		private String dpi;
		/**
		 * The max dpi supported by this skin quality.
		 */
		private float maxDpi;

		private Dpi(float maxDpi) {
			this.dpi = name().toLowerCase();
			this.maxDpi = maxDpi;
		}

		public float getMaxDpi() {
			return maxDpi;
		}

		private static String getDpi() {
			float ppcX = Gdx.graphics.getPpcX();
			Gdx.app.error("PX", ppcX + "ppc");

			Dpi dpi = XXXHDPI;
			if (ppcX <= 36.0f) {
				dpi = LDPI;
			} else if (ppcX <= 48.0f) {
				dpi = MDPI;
			} else if (ppcX <= 60.0f) {
				dpi = HDPI;
			} else if (ppcX <= 72.0f) {
				dpi = XHDPI;
			} else if (ppcX <= 84.0f) {
				dpi = XXHDPI;
			}
			return dpi.dpi;
		}
	}

	private Group rootComponent;
	private RepositoryManager repositoryManager;

	public MokapController(Platform platform, Files files,
			final Group rootComponent, final Group modalContainer) {
		super(platform, files, rootComponent, modalContainer);
		this.rootComponent = rootComponent;
		repositoryManager = new RepositoryManager();

		// This allows us to catch events related with
		// the back key in Android.
		Gdx.input.setCatchBackKey(true);
		modalContainer.getStage().addCaptureListener(new InputListener() {
			@Override
			public boolean keyUp(InputEvent event, int keycode) {
				if (keycode == Keys.BACK
						|| (Gdx.app.getType() == Application.ApplicationType.Desktop && (keycode == Keys.ALT_LEFT || keycode == Keys.ESCAPE))) {
					((MokapViews) MokapController.this.views).onBackPressed();
					return true;
				} else if (keycode == Keys.ENTER
						&& !(event.getTarget() instanceof TextArea)) {
					((MokapViews) MokapController.this.views)
							.hideOnscreenKeyboard();
					return true;
				}
				return false;
			}

		});
	}

	public void pause() {
		getPreferences().flush();
	}

	public RepositoryManager getRepositoryManager() {
		return repositoryManager;
	}

	@Override
	protected void loadPreferences() {
		getApplicationAssets().getI18N().setI18nPath("i18n-mokap");
		super.loadPreferences();
	}

	@Override
	protected ApplicationAssets createApplicationAssets(Files files) {
		String dpi = Dpi.getDpi();
		String skinPath = "skins/mokap-" + dpi + "/";
		return new ApplicationAssets(files, skinPath + "skin");
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
		void onBackPressed();
	}
}
