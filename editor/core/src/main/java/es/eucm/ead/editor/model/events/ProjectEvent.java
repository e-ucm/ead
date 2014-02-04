package es.eucm.ead.editor.model.events;

import es.eucm.ead.editor.model.Project;

public class ProjectEvent implements ModelEvent {

	private Project project;

	public ProjectEvent(Project project) {
		this.project = project;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	@Override
	public String toString() {
		return "ProjectEvent{" + "project=" + project + '}';
	}
}
