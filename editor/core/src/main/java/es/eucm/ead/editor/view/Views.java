package es.eucm.ead.editor.view;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import es.eucm.ead.editor.view.builders.ViewBuilder;
import es.eucm.ead.editor.view.classic.MainBuilder;
import es.eucm.ead.engine.I18N;

import java.util.HashMap;
import java.util.Map;

public class Views {

	private Map<String, Actor> viewsCache;

	private Map<String, ViewBuilder> viewsBuilders;

	public Views() {
		viewsCache = new HashMap<String, Actor>();
		viewsBuilders = new HashMap<String, ViewBuilder>();

		viewsBuilders.put("main", new MainBuilder());
	}

	public Actor getView(String name, Skin skin, I18N i18n) {
		Actor view = viewsCache.get(name);
		if ( view == null ){
			ViewBuilder builder = viewsBuilders.get(name);
			if (builder != null){
				view = builder.build(skin, i18n);
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
