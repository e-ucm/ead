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
package es.eucm.ead.engine.tests;

public class GameLoopTest /* implements EmptyListener */{

	/*
	 * private GameLoop gameLoop;
	 * 
	 * private GameAssets gameAssets;
	 * 
	 * private GameView gameView;
	 * 
	 * private int executed;
	 * 
	 * @BeforeClass public static void setUpClass() {
	 * MockApplication.initStatics(); }
	 * 
	 * @Before public void setUp() { executed = 0; gameAssets = new
	 * MockGameAssets(new MockFiles()); //gameLoop = new GameLoop(gameAssets);
	 * gameLoop.runGame("schema", true); gameAssets = gameLoop.getGameAssets();
	 * //gameView = gameLoop.getGameView(); gameAssets.finishLoading(); }
	 * 
	 * @Test public void testLoadGame() { testSceneLoaded("initial", 1); }
	 * 
	 * @Test public void testLoadScene() { gameLoop.loadScene("other");
	 * testSceneLoaded("other", 2); }
	 * 
	 * @Test public void testReloadScene() { testSceneLoaded("initial", 1);
	 * SceneEngineObject sceneActor = gameView.getCurrentScene();
	 * sceneActor.setX(20); gameLoop.reloadCurrentScene();
	 * gameAssets.finishLoading(); sceneActor = gameView.getCurrentScene(); //
	 * if position is reset, the scene has been reloaded assertEquals((int)
	 * sceneActor.getX(), 0); }
	 * 
	 * private void testSceneLoaded(String name, int childrenNumber) {
	 * gameAssets.finishLoading(); assertEquals(gameLoop.getCurrentScene(),
	 * name); Scene currentScene = gameView.getCurrentScene().getSchema();
	 * assertNotNull(currentScene);
	 * assertEquals(currentScene.getChildren().size(), childrenNumber); }
	 * 
	 * @Test public void testLoadSubgame() {
	 * assertEquals(gameLoop.getVarsContext().getValue("noglobal"), "value1");
	 * assertEquals(gameLoop.getVarsContext().getValue("_g_lang"), "en");
	 * assertEquals(gameLoop.getVarsContext().getValue("_g_global"), "global");
	 * gameLoop.startSubgame("subgame", null); gameAssets.finishLoading();
	 * testSceneLoaded("initialsubgame", 0);
	 * assertEquals(gameLoop.getVarsContext().getValue("_g_lang"), "es");
	 * assertEquals(gameLoop.getVarsContext().getValue("noglobal"), "value2");
	 * assertEquals(gameLoop.getVarsContext().getValue("_g_global"), "global");
	 * gameLoop.getVarsContext().setValue("_g_global", "other");
	 * gameLoop.getVarsContext().setValue("noglobal", "value3");
	 * gameLoop.endSubgame(); gameAssets.finishLoading();
	 * assertEquals(gameLoop.getVarsContext().getValue("_g_lang"), "es");
	 * assertEquals(gameLoop.getVarsContext().getValue("noglobal"), "value1");
	 * assertEquals(gameLoop.getVarsContext().getValue("_g_global"), "other");
	 * testSceneLoaded("initial", 1); }
	 * 
	 * @Test public void testPostEffects() { List<Effect> postEffects = new
	 * ArrayList<Effect>(); postEffects.add(new Empty(this));
	 * gameLoop.startSubgame("subgame", postEffects); assertEquals(executed, 0);
	 * gameLoop.endSubgame(); assertEquals(executed, 1); }
	 * 
	 * @Test public void testLoadSeveralScenes() {
	 * gameLoop.loadScene("initial"); gameLoop.loadScene("other");
	 * gameLoop.loadScene("another"); gameLoop.loadScene("initial"); // Just to
	 * add some random gameAssets.update(); gameLoop.loadScene("other");
	 * gameLoop.loadScene("another"); gameLoop.loadScene("initial");
	 * gameAssets.update(); gameLoop.loadScene("other");
	 * gameLoop.loadScene("another"); gameAssets.finishLoading();
	 * assertEquals(gameLoop.getCurrentScene(), "another"); }
	 * 
	 * @Test public void testUnexistingScene() { try {
	 * gameLoop.loadScene("ñor"); gameAssets.finishLoading(); } catch
	 * (GdxRuntimeException e) { e.printStackTrace(); return; } fail(); }
	 * 
	 * @Override public void executed() { executed++; }
	 */
}
