package es.eucm.ead.editor.view.widgets.mockup.editionComponents;

import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.ead.editor.view.builders.mockup.edition.EditionWindow;
import es.eucm.ead.editor.view.widgets.mockup.panels.HiddenPanel;

public class EditionComponent extends HiddenPanel{

	protected Button button;
	private EditionWindow parent;


	public EditionComponent(Skin skin,EditionWindow parent) {
		super(skin );
		this.parent = parent;
		this.setVisible(false);
		super.stageBackground = null;
		
	}
	
	public void show(){
		if(parent.getCurrentVisible() != null){
			parent.getCurrentVisible().hide();	
		}
		super.show();
		parent.changeCurrentVisible(this);	

	}

	public void hide(){
		super.hide();
		if(parent.getCurrentVisible() == this){
			parent.changeCurrentVisible(null);
		}
	}

	public Button getButton(){
		return this.button;
	}

	public EventListener buttonListener(){
		return new ClickListener() {
			final
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (!EditionComponent.this.isVisible()) {
					EditionComponent.this.show();
				} else {
					EditionComponent.this.hide();
				}
			}
		};
	}
}
