package es.eucm.ead.mockup.core;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;

import es.eucm.ead.mockup.core.control.MockupController;
import es.eucm.ead.mockup.core.facade.IActionResolver;


public class Mockup implements ApplicationListener{

	private MockupController c;
	private IActionResolver resolver;
	
	public Mockup(IActionResolver resolver){
		this.resolver = resolver;
	}
	
	@Override
	public void create() {
		c = new MockupController(resolver);
	}

	@Override
	public void resize(int width, int height) {
		c.resize(width, height);
	}

	@Override
	public void render() {
		c.act(Gdx.graphics.getDeltaTime());
		c.draw();		
	}


	@Override
	public void pause() {
		c.pause();
	}

	@Override
	public void resume() {
		c.resume();
	}

	@Override
	public void dispose() {
		c.dispose();
	}
}
