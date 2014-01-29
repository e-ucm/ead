package es.eucm.ead.editor;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.Stage;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.ShowView;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.view.builders.classic.MainBuilder;
import es.eucm.ead.engine.Assets;
import es.eucm.ead.engine.Factory;
import es.eucm.ead.editor.platform.Platform;

public class Editor implements ApplicationListener {

	protected Platform platform;
	private Assets assets;
	private Model model;
	private Factory factory;
	private Stage stage;
	protected Controller controller;

	public Editor(Platform platform) {
		this.platform = platform;
	}

	@Override
	public void create() {
		assets = new Assets(Gdx.files);
		factory = new Factory(assets);
		model = new Model(assets, factory);
		stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),
				false);
		controller = new Controller(platform, assets, model, stage.getRoot());
		controller.action(ShowView.NAME, MainBuilder.NAME);
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void resize(int width, int height) {
		stage.setViewport(width, height);
	}

	@Override
	public void render() {
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		stage.act();
		stage.draw();
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
	}
}
