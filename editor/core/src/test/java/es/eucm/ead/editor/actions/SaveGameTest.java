package es.eucm.ead.editor.actions;

import es.eucm.ead.editor.assets.ProjectAssets;
import es.eucm.ead.editor.control.EditorIO;
import es.eucm.ead.editor.control.actions.OpenGame;
import es.eucm.ead.schema.actors.Scene;
import es.eucm.ead.schema.actors.SceneElement;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * This class is meant to test whether the {@link es.eucm.ead.editor.control.EditorIO#saveAll(es.eucm.ead.editor.model.Model)} works OK. This is the method invoked when the user hits Ctrl+S.
 *
 * {@link es.eucm.ead.editor.control.EditorIO#saveAll(es.eucm.ead.editor.model.Model)} should remove all json files from disk before performing any save operation. This test will emphasize checking this aspect.
 *
 * Created by Javier Torrente on 5/03/14.
 */
public class SaveGameTest extends EditorTest{

    @Test
    public void testSaveAll(){
        // Create a temp directory for the project. This directory will be initially empty
        String gameFolderPath = null;
        try {
            Path tempDirPath = Files.createTempDirectory("ead-savegametest");
            gameFolderPath = tempDirPath.toFile().getAbsolutePath();
            new File(gameFolderPath).mkdirs();
            mockController.getProjectAssets().setLoadingPath(gameFolderPath);
        } catch (IOException e) {
            e.printStackTrace();
            fail("Exception in SaveGameTest: "+e.toString());
        }

        // Make dummy additions to game model
        for (int j=0; j<5; j++){
            Scene scene = new Scene();
            for (int i=0; i<3; i++){
                SceneElement sceneElement = new SceneElement();
                scene.getChildren().add(sceneElement);
            }
            mockModel.getScenes().put("scene"+j, scene);
        }

        // Init editorIO
        EditorIO editorIO = new EditorIO(mockController);

        // Save the model
        editorIO.saveAll(mockModel);

        // Test all files were actually stored
        testFileExists(gameFolderPath, ProjectAssets.GAME_FILE);
        testFileExists(gameFolderPath, ProjectAssets.PROJECT_FILE);
        for (int i=0; i<5; i++){
            testFileExists(gameFolderPath, ProjectAssets.SCENES_PATH+"scene"+i+".json");
        }

        // Now, change the model. All scenes but one (scene3) will be removed. A new scene2 will be created with 1 scene element.
        for (int i=0; i<5; i++){
            if (i!=3)
                mockModel.getScenes().remove("scene"+i);
        }

        Scene scene2 = new Scene();
        SceneElement sceneElement = new SceneElement();
        scene2.getChildren().add(sceneElement);
        mockModel.getScenes().put("scene2", scene2);

        // Save the model again
        editorIO.saveAll(mockModel);

        // Test new persistent state. game.json, project.json, scenes/scene2.json and scenes/scene3.json should be the only files in the directory.
        testFileExists(gameFolderPath, ProjectAssets.GAME_FILE);
        testFileExists(gameFolderPath, ProjectAssets.PROJECT_FILE);
        testFileExists(gameFolderPath, ProjectAssets.SCENES_PATH+"scene2.json");
        testFileExists(gameFolderPath, ProjectAssets.SCENES_PATH+"scene3.json");
        testFileDoesNotExist(gameFolderPath, ProjectAssets.SCENES_PATH + "scene0.json");
        testFileDoesNotExist(gameFolderPath, ProjectAssets.SCENES_PATH+"scene1.json");
        testFileDoesNotExist(gameFolderPath, ProjectAssets.SCENES_PATH+"scene4.json");

        // Now, test scene 2 has only 1 scene element
        mockController.action(OpenGame.NAME, new File(gameFolderPath, ProjectAssets.PROJECT_FILE).getAbsolutePath());

        assertTrue(mockController.getModel().getScenes().get("scene2").getChildren().size()==1);

        // Finally, delete temp dir
        deleteDirectoryRecursively(new File(gameFolderPath));
    }

    private void testFileExists (String gameFolderPath, String subPath){
        File file = new File(gameFolderPath, subPath);
        assertTrue( file.exists() && file.length()>0);
    }

    private void testFileDoesNotExist (String gameFolderPath, String subPath){
        File file = new File(gameFolderPath, subPath);
        assertFalse(file.exists());
    }

    private void deleteDirectoryRecursively(File directory){
        // Delete dir contents
        for (File child: directory.listFiles()){
            if (child.isDirectory()){
                deleteDirectoryRecursively(child);
            } else {
                try {
                    Files.delete(child.toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        // Remove the directory now that's empty.
        try {
            Files.delete(directory.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
