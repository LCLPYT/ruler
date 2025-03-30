package work.lclpnet.ruler.rule;

import net.minecraft.util.Identifier;

public record RuleKey<V, T extends Rule<V>>(Identifier identifier) {

    @SuppressWarnings("unchecked")
    public RuleHandle<V> cast(RuleHandle<Object> handle) {
        return (RuleHandle<V>) handle;
    }
}
