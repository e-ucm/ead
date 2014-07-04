package es.eucm.ead.editor.ui.resources.frames;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.SnapshotArray;

import es.eucm.ead.editor.assets.ApplicationAssets;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.model.DropIntoArray;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.events.ListEvent;
import es.eucm.ead.editor.view.widgets.focus.FocusItem;
import es.eucm.ead.editor.view.widgets.focus.FocusItemList;
import es.eucm.ead.engine.assets.Assets.AssetLoadedCallback;
import es.eucm.ead.schema.renderers.Frame;
import es.eucm.ead.schema.renderers.Renderer;

/**
 * A {@link FocusItemList} that has drag and drop functionality between it's
 * {@link FocusItem items}.
 * 
 */
public class FramesTimeline extends FocusItemList {

	private final Vector2 tmp = new Vector2();

	/**
	 * The width of the left and right zone on which the scroll is automatically
	 * a increased/decreased.
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
	/**
	 * Reference to the frame being dragged that was just removed but not added
	 * yet. This avoids creating a new FrameWidget every time we drag and drop a
	 * frame from a position to another, by keeping track of the dragged
	 * {@link FrameWidget} and adding it to it's new position.
	 */
	private FrameWidget dragWidget;
	private ModelListener<ListEvent> framesListener = new DragModelListener();

	public FramesTimeline(Controller controller) {
		this.drag = new DragAndDrop();
		this.controller = controller;
	}

	@Override
	public void addFocusItem(FocusItem widget) {
		super.addFocusItem(widget);
		if (widget instanceof FrameWidget) {
			FrameWidget frame = (FrameWidget) widget;
			/*
			 * The items have to be targets and sources in order to be able to
			 * easily move them between the time line
			 */
			drag.addSource(newSource(frame));
			drag.addTarget(newTarget(frame));
		}
	}

	/**
	 * Loads the frames into the time line.
	 * 
	 * @param frames
	 */
	public void setFrames(Array<Frame> frames) {
		drag.clear();
		super.framesLayout.clear();
		this.frames = frames;
		for (Frame frame : frames) {
			loadFrameWidgetFromFrame(frame);
		}
		Model model = controller.getModel();
		model.removeListenerFromAllTargets(framesListener);
		model.addListListener(frames, framesListener);
	}

	/**
	 * Adds a new {@link FrameWidget} to the end of this {@link FramesTimeline
	 * time line} from a given {@link Frame}.
	 * 
	 * @param frame
	 */
	private void loadFrameWidgetFromFrame(Frame frame) {
		Renderer renderer = frame.getRenderer();
		if (renderer instanceof es.eucm.ead.schema.renderers.Image) {
			es.eucm.ead.schema.renderers.Image image = (es.eucm.ead.schema.renderers.Image) renderer;
			String uri = image.getUri();
			loadFrameWidgetFromURI(uri, frame.getTime());

		}
	}

	private void loadFrameWidgetFromURI(String fileName, final float duration) {
		ApplicationAssets assets = controller.getApplicationAssets();
		assets.get(fileName, Texture.class, new AssetLoadedCallback<Texture>() {

			@Override
			public void loaded(String fileName, Texture asset) {
				Image image = new Image();
				loadTextureIntoImage(asset, image);
				FrameWidget focusItem = new FrameWidget(image, controller);
				focusItem.setDuration(duration);
				addFocusItem(focusItem);
			}
		});
	}

	private void loadTextureIntoImage(Texture tex, Image image) {
		image.setDrawable(new TextureRegionDrawable(new TextureRegion(tex)));
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

	@Override
	public void act(float delta) {
		super.act(delta);

		// Detect if we're dragging via Drag'n Drop and if we're inside the
		// ACTION_ZONE so we start scrolling
		if (drag.isDragging()) {

			getStage().getRoot().stageToLocalCoordinates(
					tmp.set(Gdx.input.getX(), 0));

			if (tmp.x < ACTION_ZONE) {
				float deltaX = (1f - tmp.x / ACTION_ZONE);
				setScrollX(getScrollX() - deltaX * SCROLL_SPEED_MULTIPLIER);

			} else if (tmp.x > getWidth() - ACTION_ZONE) {
				float deltaX = (1f - (getWidth() - tmp.x) / ACTION_ZONE);
				setScrollX(getScrollX() + deltaX * SCROLL_SPEED_MULTIPLIER);

			}
		}
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
				SnapshotArray<Actor> children = FramesTimeline.this.framesLayout
						.getChildren();

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

			}
		};
	}

	private class DragModelListener implements ModelListener<ListEvent> {
		@Override
		public void modelChanged(ListEvent event) {
			switch (event.getType()) {
			case ADDED:
				final int num = event.getIndex();
				final SnapshotArray<Actor> children = framesLayout
						.getChildren();
				if (dragWidget != null) {
					// We're inserting a FrameWidget that was just
					// removed after a drag and drop action
					FramesTimeline.this.framesLayout.add(num, dragWidget)
							.margin(PAD);

					setFocus(dragWidget);

					// Now we center the scroll at the frame we've just added
					centerScrollAt(dragWidget);

					dragWidget = null;

					for (int i = children.size - 1; i > num; --i) {
						children.swap(i, i - 1);
					}

				} else {
					// We're probably inserting FrameWidgets that
					// the user has just imported from the
					// FileChooser
					Frame newFrame = ((Frame) event.getElement());
					loadFrameWidgetFromFrame(newFrame);
				}
				break;
			case REMOVED:
				int num1 = event.getIndex();

				dragWidget = (FrameWidget) framesLayout.getChildren().get(num1);
				dragWidget.remove();
				break;
			default:
				break;
			}
		}

		private void centerScrollAt(final Actor actor) {

			Gdx.app.postRunnable(new Runnable() {

				@Override
				public void run() {
					setScrollX(actor.getX() - getWidth() * .5f
							+ actor.getWidth() * .5f);
				}
			});
		}
	}
}
