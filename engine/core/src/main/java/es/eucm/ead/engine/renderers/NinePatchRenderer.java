package es.eucm.ead.engine.renderers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

import es.eucm.ead.engine.Engine;
import es.eucm.ead.schema.components.Bounds;
import es.eucm.ead.schema.components.Dimension;
import es.eucm.ead.schema.renderers.NinePatch;

public class NinePatchRenderer extends AbstractRenderer<NinePatch> {

	private NinePatchDrawable drawable;

	private Dimension size;

	@Override
	public void initialize(NinePatch schemaObject) {
		size = schemaObject.getSize();
		Bounds bounds = schemaObject.getBounds();
		com.badlogic.gdx.graphics.g2d.NinePatch ninePatch = new com.badlogic.gdx.graphics.g2d.NinePatch(
				Engine.assets.get(schemaObject.getUri(), Texture.class), bounds
						.getLeft(), bounds.getRight(), bounds.getTop(), bounds
						.getBottom());
		drawable = new NinePatchDrawable(ninePatch);
	}

	@Override
	public void draw(Batch batch) {
		drawable.draw(batch, 0, 0, size.getWidth(), size.getHeight());
	}

	@Override
	public float getHeight() {
		return size.getHeight();
	}

	@Override
	public float getWidth() {
		return size.getWidth();
	}

}
