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
package es.eucm.ead.mockup.core.control.screens;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.SnapshotArray;

import es.eucm.ead.mockup.core.control.MockupController;
import es.eucm.ead.mockup.core.control.listeners.EventListener;
import es.eucm.ead.mockup.core.control.listeners.FocusListener;
import es.eucm.ead.mockup.core.view.UIAssets;
import es.eucm.ead.mockup.core.view.ui.Panel;

/**
 * Has auxiliary attributes.
 * It's responsible for drawing the screen.
 * It's responsible for updating the screen.
 * 
 */
public abstract class AbstractScreen implements EventListener, FocusListener {
	/**
	 * Fade in/out default duration.
	 */
	public static float fadeDuration = .25f;
	/**
	 * Used to draw and update the UI. 
	 * Has constant width and height defined in Constants.
	 */
	public static Stage stage;

	/**
	 * Used for the UI elements.
	 */
	public static Skin skin;

	/**
	 * Used to manage UI resources.
	 */
	public static AssetManager am;

	/**
	 * Static reference to the main controller.
	 */
	public static MockupController mockupController;

	/**
	 * Static reference to the default bitmap font.
	 */
	public static BitmapFont font;

	/**
	 * Stage's width.
	 * The stage is used for the UI.
	 */
	public static float stagew;
	public static float halfstagew;

	/**
	 * Stage's height.
	 * The stage is used for the UI.
	 */
	public static float stageh;
	public static float halfstageh;

	/**
	 * Displays current Screens UI.
	 */
	protected Group root;

	/**
	 * Used to go to navigate to the previous screen when the
	 * Kays.Back button is pressed.
	 * (When {onBackKeyPressed()} is triggered) 
	 */
	private Screens previousScreen;

	@Override
	public void create() {

	}

	/**
	 * Updates the screen.
	 * @param delta elapsed time since the last time.
	 */
	public void act(float delta) {

	}

	public void pause() {

	}

	public void resume() {

	}

	/**
	 * Renderer's loop.
	 */
	public void draw() {

	}

	@Override
	public void show() {
		stage.addAction(Actions.sequence(Actions.fadeIn(fadeDuration,
				Interpolation.fade)));
	}

	@Override
	public void hide() {

	}

	protected void exitAnimation(final Screens next) {
		stage.addAction(Actions.sequence(Actions.fadeOut(fadeDuration,
				Interpolation.fade), Actions.run(new Runnable() {
			@Override
			public void run() {
				
				/*FIXME hardcoded, find a better solution!*/
				SnapshotArray<Actor> childrens = root.getChildren();
				for(Actor children : childrens){
					if(children.isVisible() && children instanceof Panel){
						children.setVisible(false);
					}
				}
				UIAssets.getOptionsGroup().findActor(UIAssets.OPTIONS_PANEL_NAME).setVisible(false);
				UIAssets.getNavigationGroup().findActor(UIAssets.NAVIGATION_PANEL_NAME).setVisible(false);
				/*End of FIXME*/
	
				mockupController.changeTo(next);
			}
		})));
	}

	/**
	 * previousScreen must be configured or onBackKeyPressed() will throw an IllegalStateException.
	 * You could also override {onBackKeyPressed()} method instead.
	 * 
	 * @param previousScreen
	 */
	protected void setPreviousScreen(Screens previousScreen) {
		this.previousScreen = previousScreen;
	}

	/**
	 * Executed when BACK key was pressed.
	 * If previousScreen is null an {@link IllegalStateException IllegalStateException} will be thrown.
	 */
	public void onBackKeyPressed() {
		if (previousScreen == null) {
			throw new IllegalStateException(
					"previousScreen is null in "
							+ this.getClass().getSimpleName()
							+ " please configure previousScreen via {setPreviousScreen(Screens previousScreen)} method"
							+ " or @Override {onBackKeyPressed()}");
		}
		exitAnimation(previousScreen);
	}
}
