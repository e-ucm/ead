package es.eucm.ead.editor;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.Stage;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.engine.Assets;
import es.eucm.ead.engine.Factory;

public class Editor implements ApplicationListener {

	private Assets assets;
	private Model model;
	private Factory factory;
	private Stage stage;
	private Controller controller;

	@Override
	public void create() {
		assets = new Assets(Gdx.files);
		factory = new Factory(assets);
		model = new Model(assets, factory);
		stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),
				false);
		controller = new Controller(assets, model, stage.getRoot());
		controller.setView("main");
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
