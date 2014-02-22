package es.eucm.ead.editor.view.widgets.mockup;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.esotericsoftware.tablelayout.Cell;

/**
 * A simple Table with one row and background
 */
public class ToolBar extends Table {

		private Drawable stageBackground;

		/**
		 * Create a {@link ToolBar toolbar} with default style.
		 * 
		 * @param skin
		 *            the skin to use
		 */
		public ToolBar(Skin skin) {
			super(skin);
			setBackground("blueBlackMedium");
		}

		/**
		 * Create a {@link ToolBar toolbar} with the specified style.
		 */
		public ToolBar(Skin skin, String drawableBackground) {
			super(skin);
			setBackground(drawableBackground);
			
		}

		@Override
		public Cell<?> row() {
			throw new IllegalStateException("There are no rows in a ToolBar");
		}
		
		@Override
		protected void drawBackground(Batch batch, float parentAlpha, float x,
				float y) {
			if (stageBackground != null) {
				Color color = getColor();
				batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
				Stage stage = getStage();
				stageBackground.draw(batch, 0, 0, stage.getWidth(),
						stage.getHeight());

			}
			super.drawBackground(batch, parentAlpha, x, y);
		}
}

