package es.eucm.ead.editor.actions;

import com.badlogic.gdx.scenes.scene2d.Group;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.platform.MockPlatform;
import es.eucm.ead.engine.mock.MockApplication;
import es.eucm.ead.engine.mock.MockFiles;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * This abstract class is meant to be the "parent" test for all editor tests. It initializes a basic platform, controller and model that are made available to any test extending this class. EditorTest also deals with disposal of any resources initialized by platform.
 * Created by Javier Torrente on 5/03/14.
 */
public abstract class EditorTest {

    /**
     * Mock editor controller. For testing. It is initialized statically before the class is actually loaded, so it is available for any test method implemented in the child test class.
     */
    protected static Controller mockController;

    /**
     * Mock platform. For testing. It is initialized statically before the class is actually loaded, so it is available for any test method implemented in the child test class.
     */
    protected static MockPlatform mockPlatform;

    /**
     * Mock model. For testing. It is initialized statically before the class is actually loaded, so it is available for any test method implemented in the child test class.
     */
    protected static Model mockModel;

    @BeforeClass
    public static void setUpClass() {
        MockApplication.initStatics();
        mockPlatform = new MockPlatform();
        mockController = new Controller(mockPlatform, new MockFiles(), new Group());
        mockModel = mockController.getModel();
    }

    @AfterClass
    public static void tearDownClass() {
        mockPlatform.removeTempFiles();
    }
}
