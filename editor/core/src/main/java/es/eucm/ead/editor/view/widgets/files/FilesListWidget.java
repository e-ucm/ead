/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2014 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          CL Profesor Jose Garcia Santesmases 9,
 *          28040 Madrid (Madrid), Spain.
 *
 *          For more info please visit:  <http://e-adventure.e-ucm.es> or
 *          <http://www.e-ucm.es>
 *
 * ****************************************************************************
 *
 *  This file is part of eAdventure
 *
 *      eAdventure is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU Lesser General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      (at your option) any later version.
 *
 *      eAdventure is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU Lesser General Public License for more details.
 *
 *      You should have received a copy of the GNU Lesser General Public License
 *      along with eAdventure.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.eucm.ead.editor.view.widgets.files;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.SnapshotArray;
import es.eucm.ead.editor.view.widgets.files.FileIconWidget.FileIconWidgetStyle;
import es.eucm.ead.editor.view.widgets.layouts.LinearLayout;

import java.util.Comparator;

/**
 * A widget representing a list of files
 */
public class FilesListWidget extends LinearLayout {

	/**
	 * To order files by type (folder - regular file) and name
	 */
	private final static FilesComparator fileComparator = new FilesComparator();

	private FilesListWidgetStyle style;

	private FileIconWidgetStyle folderStyle;

	private FileIconWidgetStyle fileStyle;

	/**
	 * Pool to create file icon listeners, to avoid massive instantiation of
	 * {@link FileClickListener}
	 */
	private ListenersPools listenersPools = new ListenersPools();

	/**
	 * Gives scroll to filesContainer
	 */
	private ScrollPane scrollPane;

	/**
	 * Container for the file icons
	 */
	private Group filesContainer;

	/**
	 * The current selected file
	 */
	private FileHandle selectedFile;

	/**
	 * The widget associated to the selected file
	 */
	private FileIconWidget selectedIcon;

	private TextField currentPath;

	public FilesListWidget(Skin skin) {
		super(false);

		style = skin.get(FilesListWidgetStyle.class);
		folderStyle = new FileIconWidgetStyle(style.folderIcon, style.font,
				style.fontColor, style.selected, style.over);
		fileStyle = new FileIconWidgetStyle(style.fileIcon, style.font,
				style.fontColor, style.selected, style.over);

		filesContainer = new Group();

		ScrollPaneStyle scrollStyle = skin.get(ScrollPaneStyle.class);
		scrollPane = new ScrollPane(filesContainer, scrollStyle);
		scrollPane.setFlickScroll(false);
		scrollPane.setFlingTime(0);
		scrollPane.setSmoothScrolling(false);

		currentPath = new TextField("", skin);
		currentPath.addListener(new InputListener() {
			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				switch (keycode) {
				case Keys.ENTER:
					String path = currentPath.getText();
					FileHandle fileHandle = Gdx.files.absolute(path);
					if (fileHandle.exists()) {
						setSelectedFile(fileHandle, true);
					}
					return true;
				}
				return false;
			}
		});

