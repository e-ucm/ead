package es.eucm.ead.editor.view.widgets.mockup;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import es.eucm.ead.editor.view.widgets.mockup.buttons.ProjectButton;

/**
 * Displays the recent projects on the initial screen. The maximum number of
 * recent projects displayed is 8.
 */
public class RecentProjects extends ScrollPane {

	private final int MAX_RECENT_PROJECTS = 8;
	private int addedProjects;
	private Table projs;

	public RecentProjects() {
		super(null);

		final float DEFAULT_PAD = 10f;
		this.projs = new Table();
		this.projs.pad(DEFAULT_PAD);
		this.projs.defaults().space(DEFAULT_PAD);
		this.projs.debug();

		this.addedProjects = 0;

		setWidget(this.projs);
	}

	@Override
	public float getPrefWidth() {
		return Gdx.graphics.getWidth() * .5f;
	}

	public void clearRecents() {
		projs.clear();
		this.addedProjects = 0;
	}

	public void addRecent(ProjectButton recent) {
		if (this.addedProjects < this.MAX_RECENT_PROJECTS) {
			this.projs.add(recent);
			++this.addedProjects;
		}
	}
}
