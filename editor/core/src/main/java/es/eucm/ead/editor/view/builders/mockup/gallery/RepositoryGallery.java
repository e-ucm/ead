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
package es.eucm.ead.editor.view.builders.mockup.gallery;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.editor.ChangeView;
import es.eucm.ead.editor.view.builders.mockup.menu.InitialScreen;
import es.eucm.ead.editor.view.listeners.ActionOnClickListener;
import es.eucm.ead.editor.view.widgets.mockup.buttons.ElementButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.ToolbarButton;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.entities.ModelEntity;

/**
 * The gallery that will display our projects. Has a top tool bar and a gallery
 * grid.
 */
public class RepositoryGallery extends BaseGallery<ElementButton> {

	public static final String NAME = "mockup_repository_gallery";

	private static final String IC_GO_BACK = "ic_goback";
	private static final String REPOSITORY_FOLDER = ".onlineRepository";

	private final Array<ElementButton> onlineElements = new Array<ElementButton>(
			false, 10);

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	protected Button topLeftButton(Vector2 viewport, Skin skin,
			Controller controller) {
		final Button backButton = new ToolbarButton(viewport, skin, IC_GO_BACK);
		backButton.addListener(new ActionOnClickListener(controller,
				ChangeView.class, InitialScreen.NAME));
		return backButton;
	}

	private void setButtonDisabled(boolean disabled, Button button) {
		Touchable t = disabled ? Touchable.disabled : Touchable.enabled;

		button.setDisabled(disabled);
		button.setTouchable(t);
	}

	/* HTTP TEST */

	HttpRequest httpRequest;

	@Override
	protected Button getFirstPositionActor(Vector2 viewport, I18N i18n,
			Skin skin, Controller controller) {
		return null;
	}

	@Override
	protected String getTitle(I18N i18n) {
		return i18n.m("general.mockup.repository");
	}

	@Override
	protected void addSortingsAndComparators(Array<String> shortings,
			ObjectMap<String, Comparator<ElementButton>> comparators, I18N i18n) {
		// Do nothing since we won't have additional sorting methods in
		// RepositoryGallery

	}

	@Override
	protected boolean updateGalleryElements(Controller controller,
			Array<ElementButton> elements, Vector2 viewport, I18N i18n,
			Skin skin) {
		elements.clear();
		for (ElementButton elem : onlineElements) {
			elements.add(elem);
		}
		return true;
	}

	@Override
	protected void entityClicked(InputEvent event, ElementButton target,
			Controller controller, I18N i18n) {

	}

	@Override
	protected void entityDeleted(ElementButton entity, Controller controller) {

	}

	@Override
	public void initialize(final Controller controller) {
		String url;
		String httpMethod = Net.HttpMethods.GET;
		String requestContent = null;
		// url = "http://www.apache.org/licenses/LICENSE-2.0.txt";
		url = "http://repo-justusevim.rhcloud.com/test";
		httpRequest = new HttpRequest(httpMethod);
		httpRequest.setUrl(url);
		httpRequest.setContent(requestContent);
		Gdx.net.sendHttpRequest(httpRequest, new HttpResponseListener() {

			@Override
			public void handleHttpResponse(final HttpResponse httpResponse) {
				final int statusCode = httpResponse.getStatus().getStatusCode();
				// We are not in main thread right now so we
				// need to post to main thread for ui updates

				if (statusCode != 200) {
					Gdx.app.log("NetAPITest",
							"An error ocurred since statusCode is not OK, "
									+ httpResponse);
					return;
				}

				final String res = httpResponse.getResultAsString();
				Gdx.app.log("NetAPITest", "Success ~> " + httpResponse + ", "
						+ res);

				Gdx.app.postRunnable(new Runnable() {
					public void run() {
						ArrayList<ModelEntity> elems = controller
								.getEditorGameAssets().fromJson(
										ArrayList.class, res);
						onlineElements.clear();
						for (ModelEntity elem : elems) {
							System.out.println(elem);
							onlineElements.add(new ElementButton(controller
									.getPlatform().getSize(), controller
									.getApplicationAssets().getI18N(), elem,
									null, controller.getApplicationAssets()
											.getSkin(), controller));
						}
						RepositoryGallery.super.initialize(controller);
					}
				});
			}

			@Override
			public void failed(Throwable t) {
				Gdx.app.log("NetAPITest",
						"Failed to perform the HTTP Request: ", t);

			}

			@Override
			public void cancelled() {
				Gdx.app.log("NetAPITest", "HTTP request cancelled");

			}
		});
	}

	@Override
	public void release(Controller controller) {
		super.release(controller);
	}

	private void download(HttpResponse httpResponse) {
		InputStream input = null;
		OutputStream output = null;
		try {
			input = httpResponse.getResultAsStream();

			output = Gdx.files.external("test").write(false);

			byte data[] = new byte[4096];
			// long total = 0;
			int count;
			while ((count = input.read(data)) != -1) {
				// allow canceling with back button
				/*
				 * if (isCancelled()) { input.close(); return null; }
				 */
				// total += count;
				// publishing the progress....
				/*
				 * if (fileLength > 0) // only if total length is known
				 * publishProgress((int) (total * 100 / fileLength));
				 */
				output.write(data, 0, count);
			}
		} catch (Exception e) {
			Gdx.app.error("NetAPITest", "Exception", e);
		} finally {
			try {
				if (output != null)
					output.close();
				if (input != null)
					input.close();
			} catch (IOException ignored) {
				Gdx.app.error("NetAPITest", "This exception should be ignored",
						ignored);
			}
		}
	}
}