package es.eucm.ead.android.mockup;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.scenes.scene2d.Group;

import es.eucm.ead.android.mockup.platform.DevicePictureControl;
import es.eucm.ead.android.mockup.platform.DeviceVideoControl;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.platform.Platform;

public class MockupController extends Controller {

	private DeviceVideoControl videoControl;

	private DevicePictureControl pictureControl;

	public MockupController(Platform platform,
			DevicePictureControl pictureControl,
			DeviceVideoControl videoControl, Files files, Group rootView) {
		super(platform, files, rootView);
		this.videoControl = videoControl;
		this.pictureControl = pictureControl;
	}

	public DeviceVideoControl getVideoControl() {
		return this.videoControl;
	}

	public DevicePictureControl getPictureControl() {
		return this.pictureControl;
	}
}
