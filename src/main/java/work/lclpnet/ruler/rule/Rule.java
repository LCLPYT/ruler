package work.lclpnet.ruler.rule;

public interface Rule<T> {

    T get();

    void set(T value);
}
