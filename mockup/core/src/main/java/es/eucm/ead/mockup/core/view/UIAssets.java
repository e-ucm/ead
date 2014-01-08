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

import es.eucm.ead.mockup.core.control.screens.AbstractScreen;
import es.eucm.ead.mockup.core.view.ui.components.NavigationPanel;
import es.eucm.ead.mockup.core.view.ui.components.OptionsPanel;

public class UIAssets {

	private static Group optionsGroup, navigationGroup;
	private static boolean created = false;
	public static final String OPTIONS_PANEL_NAME = "op";
	
	public static void create(){
		created = true;
		createOptionsGroup();
		createNavigationGroup();
	}
	
	private static void createOptionsGroup(){
		optionsGroup = new Group();
		optionsGroup.setVisible(false);
		final OptionsPanel p = new OptionsPanel(AbstractScreen.skin, "dialog");
		p.setName(OPTIONS_PANEL_NAME);
		final Button options = new ImageButton(AbstractScreen.skin, "toggle");
		options.setBounds(AbstractScreen.stagew - 100, AbstractScreen.stageh - 100, 90, 90);
		options.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (!p.isVisible()) {
					AbstractScreen.mockupController.show(p);
				} else {
					AbstractScreen.mockupController.hide(p);
				}
			}
		});		
		
		Image i = new Image(new Texture(Gdx.files
				.internal("mockup/temp/image.png")));
		i.setTouchable(Touchable.disabled);
		i.setBounds(AbstractScreen.halfstagew - 100, AbstractScreen.halfstageh - 100, 200, 200);

		optionsGroup.addActor(i);
		optionsGroup.addActor(p);
		optionsGroup.addActor(options);
	}
	
	private static void createNavigationGroup(){
		navigationGroup = new Group();
		navigationGroup.setVisible(false);
		final NavigationPanel p = new NavigationPanel(AbstractScreen.skin, "default");p.setModal(false);
		final Button options = new ImageButton(AbstractScreen.skin);
		options.setBounds(0, AbstractScreen.stageh - 100, 90, 90);
		options.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				event.cancel();
				if (!p.isVisible()) {
					AbstractScreen.mockupController.show(p);
				} else {
					AbstractScreen.mockupController.hide(p);
				}
			}
		});

		navigationGroup.addActor(p);
		navigationGroup.addActor(options);
	}
	
	public static void addActors(){
		AbstractScreen.stage.addActor(navigationGroup);
		AbstractScreen.stage.addActor(optionsGroup);
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
