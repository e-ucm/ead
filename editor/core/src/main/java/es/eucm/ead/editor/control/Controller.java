package es.eucm.ead.editor.control;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.engine.Assets;
import es.eucm.ead.editor.platform.Platform;

public class Controller {

	private Group rootView;

	private Model model;

	private Platform platform;

	private Assets assets;

	private Views views;

	private Actions actions;

	private Preferences preferences;

	public Controller(Platform platform, Assets assets, Model model,
			Group rootView) {
		this.platform = platform;
		this.assets = assets;
		this.rootView = rootView;
		this.model = model;
		this.views = new Views();
		this.actions = new Actions(this);
		this.preferences = new Preferences(assets);
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

	public Preferences getPreferences() {
		return preferences;
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
