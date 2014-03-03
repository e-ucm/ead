package es.eucm.ead.editor.control.actions;

import com.badlogic.gdx.files.FileHandle;
import es.eucm.ead.editor.Editor;
import es.eucm.ead.editor.assets.ProjectAssets;
import es.eucm.ead.editor.control.commands.*;
import es.eucm.ead.editor.model.Project;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Javier Torrente on 3/03/14.
 */
public class DeleteScene extends EditorAction {

    public static final String NAME = "deleteScene";

    public DeleteScene() {
        super(NAME);
    }

    @Override
    public void perform(Object... args) {
        Project project = controller.getModel().getProject();
        // If there's only one scene, then this action cannot be done and the user must be warned.
        if (controller.getModel().getScenes().size()==1){
            //TODO This is to be done
        }
        // There are more than only one scene
        else {
            List<Command> commandList = new ArrayList<Command>();
            // The action of deleting an scene involves the next commands:
            // 1) If the scene is the "editScene", change the editscene
            if (project.getEditScene().equals(args[0])){
                commandList.add(new FieldCommand(project, EditScene.NAME, findAlternateScene((String)args[0]), false));
            }

            // 2) If the scene is the "initialscene", change the initial one
            if (controller.getModel().getGame().getInitialScene().equals(args[0])){
                commandList.add(new FieldCommand(controller.getModel().getGame(), InitialScene.NAME, findAlternateScene((String)args[0]), false));
            }

            // 3) Delete the scene properly speaking
            commandList.add(new MapCommand.RemoveFromMapCommand(controller.getModel().getScenes(), args[0]));
            CompositeCommand deleteSceneCommand = new CompositeCommand(commandList);
            controller.command(deleteSceneCommand);

            // Finally, delete the scene file. This is necessary since the file won't get deleted when the game project is saved. This operation does not need to be undone.
            deleteSceneFile((String) args[0]);
        }
    }

    /**
     * Method that returns the name of a scene that is different from the one given as a parameter.
     * In case there's only one scene, it will return null
     * @param scene The name of the scene that should not be returned
     * @return  The name of a scene that is not equals to the given one
     */
    private String findAlternateScene(String scene){
        String alternateScene = null;
        for (String sceneName:controller.getModel().getScenes().keySet()){
            if (!sceneName.equals(scene)){
                alternateScene = sceneName;break;
            }
        }
        return alternateScene;
    }

    /**
     * Deletes the file of the scene with the given name. First it finds out the full path of the scene. Then, it resolves the {@link com.badlogic.gdx.files.FileHandle} that wraps the file and uses it to get it deleted from disk.
     *
     * This method is placed in {@link es.eucm.ead.editor.control.actions.DeleteScene} because it should not be called from elsewhere
     *
     * @param sceneName The name of the scene, without extension or folder (e.g. "scene0").
     * @return  True if the file was deleted, false otherwise
     */
    private boolean deleteSceneFile(String sceneName){
        ProjectAssets projectAssets = controller.getProjectAssets();
        String path =
                projectAssets.convertSceneNameToPath(sceneName);
        FileHandle handle = projectAssets.resolve(path);
        if (handle!=null && handle.exists()){
            return handle.delete();
        }
        return false;
    }
}
