package es.eucm.ead.mockup.core.view.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class MenuButton extends Button{
	
	public MenuButton(String name, Skin skin, String string){
		super(skin);
		pad(17,17,10,17);
		Image sceneIcon = new Image(skin.getRegion(string));
		Label scene = new Label(name,skin);
		add(sceneIcon).expand();;
		row();
		add(scene);
		pack();
	}
}
