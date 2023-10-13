package work.lclpnet.ruler.rule;

public interface RuleFactory<T extends Rule<?>> {

    T create(RuleHandle args);
}
