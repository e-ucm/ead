package es.eucm.ead.editor.view.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.AddScene;
import es.eucm.ead.editor.control.actions.EditScene;
import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.Project;
import es.eucm.ead.editor.model.events.ProjectEvent;
import es.eucm.ead.editor.model.events.SceneEvent;
import es.eucm.ead.editor.view.listeners.ActionOnClickListener;
import es.eucm.ead.engine.I18N;

public class ScenesList extends AbstractWidget {

	private Controller controller;

	public ScenesList(Controller c) {
		this.controller = c;
		c.addModelListener(ProjectEvent.class,
				new ModelListener<ProjectEvent>() {
					@Override
					public void modelChanged(ProjectEvent event) {
						build(event.getProject());
					}
				});
		c.addModelListener(SceneEvent.class, new ModelListener<SceneEvent>() {

			@Override
			public void modelChanged(SceneEvent event) {
				switch (event.getType()) {
				case ADDED:
					addEditButton(event.getName(), controller.getEditorAssets()
							.getI18N(), controller.getEditorAssets().getSkin());
					break;
				}
			}
		});
	}

	private void build(Project project) {
		Skin skin = controller.getEditorAssets().getSkin();
		I18N i18N = controller.getEditorAssets().getI18N();
		for (String scene : project.getScenes()) {
			addEditButton(scene, i18N, skin);
		}
		TextButton addButton = new TextButton(i18N.m("scene.add"), skin);
		addButton.addListener(new ActionOnClickListener(controller,
				AddScene.NAME));
		addActor(addButton);
	}

	private void addEditButton(String scene, I18N i18N, Skin skin) {
		TextButton textButton = new TextButton(i18N.m("scene.edit", scene),
				skin);
		textButton.addListener(new ActionOnClickListener(controller,
				EditScene.NAME, scene));
		addActor(textButton);
	}

	@Override
	public void layout() {
		float y = 0;
		float size = this.getWidth();
		for (Actor a : getChildren()) {
			a.setBounds(0, y, size, size);
			y += size;
		}
	}

	@Override
	public float getPrefWidth() {
		return Gdx.graphics.getWidth() / 7;
	}

	@Override
	public float getPrefHeight() {
		return Gdx.graphics.getHeight();
	}
}
