package es.eucm.ead.editor.control;

import com.badlogic.gdx.scenes.scene2d.Actor;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.builders.ViewBuilder;
import es.eucm.ead.editor.view.builders.classic.MainBuilder;

import java.util.HashMap;
import java.util.Map;

public class Views {

	private Map<String, Actor> viewsCache;

	private Map<String, ViewBuilder> viewsBuilders;

	public Views() {
		viewsCache = new HashMap<String, Actor>();
		viewsBuilders = new HashMap<String, ViewBuilder>();
		addViews();
	}

	private void addViews() {
		addView(new MainBuilder());
	}

	private void addView(ViewBuilder viewBuilder) {
		viewsBuilders.put(viewBuilder.getName(), viewBuilder);
	}

	public Actor getView(String name, Controller controller) {
		Actor view = viewsCache.get(name);
		if (view == null) {
			ViewBuilder builder = viewsBuilders.get(name);
			if (builder != null) {
				view = builder.build(controller);
			}
		}
		return view;
	}

	/**
	 * Clears the views cache. Called whenever all the views must be regenerated
	 * (e.g., when the interface language changed)
	 */
	public void clearCache() {
		viewsCache.clear();
	}
}
