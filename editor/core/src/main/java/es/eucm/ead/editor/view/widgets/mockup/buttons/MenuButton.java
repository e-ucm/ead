package es.eucm.ead.editor.view.widgets.mockup.buttons;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.utils.Scaling;

/**
 * A button displayed in the MainMenu and PanelMenu Screens.
 */
public class MenuButton extends Button {

	// XXX Should this constants change when resizing?
	private final float PAD_TOP = 17f, 
			PAD_LEFT = 17f, 
			PAD_BOTTOM = 10f,
			PAD_RIGHT = 17f;
	
	public MenuButton(String name, Skin skin, String iconRegion) {
		super(skin);
		initialize(name, skin, iconRegion);
	}
	
	private void initialize(String name, Skin skin, String iconRegion) {
		Image sceneIcon = new Image(skin.getRegion(iconRegion));
		sceneIcon.setScaling(Scaling.fit);
		
		Label scene = new Label(name, skin);
		scene.setAlignment(Align.center);

		pad(PAD_TOP, PAD_LEFT, PAD_BOTTOM, PAD_RIGHT);
		add(sceneIcon).expand();
		row();
		add(scene);
		setSize(getPrefWidth(), getPrefHeight());
	}
	
	@Override
	public float getPrefWidth() {
		// We make sure it's a square
		return Math.max(super.getPrefHeight(), super.getPrefHeight());
	}
	
	@Override
	public float getPrefHeight() {
		// We make sure it's a square
		return Math.max(super.getPrefHeight(), super.getPrefHeight());
	}
}
