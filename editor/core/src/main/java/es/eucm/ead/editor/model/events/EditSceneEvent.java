package es.eucm.ead.editor.model.events;

import es.eucm.ead.schema.actors.Scene;

public class EditSceneEvent implements ModelEvent {

	private String name;
	
	private Scene scene;

	public EditSceneEvent(String name, Scene scene) {
		this.name = name;
		this.scene = scene;
	}

	public String getName() {
		return name;
	}

	public Scene getScene() {
		return scene;
	}
}
