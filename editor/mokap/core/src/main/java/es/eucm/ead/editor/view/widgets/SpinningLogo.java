package es.eucm.ead.editor.view.widgets;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import es.eucm.ead.editor.view.SkinConstants;

public class SpinningLogo extends Group {

	public SpinningLogo(Skin skin) {
		addActor(new Image(skin, SkinConstants.DRAWABLE_CUP));
        Image image = new Image(skin, SkinConstants.DRAWABLE_PENCIL);
        image.addAction(Actions.forever(Actions.rotateBy(-360, 1.2f, Interpolation.exp5Out)));
        image.pack();
        image.setOrigin(Align.center);
		addActor(image);
	}
}
