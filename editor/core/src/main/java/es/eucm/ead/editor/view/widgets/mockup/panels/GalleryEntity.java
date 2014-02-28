package es.eucm.ead.editor.view.widgets.mockup.panels;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;


/**
 * Represents a selectable entry for the GalleryGrid by implementing
 * SelectListener interface.
 */
public class GalleryEntity extends TextButton {
	private static final float animationDuration = .4f;
	private boolean selected, originUpdated = false;
	private static NinePatch selectedview;

	public GalleryEntity(String name, Skin skin) {
		super(name, skin);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		if (selected)
			selectedview.draw(batch, getX(), getY(), getWidth(), getHeight());
	}

	public void select() {
		changeAlpha(.9f);
		selected = true;
		if (!originUpdated) {
			originUpdated = true;
			setOrigin(getWidth() * .5f, getHeight() * .5f);
		}
		addAction(Actions.scaleTo(.9f, .9f, animationDuration,
				Interpolation.swingOut));
	}

	public void deselect() {
		selected = false;
		changeAlpha(1f);
		addAction(Actions.scaleTo(1f, 1f, animationDuration,
				Interpolation.swingOut));
	}

	private void changeAlpha(float to) {
		Color col = getColor();
		col.a = to;
	}

	public boolean isSelected() {
		return selected;
	}
}

