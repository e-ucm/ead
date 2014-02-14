package es.eucm.ead.editor.view.widgets.mockup;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.widgets.mockup.panels.LateralOptionsPanel;
import es.eucm.ead.editor.view.widgets.mockup.panels.LateralPanel;

public class Options extends Table{

	private Button optButton;
	private LateralPanel optPanel;
	
	private Rectangle rtmp;
	
	private boolean opened;
	
	String IC_OPTIONS = "ic_settings";
	
	public Options(Controller controller, Skin skin) {
		super(skin);
		
		optButton = new ImageButton(skin, IC_OPTIONS);
		optButton.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				opened = !optPanel.isVisible();
				if(optPanel.isVisible()){
					optPanel.hide();
				} else {
					optPanel.show();
				}
				return false;
			}
		});
				
		optPanel = new LateralOptionsPanel(controller, skin);
		opened=optPanel.isVisible();
		rtmp = new Rectangle();
		rtmp.set(optPanel.getX(), optPanel.getY(), optPanel.getWidth(), optPanel.getHeight());
		
		this.add(optButton).top().right().expand();
		this.row();
		this.add(optPanel).center().right().expand();
	}
	
	public boolean isOpened(){
		return opened;
	}
	
	public Button getButton(){
		return optButton;
	}
	
	public LateralPanel getPanel(){
		return optPanel;
	}

}
