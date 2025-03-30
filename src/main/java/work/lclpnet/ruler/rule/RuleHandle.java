package work.lclpnet.ruler.rule;

public interface RuleHandle<V> {

    void onChange(V oldValue, V newValue);

    @SuppressWarnings("unchecked")
    static <V> RuleHandle<V> cast(RuleHandle<?> handle) {
        return (RuleHandle<V>) handle;
    }
}
