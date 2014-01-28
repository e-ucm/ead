package es.eucm.ead.editor.control;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.view.Views;
import es.eucm.ead.engine.Assets;
import es.eucm.editor.io.Platform;

public class Controller {

	private Group rootView;

	private Model model;

	private Platform platform;

	private Assets assets;

	private Views views;

	private Actions actions;

	public Controller(Platform platform, Assets assets, Model model,
			Group rootView) {
		this.platform = platform;
		this.assets = assets;
		this.rootView = rootView;
		this.model = model;
		this.views = new Views();
		this.actions = new Actions(this);
	}

	public Model getModel() {
		return model;
	}

	public Assets getAssets() {
		return assets;
	}

	public Platform getPlatform() {
		return platform;
	}

	public void view(String viewName) {
		Actor actor = views.getView(viewName, this);
		rootView.clear();
		rootView.addActor(actor);
	}

	public void action(String actionName, Object... args) {
		actions.perform(actionName, args);
	}

	public String getLoadingPath() {
		return assets.getLoadingPath();
	}

	public void setGamePath(String gamePath) {
		assets.setGamePath(gamePath, false);
		model.load(gamePath);
	}
}
