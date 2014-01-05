package es.eucm.ead.mockup.core.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.ead.mockup.core.model.Screen;
import es.eucm.ead.mockup.core.view.ui.components.NavigationPanel;
import es.eucm.ead.mockup.core.view.ui.components.OptionsPanel;

public class UIAssets {

	private static Group optionsGroup, navigationGroup;
	private static boolean created = false;
	
	public static void create(){
		created = true;
		createOptionsGroup();
		createNavigationGroup();
	}
	
	private static void createOptionsGroup(){
		optionsGroup = new Group();
		optionsGroup.setVisible(false);
		final OptionsPanel p = new OptionsPanel(Screen.skin, "dialog");
		final Button options = new ImageButton(Screen.skin, "toggle");
		options.setBounds(Screen.stagew - 100, Screen.stageh - 100, 90, 90);
		options.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (!p.isVisible()) {
					Screen.mockupController.show(p);
				} else {
					Screen.mockupController.hide(p);
				}
			}
		});		
		
		Image i = new Image(new Texture(Gdx.files
				.internal("mockup/temp/image.png")));
		i.setTouchable(Touchable.disabled);
		i.setBounds(Screen.halfstagew - 100, Screen.halfstageh - 100, 200, 200);

		optionsGroup.addActor(i);
		optionsGroup.addActor(p);
		optionsGroup.addActor(options);
	}
	
	private static void createNavigationGroup(){
		navigationGroup = new Group();
		navigationGroup.setVisible(false);
		final NavigationPanel p = new NavigationPanel(Screen.skin, "dialog");
		final Button options = new ImageButton(Screen.skin);
		options.setBounds(0, Screen.stageh - 100, 90, 90);
		options.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				event.cancel();
				if (!p.isVisible()) {
					Screen.mockupController.show(p);
				} else {
					Screen.mockupController.hide(p);
				}
			}
		});

		navigationGroup.addActor(p);
		navigationGroup.addActor(options);
	}
	
	public static void addActors(){
		Screen.stage.addActor(navigationGroup);
		Screen.stage.addActor(optionsGroup);
	}

	/**
	 * Used to display the configuration.
	 */
	public static Group getOptionsGroup() {
		return optionsGroup;
	}
	
	public static Group getNavigationGroup() {
		return navigationGroup;
	}
	
	public static boolean isCreated() {
		return created;
	}
}