		add(scrollPane).expand(true, true);
		add(currentPath).expandX();

	}

	@Override
	protected void drawChildren(Batch batch, float parentAlpha) {
		if (style.background != null) {
			style.background.draw(batch, 0, 0, getWidth(), getHeight());
		}
		super.drawChildren(batch, parentAlpha);
	}

	/**
	 * Sets the current selected file
	 * 
	 * @param selectedFile
	 *            the file selected. Can be a file or directory
	 * @param open
	 *            if the file list must try to open selectedFile. If true, and
	 *            selectedFile is directory, the view is updated showing the
	 *            directory content
	 */
	public void setSelectedFile(FileHandle selectedFile, boolean open) {
		this.selectedFile = selectedFile;
		this.currentPath.setText(selectedFile.path());
		if (open) {
			updateView(selectedFile);
		}
	}

	/**
	 * @return the current selected file
	 */
	public FileHandle getSelectedFile() {
		return selectedFile;
	}

	/**
	 * Updates the widget to show the folder contents.
	 * 
	 * @param file
	 *            if file is a directory, shows its content. If it s a regular
	 *            file, shows the parent directory
	 */
	private void updateView(FileHandle file) {
		FileHandle folder = file;
		if (!folder.isDirectory()) {
			folder = folder.parent();
		}
		filesContainer.clearChildren();
		listenersPools.freeAll();
		FileHandle parent = folder.parent();
		if (parent != null) {
			FileIconWidget parentIcon = new FileIconWidget("..", true,
					folderStyle);
			parentIcon.addListener(listenersPools.obtain(parent));
			filesContainer.addActor(parentIcon);
		}
		for (FileHandle child : folder.list()) {
			FileIconWidget widget;
			if (child.isDirectory()) {
				widget = new FileIconWidget(child.name(), true, folderStyle);
			} else {
				widget = new FileIconWidget(child.name(), false, fileStyle);
			}
			filesContainer.addActor(widget);
			widget.addListener(new FileClickListener(child));
		}
		scrollPane.setScrollPercentY(0f);
		scrollPane.setScrollingDisabled(true, false);
		invalidateHierarchy();
	}

	@Override
	public float getPrefWidth() {
		return Gdx.graphics.getWidth();
	}

	@Override
	public float getPrefHeight() {
		return Gdx.graphics.getHeight();
	}

	@Override
	public void layout() {
		super.layout();

		scrollPane.invalidate();

		float x = style.margin;
		float y = -style.margin;
		float containerHeight = style.margin;
		float maxHeight = 0;
		SnapshotArray<Actor> children = filesContainer.getChildren();
		children.sort(fileComparator);

		for (Actor actor : children) {
			float height = getPrefHeight(actor);
			maxHeight = Math.max(maxHeight, height);
			float width = getPrefWidth(actor);
			if (x + width + style.margin > scrollPane.getWidth()) {
				x = style.margin;
				y -= maxHeight + style.margin;
				containerHeight += maxHeight + style.margin;
			}
			actor.setBounds(x, y - height, width, height);
			x += width + style.margin;
		}

		containerHeight += maxHeight;
		for (Actor actor : children) {
			actor.setY(actor.getY() + containerHeight);
		}
		filesContainer.setBounds(0, 0, scrollPane.getWidth(), containerHeight);

		if (containerHeight < scrollPane.getHeight()) {
			scrollPane.setY(scrollPane.getY() + scrollPane.getHeight()
					- containerHeight);
		}
	}

	public static class FilesListWidgetStyle {
		/**
		 * Icon for regular files
		 */
		public Drawable fileIcon;
		/**
		 * Icon for directories
		 */
		public Drawable folderIcon;
		/**
		 * Font for file names
		 */
		public BitmapFont font;
		/**
		 * Font color for file names
		 */
		public Color fontColor;
		/**
		 * Background for the list
		 */
		public Drawable background;

		/**
		 * Drawable for when individual file widget are selected
		 */
		public Drawable selected;

		/**
		 * Drawable for when mouse is over an individual file widget
		 */
		public Drawable over;

		/**
		 * Margin between icons
		 */
		public float margin = 30.0f;
	}

	/**
	 * Default listener for individual file widgets
	 */
	private class FileClickListener extends ClickListener {

		private FileHandle fileHandle;

		public FileClickListener() {

		}

		public FileClickListener(FileHandle fileHandle) {
			this.fileHandle = fileHandle;
		}

		public void setFileHandle(FileHandle fileHandle) {
			this.fileHandle = fileHandle;
		}

		@Override
		public void clicked(InputEvent event, float x, float y) {
			if (selectedIcon != null) {
				selectedIcon.setSelected(false);
			}
			if (event.getListenerActor() != null) {
				selectedIcon = ((FileIconWidget) event.getListenerActor());
			}

			if (selectedIcon != null) {
				selectedIcon.setSelected(true);
			}

			setSelectedFile(fileHandle, fileHandle.isDirectory()
					&& getTapCount() > 1);
		}
	}

	/**
	 * Pool to create file icon listeners, to avoid massive instantiation of
	 * {@link FileClickListener}
	 */
	private class ListenersPools extends Pool<FileClickListener> {

		private Array<FileClickListener> fileListeners = new Array<FileClickListener>();

		public FileClickListener obtain(FileHandle fh) {
			FileClickListener fileListener = obtain();
			fileListener.setFileHandle(fh);
			fileListeners.add(fileListener);
			return fileListener;
		}

		public void freeAll() {
			for (FileClickListener fileListener : fileListeners) {
				free(fileListener);
			}
		}

		@Override
		protected FileClickListener newObject() {
			return new FileClickListener();
		}
	}

	/**
	 * Compares files. This comparator compares first by type (folder or name),
	 * and then by name (using string comparison of the files names)
	 */
	private static class FilesComparator implements Comparator<Actor> {
		@Override
		public int compare(Actor actor, Actor actor2) {
			if (actor instanceof FileIconWidget
					&& actor2 instanceof FileIconWidget) {
				FileIconWidget w1 = (FileIconWidget) actor;
				FileIconWidget w2 = (FileIconWidget) actor2;

				if (w1.isFolder() != w2.isFolder()) {
					return w1.isFolder() ? -1 : 1;
				} else {
					return w1.getFileName().compareTo(w2.getFileName());
				}
			}
			return 0;
		}
	}

}
