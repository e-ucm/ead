package es.eucm.ead.mockup.core.utils;

public class Pair<FIRST, SECOND> {

    private FIRST first;
    private SECOND second;

    public Pair(FIRST first, SECOND second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public int hashCode() {
        return 43 * hashcode(first) + hashcode(second);
    }

    private static int hashcode(Object o) {
        return o == null ? 0 : o.hashCode();
    }

    @Override
    public String toString() {
        return "(" + this.first + ", " + this.second + ')';
    }

	public FIRST getFirst() {
		return this.first;
	}

	public SECOND getSecond() {
		return this.second;
	}
}