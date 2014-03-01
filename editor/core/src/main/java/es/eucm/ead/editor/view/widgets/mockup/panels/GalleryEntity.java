package es.eucm.ead.editor.view.widgets.mockup.panels;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.widgets.mockup.buttons.DescriptionCard;
import es.eucm.ead.editor.view.widgets.mockup.panels.GalleryGrid.SelectListener;
import es.eucm.ead.engine.I18N;


/**
 * Represents a selectable entry for the {@link GalleryGrid} by implementing
 * SelectListener interface.
 */
public class GalleryEntity extends DescriptionCard implements SelectListener{
	private static final float ANIMATION_DURATION = .4f;
	private boolean selected, originUpdated = false;
	private static NinePatch selectedview;

	public GalleryEntity(Vector2 viewport, I18N i18n, String title,
			String description, String imageName, Skin skin,
			Controller controller, String actionName, Object... args) {
		super(viewport, i18n, title, description, imageName, skin, controller,
				actionName, args);
		if(selectedview == null){
			selectedview = skin.getPatch("text_focused");
		}	
	}

	public GalleryEntity(Vector2 viewport, I18N i18n, String title,
			String description, String imageName, Skin skin) {
		super(viewport, i18n, title, description, imageName, skin);
		if(selectedview == null){
			selectedview = skin.getPatch("text_focused");
		}	
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		if (this.selected)
			selectedview.draw(batch, getX(), getY(), getWidth(), getHeight());
	}

	@Override
	public void select() {
		changeAlpha(.9f);
		this.selected = true;
		if (!this.originUpdated) {
			this.originUpdated = true;
			setOrigin(getWidth() * .5f, getHeight() * .5f);
		}
		setTransform(true);
		addAction(Actions.scaleTo(.9f, .9f, ANIMATION_DURATION,
				Interpolation.swingOut));
	}

	@Override
	public void deselect() {
		addAction(Actions.sequence(Actions.scaleTo(1f, 1f, ANIMATION_DURATION,
				Interpolation.swingOut), Actions.run(onAnimationFinished)));
	}

	private void changeAlpha(float to) {
		Color col = getColor();
		col.a = to;
	}

	public boolean isSelected() {
		return this.selected;
	}

	private final Runnable onAnimationFinished = new Runnable(){
		@Override
		public void run() {
			selected = false;
			changeAlpha(1f);
			setTransform(false);			
		}
	};
}

