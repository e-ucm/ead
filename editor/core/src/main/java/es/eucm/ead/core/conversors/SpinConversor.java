package es.eucm.ead.core.conversors;

import es.eucm.ead.core.EAdEngine;
import es.eucm.ead.schema.actions.Spin;
import es.eucm.ead.schema.actions.Transform;
import es.eucm.ead.schema.components.Transformation;

public class SpinConversor implements Conversor<Spin> {
	@Override
	public Object convert(Spin s) {
		Transform t = EAdEngine.factory.newInstance(Transform.class);
		t.setRelative(true);
		t.setDuration(s.getDuration());
		Transformation tr = EAdEngine.factory.newInstance(Transformation.class);
		tr.setScaleY(0);
		tr.setScaleX(0);
		tr.setRotation(s.getSpins() * 360);
		t.setLoop(true);
		t.setTransformation(tr);
		return t;
	}
}
