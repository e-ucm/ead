package es.eucm.ead.editor.control;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;

import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.view.Views;
import es.eucm.ead.engine.Assets;

public class Controller {

	private Group rootView;

	private Model model;

	private Assets assets;

	private Views views;

	public Controller(Assets assets, Model model, Group rootView) {
		this.assets = assets;
		this.rootView = rootView;
		this.model = model;
		this.views = new Views();
	}

	public void setView(String name) {
		Actor actor = views.getView(name, assets.getSkin(), assets.getI18N());
		rootView.clear();
		rootView.addActor(actor);
	}

}
