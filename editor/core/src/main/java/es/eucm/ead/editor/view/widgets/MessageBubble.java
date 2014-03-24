package es.eucm.ead.editor.view.widgets;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 * Created by Angel-E-UCM on 14/03/14.
 */
public class MessageBubble extends Widget {


    Drawable drawable;


    @Override
    public void draw(Batch batch, float parentAlpha) {
        float width = drawable.getMinWidth();
        float height = drawable.getMinHeight();
        for (int i = 0; i <= getWidth() + width; i += width) {
            for (int j = 0; j <= getHeight() + height; j += height) {
                drawable.draw(batch, i, j, width, height);
                drawable.
            }
        }
        super.draw(batch, parentAlpha);
    }


}
