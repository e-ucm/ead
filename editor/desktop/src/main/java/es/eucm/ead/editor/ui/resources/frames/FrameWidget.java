package es.eucm.ead.editor.ui.resources.frames;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.esotericsoftware.tablelayout.Cell;

import es.eucm.ead.editor.assets.ApplicationAssets;
import es.eucm.ead.editor.assets.EditorGameAssets;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.model.SetFrameTime;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Model.FieldListener;
import es.eucm.ead.editor.model.events.FieldEvent;
import es.eucm.ead.editor.ui.resources.frames.AnimationEditor.FrameEditionListener;
import es.eucm.ead.editor.view.widgets.IconButton;
import es.eucm.ead.editor.view.widgets.dragndrop.focus.FocusItem;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.engine.assets.Assets.AssetLoadedCallback;
import es.eucm.ead.schema.renderers.Frame;
import es.eucm.ead.schemax.FieldName;

/**
 * A {@link FocusItem} that has a duplicate and delete button on top and also a
 * {@link TextField} at the bottom in order to edit the duration of the
 * {@link Frame}. Used by the {@link FramesTimeline}.
 */
public class FrameWidget extends FocusItem {

	private static final float IMAGE_WIDTH = 70F, IMAGE_HEIGHT = 100F;
	private static final int MAX_DURATION_LENGTH = 4;

	private FrameEditionListener listener;
	private FramesTimeline timeline;
	private Cell<Actor> topCell;
	private float previousTime;
	private TextField time;
	private Frame frame;
	private Table top;
	private FieldListener textfieldListener = new FieldListener() {

		@Override
		public void modelChanged(FieldEvent event) {
			Float newValue = (Float) event.getValue();
			timeChanged(newValue);
		}

		@Override
		public boolean listenToField(String fieldName) {
			return FieldName.TIME.equals(fieldName);
		}

		void timeChanged(float newValue) {
			int cursosPos = time.getCursorPosition();
			time.setText(String.valueOf(newValue));
			time.setCursorPosition(cursosPos);
			previousTime = newValue;
			int index = timeline.indexOf(FrameWidget.this);
			if (index != -1) {
				listener.frameTimeChanged(index, newValue);
			}
		}

	};

	public FrameWidget(Frame fram, Controller controller,
			FramesTimeline timeline) {
		super(null, controller);
		this.timeline = timeline;
		this.frame = fram;
		init(controller);
	}

	@Override
	protected void build(Controller controller) {

		widget = new Image();

		ApplicationAssets assets = controller.getApplicationAssets();
		Skin skin = assets.getSkin();
		I18N i18n = assets.getI18N();

		this.time = new TextField("", skin) {
			@Override
			public float getPrefWidth() {
				return IMAGE_WIDTH;
			}
		};

		IconButton dup = new IconButton("copy24x24", skin);
		dup.setTooltip(i18n.m("frames.duplicate"));
		dup.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				timeline.duplicate(FrameWidget.this);
			}
		});

		IconButton delete = new IconButton("recycle24x24", skin);
		delete.setTooltip(i18n.m("frames.delete"));
		delete.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				timeline.delete(FrameWidget.this);
			}
		});

		top = new Table();
		top.setVisible(false);
		top.add(dup).left();
		top.add(delete).expandX().right();

		getImage().setScaling(Scaling.fit);
		topCell = add().expandX().fillX();
		row();
		add(widget).height(IMAGE_HEIGHT).width(IMAGE_WIDTH);
		row();
		add(time);
	}

	private void init(final Controller controller) {

		time.setRightAligned(true);
		time.setMaxLength(MAX_DURATION_LENGTH);
		time.setText(String.valueOf(frame.getTime()));
		time.setTextFieldFilter(new TextFieldFilter() {

			@Override
			public boolean acceptChar(TextField textField, char c) {
				return Character.isDigit(c) || c == '.';
			}
		});
		time.addListener(new InputListener() {

			private String previousText = time.getText();

			@Override
			public boolean keyTyped(InputEvent event, char character) {
				String text = time.getText();
				if (!text.isEmpty() && !text.equals(previousText)) {
					float timeVal = previousTime;
					try {
						timeVal = Float.valueOf(text);
					} catch (NumberFormatException formatEx) {
						Gdx.app.error("FrameWidget",
								"Error getting frame time, setting previous time: "
										+ previousTime, formatEx);
					}

					if (timeVal != previousTime) {
						controller.action(SetFrameTime.class, frame, timeVal);
						previousText = String.valueOf(timeVal);
					}
				}
				return true;
			}
		});

		EditorGameAssets assets = controller.getEditorGameAssets();
		assets.get(((es.eucm.ead.schema.renderers.Image) frame.getRenderer())
				.getUri(), Texture.class, new AssetLoadedCallback<Texture>() {

			@Override
			public void loaded(String fileName, Texture asset) {
				((Image) widget).setDrawable(new TextureRegionDrawable(
						new TextureRegion(asset)));
			}
		}, true);

		controller.getModel().addFieldListener(frame, textfieldListener);
	}

	void clearTextFieldListener(Model model) {
		model.removeListener(frame, textfieldListener);
	}

	public Image getImage() {
		return (Image) widget;
	}

	@Override
	public void setFocus(boolean focus) {
		super.setFocus(focus);
		top.setVisible(focus);
		if (!focus) {
			if (topCell.hasWidget()) {
				topCell.setWidget(null);
			}
		} else {
			topCell.setWidget(top);
		}
		top.invalidateHierarchy();
		previousTime = Float.valueOf(time.getText());
	}

	public void setFrameEditionListener(FrameEditionListener listener) {
		this.listener = listener;
	}

	public FrameEditionListener getFrameEditionListener() {
		return listener;
	}

	public Frame getFrame() {
		return frame;
	}
}
