package es.eucm.ead.editor.ui.resources.frames;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Pools;
import com.esotericsoftware.tablelayout.Value;

import es.eucm.ead.editor.assets.ApplicationAssets;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.Selection;
import es.eucm.ead.editor.control.actions.model.SetFramesSequence;
import es.eucm.ead.editor.control.actions.model.SetSelection;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Model.FieldListener;
import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.Model.SelectionListener;
import es.eucm.ead.editor.model.events.FieldEvent;
import es.eucm.ead.editor.model.events.ListEvent;
import es.eucm.ead.editor.model.events.SelectionEvent;
import es.eucm.ead.engine.ComponentLoader;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.engine.components.renderers.frames.FramesComponent;
import es.eucm.ead.schema.renderers.Frame;
import es.eucm.ead.schema.renderers.Frames;
import es.eucm.ead.schema.renderers.Frames.Sequence;
import es.eucm.ead.schemax.FieldName;

/**
 * A widget that allows frame-based edition in order to create animations.
 */
public class AnimationEditor extends Table {

	private static final String SEQUENCE_PREFIX = "sequence.";

	private static final float BOTTOM_PAD = 30F;

	private Frames frames;
	private String prevSequence;
	private Controller controller;
	private FramesTimeline timeline;
	private PreviewView previewView;
	private SelectBox<String> sequenceBox;
	private FieldListener sequenceListener = new SequenceFieldListener();
	private ModelListener<ListEvent> framesListener = new FramesListListener();
	private SelectionListener selectionListener = new FramesSelectionListener();

	public AnimationEditor(Controller control) {

		this.controller = control;
		controller.getModel().addSelectionListener(selectionListener);

		previewView = new PreviewView();

		timeline = new FramesTimeline(controller);

		ApplicationAssets assets = controller.getApplicationAssets();
		Skin skin = assets.getSkin();
		I18N i18n = assets.getI18N();

		TextButton play = new TextButton("play", skin);
		play.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				previewView.togglePlaying();
			}
		});

		final Sequence[] values = Sequence.values();
		String[] sequence = new String[values.length];
		for (int i = 0; i < values.length; ++i) {
			sequence[i] = i18n.m(SEQUENCE_PREFIX + values[i].toString());
		}
		this.sequenceBox = new SelectBox<String>(skin);
		this.sequenceBox.setItems(sequence);
		prevSequence = sequenceBox.getSelected();
		this.sequenceBox.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				String selectedOrdering = sequenceBox.getSelected();

				if (prevSequence != selectedOrdering) {

					controller.action(SetFramesSequence.class, frames,
							values[sequenceBox.getSelectedIndex()]);
				}
			}
		});

		add(new Container(previewView).fill()).expand().fill().colspan(2);
		row();
		add(play).expandX();
		add(sequenceBox).right();
		row();
		add(timeline).padBottom(BOTTOM_PAD).colspan(2)
				.minHeight(Value.percentHeight(1f, timeline));
	}

	/**
	 * Loads the frames into a {@link FramesTimeline} and also displays them in
	 * the {@link PreviewView}.
	 * 
	 */
	public void setFrames(Frames frames) {

		Model model = controller.getModel();
		model.removeListenerFromAllTargets(framesListener);
		model.addListListener(frames.getFrames(), framesListener);
		model.removeListenerFromAllTargets(sequenceListener);
		model.addFieldListener(frames, sequenceListener);

		controller.action(SetSelection.class, null, Selection.FRAMES, frames);
	}

	private void refreshPreview() {
		FramesComponent previousComponent = previewView.getPreviewFrames();
		if (previousComponent != null) {
			Pools.free(previousComponent);
		}
		previewView.setPreviewFrames(createFramesComponent());
		previewView.invalidateHierarchy();
	}

	private FramesComponent createFramesComponent() {
		ComponentLoader compLoader = controller.getEngine().getEntitiesLoader()
				.getComponentLoader();
		return (FramesComponent) compLoader.toEngineComponent(frames);
	}

	/**
	 * Used to notify the {@link PreviewView} when a {@link FrameWidget} has
	 * gained focus in the {@link FramesTimeline}. Also notifies when a
	 * specified frame changes his duration.
	 * 
	 */
	interface FrameEditionListener {

		/**
		 * Invoked when a frame has gained focus.
		 * 
		 * @param frame
		 */
		void frameSelected(int index);

		/**
		 * Invoked when a frame duration has changed.
		 * 
		 * @param frame
		 */
		void frameTimeChanged(int index, float newValue);
	}

	/**
	 * Used to notify added/removed frame events to the widgets.
	 * 
	 */
	private class FramesListListener implements ModelListener<ListEvent> {

		@Override
		public void modelChanged(ListEvent event) {
			int num = event.getIndex();
			Frame elem = (Frame) event.getElement();
			switch (event.getType()) {
			case ADDED:
				refreshPreview();
				timeline.frameAdded(num, elem, previewView);
				break;
			case REMOVED:
				refreshPreview();
				timeline.frameRemoved(num, elem);
				break;
			}
		}
	}

	/**
	 * Used to know when to load new frames or when to notify the widgets that a
	 * new frame has received focus.
	 */
	private class FramesSelectionListener implements SelectionListener {

		boolean isFrames;

		@Override
		public void modelChanged(SelectionEvent event) {
			if (isFrames) {
				if (event.getType() == SelectionEvent.Type.FOCUSED) {
					frames = (Frames) event.getSelection().first();
					refreshPreview();
					timeline.loadFrames(frames.getFrames(), previewView);
				}
			} else {
				int index;
				switch (event.getType()) {
				case FOCUSED:
				case REMOVED:
					index = frames.getFrames().indexOf(
							(Frame) event.getSelection().first(), true);
					timeline.centerScrollAt(index);
					previewView.frameSelected(index);
					break;
				case ADDED:
					break;
				}
			}
		}

		@Override
		public boolean listenToContext(String contextId) {
			isFrames = contextId.equals(Selection.FRAMES);
			return isFrames || contextId.equals(Selection.FRAME);
		}
	}

	/**
	 * Notifies the {@link PreviewView} that the sequence has changed.
	 */
	private class SequenceFieldListener implements FieldListener {

		@Override
		public void modelChanged(FieldEvent event) {
			refreshPreview();
			sequenceBox.setSelected(prevSequence = controller
					.getApplicationAssets().getI18N()
					.m(SEQUENCE_PREFIX + event.getValue().toString()));
		}

		@Override
		public boolean listenToField(String fieldName) {
			return FieldName.SEQUENCE.equals(fieldName);
		}
	}
}
