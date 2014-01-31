package es.eucm.ead.engine.mock.engine;

import es.eucm.ead.engine.Assets;
import es.eucm.ead.engine.Factory;
import es.eucm.ead.engine.mock.engineobjects.MockEmptyAction;
import es.eucm.ead.engine.mock.schema.MockEmpty;

public class MockFactory extends Factory {

	public MockFactory(Assets assets) {
		super(assets);
		bind("mockempty", MockEmpty.class, MockEmptyAction.class);
	}
}
