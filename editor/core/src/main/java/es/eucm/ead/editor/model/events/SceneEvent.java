package es.eucm.ead.editor.model.events;

import es.eucm.ead.schema.actors.Scene;

public class SceneEvent implements ModelEvent {

	public Type getType() {
		return type;
	}

	public enum Type {
		EDIT, ADDED, REMOVED
	}

	private String name;

	private Scene scene;

	private Type type;

	public SceneEvent(String name, Scene scene, Type type) {
		this.name = name;
		this.scene = scene;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public Scene getScene() {
		return scene;
	}

	@Override
	public String toString() {
		return "SceneEvent{" + "name='" + name + '\'' + ", type=" + type + '}';
	}
}
