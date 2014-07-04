package es.eucm.ead.editor.ui.resources.frames;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Scaling;
import com.esotericsoftware.tablelayout.Cell;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.view.widgets.IconButton;
import es.eucm.ead.editor.view.widgets.focus.FocusItem;
import es.eucm.ead.engine.I18N;

public class FrameWidget extends FocusItem {

	private static final float IMAGE_WIDTH = 70F, IMAGE_HEIGHT = 100F;
	private static final String DEFAULT_DURATION = "40";
	private static final int MAX_DURATION_LENGTH = 8;

	private Cell<Actor> topCell;
	private TextField duration;
	private Table top;

	public FrameWidget(Image widget, Controller controller) {
		super(widget, controller);
	}

	@Override
	protected void build(Controller controller) {

		Skin skin = controller.getApplicationAssets().getSkin();
		I18N i18n = controller.getApplicationAssets().getI18N();

		this.duration = new TextField(DEFAULT_DURATION, skin) {
			@Override
			public float getPrefWidth() {
				return IMAGE_WIDTH;
			}
		};
		duration.setMaxLength(MAX_DURATION_LENGTH);
		duration.setRightAligned(true);

		IconButton dup = new IconButton("close", skin);
		dup.setTooltip(i18n.m("frames.duplicate"));
		IconButton delete = new IconButton("close", skin);
		dup.setTooltip(i18n.m("frames.delete"));

		top = new Table();
		top.setVisible(false);
		top.add(dup).left();
		top.add(delete).expandX().right();

		getImage().setScaling(Scaling.fit);
		topCell = add(top).expandX().fillX().ignore();
		row();
		add(widget).height(IMAGE_HEIGHT).width(IMAGE_WIDTH);
		row();
		add(duration);

	}

	public void setDuration(float duration) {
		this.duration.setText(String.valueOf(duration));
	}

	public Image getImage() {
		return (Image) widget;
	}

	@Override
	public void setFocus(boolean focus) {
		super.setFocus(focus);
		top.setVisible(focus);
		topCell.ignore(!focus);
		top.invalidateHierarchy();
	}
}
