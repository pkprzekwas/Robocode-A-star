/**
 * Created by pprzekwa on 2016-05-26.
 */
public enum State {
    MAP_RECOGNITION("Map recognition..."),
    ANALYZE("Analysing..."),
    END("END");

    private final String text;

    private State(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
