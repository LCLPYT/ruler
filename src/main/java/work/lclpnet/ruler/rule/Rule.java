package work.lclpnet.ruler.rule;

public interface Rule<T> {

    T get();

    void set(T value);

    String serialized();

    void deserialize(String serialized);

    void changeFromInput(String input);
}
