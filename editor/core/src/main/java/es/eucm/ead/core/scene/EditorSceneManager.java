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
package es.eucm.ead.core.scene;

import org.stringtemplate.v4.ST;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;

import es.eucm.ead.core.EAdEngine;
import es.eucm.ead.core.EditorEngine;
import es.eucm.ead.core.io.EditorIO;
import es.eucm.ead.core.io.Platform.StringListener;
import es.eucm.ead.schema.actors.SceneElement;
import es.eucm.ead.schema.behaviors.Behavior;
import es.eucm.ead.schema.game.Game;

public class EditorSceneManager extends SceneManager {

	private FileHandle currentPath;

	private EditorIO io = (EditorIO) EAdEngine.jsonIO;

	public EditorSceneManager(AssetManager assetManager) {
		super(assetManager);
	}

	@Override
	public void loadGame() {
		if (currentPath != null) {
			super.loadGame();
		}
		loadTemplates();
	}

	public void loadTemplate(String template) {
		EditorEngine.assetManager.load(template, String.class);
		EditorEngine.assetManager.finishLoading();
	}

	private void loadTemplates() {
		loadTemplate("@templates/imageactor.json");
		loadTemplate("@templates/gosceneb.json");
	}

	public void readGame() {
		EditorEngine.platform.askForFile(new StringListener() {
			@Override
			public void string(String result) {
				if (result != null && result.endsWith("game.json")) {
					currentPath = Gdx.files.absolute(result).parent();
					EAdEngine.engine.setLoadingPath(currentPath.path());
					Gdx.app.postRunnable(new Runnable() {
						@Override
						public void run() {
							loadGame();
						}
					});
				}
			}
		});
	}

	public void newGame() {
		final Game game = new Game();
		EditorEngine.platform.askForString("Name of the game?",
				new StringListener() {
					@Override
					public void string(String result) {
						currentPath = Gdx.files.external("eadgames/" + result);
						EditorEngine.platform.askForString("Width?",
								new StringListener() {

									@Override
									public void string(String result) {
										int width = 800;
										try {
											width = Integer.parseInt(result);
										} catch (NumberFormatException e) {

										}
										game.setWidth(width);
										EditorEngine.platform.askForString(
												"Height?",
												new StringListener() {

													@Override
													public void string(
															String result) {
														int height = 600;
														try {
															height = Integer
																	.parseInt(result);
														} catch (NumberFormatException e) {

														}
														game.setHeight(height);
														EditorEngine.platform
																.askForString(
																		"Initial scene?",
																		new StringListener() {

																			@Override
																			public void string(
																					String result) {
																				game
																						.setInitialScene(result);
																				EAdEngine.jsonIO
																						.toJson(
																								game,
																								currentPath
																										.child("game.json"));
																				currentPath
																						.child(
																								"scenes")
																						.mkdirs();
																				Gdx.app
																						.postRunnable(new Runnable() {

																							@Override
																							public void run() {
																								EAdEngine.engine
																										.setLoadingPath(currentPath
																												.file()
																												.getAbsolutePath());
																								loadGame();
																							}
																						});
																			}
																		});
													}
												});
									}
								});
					}
				});
	}

	public void save(boolean optimize) {
		String name = this.getCurrentSceneName();
		if (!name.endsWith(".json")) {
			name += ".json";
		}
		io.save(EditorEngine.sceneManager.getScene(), (optimize ? "bin/" : "")
				+ name, optimize);
	}

	public void addSceneElement() {
		EditorEngine.platform.askForFile(new StringListener() {

			@Override
			public void string(String result) {
				if (result != null) {
					SceneElement sceneElement = buildFromTemplate(
							SceneElement.class, "imageactor.json", "uri",
							result);
					EditorEngine.sceneManager.loadSceneElement(sceneElement);
				}
			}
		});
	}

	public void newScene(final SceneElement element) {
		EditorEngine.platform.askForString("Scene name?", new StringListener() {
			@Override
			public void string(String result) {
				if (result != null) {
					final String scene = result;
					Gdx.app.postRunnable(new Runnable() {
						@Override
						public void run() {
							if (element != null) {
								Behavior goscene = buildFromTemplate(
										Behavior.class, "gosceneb.json",
										"scene", scene, "event", "touchDown");
								element.getBehaviors().add(goscene);
							}
							save(false);
							loadScene(scene);
						}
					});
				}
			}
		});
	}

	public <T> T buildFromTemplate(Class<T> clazz, String templateName,
			String... params) {
		String template = EditorEngine.assetManager.get("@templates/"
				+ templateName);
		ST st = new ST(template);
		for (int i = 0; i < params.length - 1; i++) {
			st.add(params[i], params[i + 1]);
		}
		return io.fromJson(clazz, st.render());
	}
}
