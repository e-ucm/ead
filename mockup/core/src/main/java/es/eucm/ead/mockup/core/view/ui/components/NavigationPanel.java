package es.eucm.ead.mockup.core.view.ui.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import es.eucm.ead.mockup.core.control.screens.AbstractScreen;
import es.eucm.ead.mockup.core.view.ui.Panel;

public class NavigationPanel extends Panel {

	private float x, y;
	
	public NavigationPanel(Skin skin) {
		super(skin, "default");
	}

	public NavigationPanel(Skin skin, String styleName) {
		super(skin, styleName);
		float w = AbstractScreen.stagew * .3f;
		this.x = -w;
		this.y = AbstractScreen.stageh * .03f;
		setBounds(x, y, w, AbstractScreen.stageh * .85f);
		setVisible(false);
		setColor(Color.ORANGE);
		setModal(true);

		Label cbs1 = new Label("TODO", skin);

		Table t = new Table();
		ScrollPane sp = new ScrollPane(t, skin);
		sp.setupFadeScrollBars(0f, 0f);
		t.add(cbs1);
		t.row();
		add(sp);
	}

	@Override
	public void show() {
		super.show();
		addAction(Actions.moveTo(0,  y, fadeDuration));
	}
	
	@Override
	public void hide() {
		super.hide();
		addAction(Actions.moveTo(x,  y, fadeDuration));
	}
}
