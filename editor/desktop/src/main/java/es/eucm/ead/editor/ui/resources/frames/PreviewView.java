package es.eucm.ead.editor.ui.resources.frames;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;

import es.eucm.ead.editor.ui.resources.frames.AnimationEditor.FrameEditionListener;
import es.eucm.ead.engine.components.renderers.frames.FramesComponent;
import es.eucm.ead.engine.components.renderers.frames.FramesComponent.Frame;

/**
 * A widget that displays the {@link es.eucm.ead.schema.renderers.Frame frames}
 * with the help of {@link FramesComponent}. Used by the {@link AnimationEditor}
 * . Also listens to events such as
 * {@link FrameEditionListener#frameTimeChanged(int, float)} and
 * {@link FrameEditionListener#frameSelected(int)} in order to correctly
 * function.
 */
public class PreviewView extends WidgetGroup implements FrameEditionListener {

	private boolean playing;
	private FramesComponent previewFrames;

	@Override
	public void act(float delta) {
		if (playing && previewFrames != null) {
			previewFrames.act(delta);
		}
	}

	@Override
	public void drawChildren(Batch batch, float parentAlpha) {
		if (previewFrames != null) {
			Color color = getColor();
			batch.setColor(color.r, color.g, color.b, color.a);
			previewFrames.draw(batch);
		}
	}

	@Override
	public void layout() {
		if (previewFrames != null) {
			float maxWidth = Float.NEGATIVE_INFINITY;
			float maxHeight = maxWidth;
			Array<Frame> frames = previewFrames.getFrames();
			for (Frame frame : frames) {
				maxWidth = Math.max(maxWidth, frame.getWidth());
				maxHeight = Math.max(maxHeight, frame.getHeight());
			}

			Vector2 scale = Scaling.fit.apply(maxWidth, maxHeight, getWidth(),
					getHeight());

			setScale(scale.x / maxWidth);

			setPosition((getWidth() - scale.x) * .5f,
					(getHeight() - scale.y) * .5f);
		}
	}

	public void setPreviewFrames(FramesComponent previewFrames) {
		this.previewFrames = previewFrames;
	}

	public FramesComponent getPreviewFrames() {
		return previewFrames;
	}

	public void togglePlaying() {
		playing = !playing;
	}

	@Override
	public void frameSelected(int index) {
		if (!playing && previewFrames != null) {
			previewFrames.setCurrentFrameIndex(index);
		}
	}

	@Override
	public void frameTimeChanged(int index, float newValue) {
		previewFrames.getFrames().get(index).setDuration(newValue);
	}
}
