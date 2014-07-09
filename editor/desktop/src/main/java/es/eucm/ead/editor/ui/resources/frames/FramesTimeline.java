package es.eucm.ead.editor.ui.resources.frames;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.SnapshotArray;

import es.eucm.ead.editor.assets.ApplicationAssets;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.editor.ChooseFile;
import es.eucm.ead.editor.control.actions.model.AddFrameToFrames;
import es.eucm.ead.editor.control.actions.model.AddFrames;
import es.eucm.ead.editor.control.actions.model.DropIntoArray;
import es.eucm.ead.editor.control.actions.model.RemoveFrameFromFrames;
import es.eucm.ead.editor.control.actions.model.SetSelection;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.platform.Platform.FileChooserListener;
import es.eucm.ead.editor.ui.resources.frames.AnimationEditor.FrameEditionListener;
import es.eucm.ead.editor.view.widgets.IconButton;
import es.eucm.ead.editor.view.widgets.focus.FocusItem;
import es.eucm.ead.editor.view.widgets.focus.FocusItemList;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.schema.renderers.Frame;

/**
 * A {@link FocusItemList} that has drag and drop functionality between it's
 * {@link FrameWidget items}. Used by the {@link AnimationEditor}.
 * 
 */
public class FramesTimeline extends FocusItemList implements
		FileChooserListener {

	private static final Vector2 TMP = new Vector2();

	/**
	 * The width of the left and right zone on which the scroll is automatically
	 * increased/decreased.
	 */
	private static final float ACTION_ZONE = 90F;

	/**
	 * The factor that proportionally increases the scroll speed while in the
	 * {@link #ACTION_ZONE}.
	 */
	private static final float SCROLL_SPEED_MULTIPLIER = 10F;

	private DragAndDrop drag;
	private Controller controller;
	private Array<Frame> frames;

	public FramesTimeline(Controller control) {
		this.drag = new DragAndDrop();
		this.controller = control;
		setFadeScrollBars(false);

		ApplicationAssets assets = controller.getApplicationAssets();
		Skin skin = assets.getSkin();
		I18N i18n = assets.getI18N();

		IconButton importButton = new IconButton("close", skin);
		importButton.setTooltip(i18n.m("frames.delete"));
		importButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				controller.action(ChooseFile.class, false, FramesTimeline.this);
			}
		});

		LinearLayout container = new LinearLayout(true);
		container.pad(PAD);
		container.add(itemsList).expand(true, true);
		container.add(importButton);
		setWidget(container);
	}

	@Override
	public void addFocusItemAt(int index, FocusItem widget) {
		super.addFocusItemAt(index, widget);
		if (widget instanceof FrameWidget) {
			FrameWidget frame = (FrameWidget) widget;

			/*
			 * The items have to be targets and sources in order to be able to
			 * easily move them between the time line
			 */
			drag.addSource(frame.getSource());
			drag.addTarget(frame.getTarget());
		}
	}

	@Override
	public void act(float delta) {
		super.act(delta);

		// Detect if we're dragging via Drag'n Drop and if we're inside the
		// ACTION_ZONE so we start scrolling
		if (drag.isDragging()) {

			getStage().getRoot().stageToLocalCoordinates(
					TMP.set(Gdx.input.getX(), 0));

			if (TMP.x < ACTION_ZONE) {
				float deltaX = (1f - TMP.x / ACTION_ZONE);
				setScrollX(getScrollX() - deltaX * SCROLL_SPEED_MULTIPLIER);

			} else if (TMP.x > getWidth() - ACTION_ZONE) {
				float deltaX = (1f - (getWidth() - TMP.x) / ACTION_ZONE);
				setScrollX(getScrollX() + deltaX * SCROLL_SPEED_MULTIPLIER);

			}
		}
	}

	@Override
	public void fileChosen(String path) {
		if (path != null) {
			controller.action(AddFrames.class, path, null, frames);
			controller.action(SetSelection.class, Selection.FRAMES,
					Selection.FRAME, frames.get(frames.size - 1));
		}
	}

	/**
	 * Loads the frames into the time line.
	 * 
	 * @param frames
	 */
	public void loadFrames(Array<Frame> frames, FrameEditionListener listener) {
		drag.clear();
		Model model = controller.getModel();
		SnapshotArray<Actor> children = itemsList.getChildren();
		for (Actor frame : children) {
			((FrameWidget) frame).clearTextFieldListener(model);
		}
		super.itemsList.clear();
		this.frames = frames;
		for (Frame frame : frames) {
			loadFrameWidgetFromFrame(frame, listener, -1);
		}
		if (children.size > 0) {
			FocusItem firstFocus = (FocusItem) children.first();
			super.setFocus(firstFocus);
		}
	}

	/**
	 * Adds a new {@link FrameWidget} to the end of this {@link FramesTimeline
	 * time line} from a given {@link Frame}. If index is -1 the widget will be
	 * added at the end of the list.
	 * 
	 * @param frame
	 */
	private void loadFrameWidgetFromFrame(Frame frame,
			FrameEditionListener listener, int index) {
		FrameWidget focusItem = new FrameWidget(frame, controller,
				FramesTimeline.this);
		focusItem.setTarget(newTarget(focusItem));
		focusItem.setSource(newSource(focusItem));
		focusItem.setFrameEditionListener(listener);
		addFocusItemAt(index, focusItem);
	}

	void delete(FrameWidget frame) {
		int delIdx = indexOf(frame);
		int size = frames.size;
		if (size > 0) {
			controller.action(SetSelection.class, Selection.FRAMES,
					Selection.FRAME,
					frames.get(delIdx == size ? delIdx - 1 : delIdx + 1));
		}
		controller.action(RemoveFrameFromFrames.class, frames,
				frames.get(delIdx));
	}

	void duplicate(FrameWidget frameWidget) {

		int currIdx = indexOf(frameWidget);

		// We must create a duplicated Frame from the current
		Frame currFrame = frames.get(currIdx);
		Frame dupFrame = new Frame();
		dupFrame.setRenderer(currFrame.getRenderer());
		dupFrame.setTime(currFrame.getTime());

		controller
				.action(AddFrameToFrames.class, frames, dupFrame, currIdx + 1);
		controller.action(SetSelection.class, Selection.FRAMES,
				Selection.FRAME, dupFrame);
	}

	private Source newSource(final FrameWidget widget) {
		return new Source(widget) {

			@Override
			public Payload dragStart(InputEvent event, float x, float y,
					int pointer) {
				// Necessary to be able to drag and drop
				setCancelTouchFocus(false);

				// Necessary to stop the scroll from moving while dragging
				cancel();

				// Set the dragged item invisible
				widget.setVisible(false);

				// Set the actor displayed while dragging
				// It must have the same size as the source image
				Payload payload = new Payload();
				Image sourceImage = widget.getImage();
				Image dragImage = new Image(sourceImage.getDrawable());
				dragImage.setScaling(Scaling.fit);
				dragImage.setSize(sourceImage.getWidth(),
						sourceImage.getHeight());
				payload.setDragActor(dragImage);

				return payload;
			}

			@Override
			public void dragStop(InputEvent event, float x, float y,
					int pointer, Payload payload, Target target) {
				// Return the ScrollPane to it's original state
				setCancelTouchFocus(true);

				if (target == null) {
					// The pay load was not dropped over a target, thus put it
					// back to where it came from. In this case just set it
					// visible again.
					widget.setVisible(true);
				}
			}
		};
	}

	private Target newTarget(final FrameWidget widget) {
		return new Target(widget) {

			@Override
			public boolean drag(Source source, Payload payload, float x,
					float y, int pointer) {
				return true;
			}

			@Override
			public void drop(Source source, Payload payload, float x, float y,
					int pointer) {
				SnapshotArray<Actor> children = itemsList.getChildren();

				// Make the actor visible again and calculate it's previous
				// position
				FrameWidget dragActor = (FrameWidget) source.getActor();
				dragActor.setVisible(true);

				int dragIdx = children.indexOf(dragActor, true);
				Frame dragFrame = frames.get(dragIdx);

				// Compute it's new position
				int targetIdx = children.indexOf(widget, true);

				// If the drag Actor had a smaller position in the array than
				// the target Actor then we must must decrease a position since
				// the dragActor will be removed
				if (targetIdx > dragIdx) {
					--targetIdx;
				}

				// If the position is higher than half of the width of the
				// target frame the add the source after the target
				if (x > widget.getWidth() * .5f) {
					++targetIdx;
				}

				// Perform a composite command that removes the drag actor from
				// it's current position and adds him to it's target position
				controller.action(DropIntoArray.class, null, frames, dragFrame,
						targetIdx);
				controller.action(SetSelection.class, Selection.FRAMES,
						Selection.FRAME, dragFrame);

			}
		};
	}

	@Override
	protected void setFocus(FocusItem newFocus) {
		if (needsFocus(newFocus)) {
			controller.action(SetSelection.class, Selection.FRAMES,
					Selection.FRAME, ((FrameWidget) newFocus).getFrame());
		}
	}

	int indexOf(FrameWidget frame) {
		return itemsList.getChildren().indexOf(frame, true);
	}

	void frameAdded(int index, Frame elem, FrameEditionListener listener) {
		loadFrameWidgetFromFrame(elem, listener, index);
	}

	void frameRemoved(int index, Frame elem) {
		SnapshotArray<Actor> children = itemsList.getChildren();
		FrameWidget removedWidget = (FrameWidget) children.get(index);
		drag.removeSource(removedWidget.getSource());
		drag.removeTarget(removedWidget.getTarget());
		itemsList.removeActor(removedWidget);
		removedWidget.clear();
	}

	void centerScrollAt(int index) {
		centerScrollAt((FocusItem) itemsList.getChildren().get(index));
	}

	private void centerScrollAt(final FocusItem actor) {

		Gdx.app.postRunnable(new Runnable() {

			@Override
			public void run() {
				setScrollX(actor.getX() - getWidth() * .5f + actor.getWidth()
						* .5f);
				FramesTimeline.super.setFocus(actor);
			}
		});
	}
}
