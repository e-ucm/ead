package es.eucm.ead.editor.ui.resources.frames;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
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
import es.eucm.ead.editor.view.widgets.dragndrop.focus.FocusItemList;
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

	private Controller controller;
	private Array<Frame> frames;

	public FramesTimeline(Controller control) {
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
		addListener(new DropListener<DropListEvent>() {

			@Override
			public void actorDropped(DropListEvent event) {
				int targetIndex = event.getNewIndex();
				Frame dropFrame = frames.get(event.getOldIndex());

				// Perform a composite command that removes the drag actor from
				// it's current position and adds him to it's target position
				controller.action(DropIntoArray.class, Selection.FRAMES,
						frames, dropFrame, targetIndex);
			}
		});

		addListener(new FocusListener() {

			@Override
			public void focusChanged(FocusEvent event) {
				controller.action(SetSelection.class, Selection.FRAMES,
						Selection.FRAME,
						((FrameWidget) event.getActor()).getFrame());
			}
		});
	}

	@Override
	public void fileChosen(String path) {
		if (path != null) {
			controller.action(AddFrames.class, path, null, frames);
		}
	}

	/**
	 * Loads the frames into the time line.
	 * 
	 * @param frames
	 */
	public void loadFrames(Array<Frame> frames, FrameEditionListener listener) {
		clearDrag();
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
		if (frames.size > 0) {
			// Select the first frame if available after loading
			controller.action(SetSelection.class, Selection.FRAMES,
					Selection.FRAME, frames.first());
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
		focusItem.setFrameEditionListener(listener);
		addActorAt(index, focusItem);
	}

	void delete(FrameWidget frame) {
		int delIdx = indexOf(frame);
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
	}

	int indexOf(FrameWidget frame) {
		return itemsList.getChildren().indexOf(frame, true);
	}

	void frameAdded(int index, Frame elem, FrameEditionListener listener) {
		SnapshotArray<Actor> children = itemsList.getChildren();
		if (index < children.size) {
			if (((FrameWidget) children.get(index)).getFrame() != elem) {
				// If the widget in that position already displays that Frame we
				// don't load it. This happens when we drop an item after a
				// drag'n
				// drop event.
				loadFrameWidgetFromFrame(elem, listener, index);
			}
			controller.action(SetSelection.class, Selection.FRAMES,
					Selection.FRAME, elem);
		} else {
			loadFrameWidgetFromFrame(elem, listener, index);
			controller.action(SetSelection.class, Selection.FRAMES,
					Selection.FRAME, elem);
		}
	}

	void frameRemoved(int index, Frame elem) {
		SnapshotArray<Actor> children = itemsList.getChildren();
		if (index < children.size) {
			FrameWidget removedWidget = (FrameWidget) children.get(index);
			if (removedWidget.getFrame() == elem) {
				itemsList.removeActor(removedWidget);
				removedWidget.clear();
			}
		}
	}
}
