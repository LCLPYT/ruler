package work.lclpnet.ruler.rule;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import work.lclpnet.ruler.Ruler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Rules {

    private static final Registry<RuleKey<?, ?>> RULE_REGISTRY = new SimpleRegistry<>(
            RegistryKey.ofRegistry(Ruler.identifier("rules")),
            Lifecycle.stable()
    );

    private static final Codec<RuleKey<?, ?>> RULE_CODEC = RULE_REGISTRY.getCodec();
    private static final Codec<Map<RuleKey<?, ?>, RuleKey.Value<?>>> MAP_CODEC = Codec.dispatchedMap(RULE_CODEC, RuleKey::valueWrapperCodec);
    public static final Codec<Rules> CODEC = MAP_CODEC.xmap(Rules::new, Rules::entries);

    protected static <V, R extends Rule<V>> RuleKey<V, R> register(Identifier identifier, Function<Identifier, RuleKey<V, R>> factory) {
        RuleKey<V, R> key = factory.apply(identifier);

        return Registry.register(RULE_REGISTRY, identifier, key);
    }

    public static Map<RuleKey<?, ?>, RuleKey.Value<?>> defaultRules() {
        return RULE_REGISTRY.stream()
                .collect(Collectors.toUnmodifiableMap(Function.identity(), RuleKey::wrappedDefaultValue));
    }

    private final Map<RuleKey<?, ?>, Rule<?>> rules;
    private final Map<RuleKey<?, ?>, RuleChangeCallback<?>> callbacks = new HashMap<>();
    @Nullable
    private GlobalRuleChangeCallback globalCallback;

    public Rules() {
        this(Map.of(), null);
    }

    public Rules(Map<RuleKey<?, ?>, RuleKey.Value<?>> ruleOverrides) {
        this(ruleOverrides, null);
    }

    public Rules(@Nullable GlobalRuleChangeCallback globalCallback) {
        this(Map.of(), globalCallback);
    }

    public Rules(Map<RuleKey<?, ?>, RuleKey.Value<?>> ruleOverrides, @Nullable GlobalRuleChangeCallback globalCallback) {
        var rules = new HashMap<>(defaultRules());

        rules.putAll(ruleOverrides);

        var builder = ImmutableMap.<RuleKey<?, ?>, Rule<?>>builder();

        for (var _entry : rules.entrySet()) {
            var entry = RuleValue.makeUnsafe(_entry.getKey(), _entry.getValue());
            var rule = entry.createRule((oldValue, newValue) -> this.changed(entry.factory, oldValue, newValue));

            builder.put(entry.factory, rule);
        }

        this.rules = builder.build();
        this.globalCallback = globalCallback;
    }

    @SuppressWarnings("unchecked")
    @NotNull
    public <V, R extends Rule<V>> R getRule(RuleKey<V, R> key) {
        R rule = (R) rules.get(key);

        if (rule == null) {
            throw new NullPointerException("Rule of type %s not registered".formatted(key.identifier()));
        }

        return rule;
    }

    public <V, R extends Rule<V>> V get(RuleKey<V, R> key) {
        return getRule(key).get();
    }

    public <V, R extends Rule<V>> void set(RuleKey<V, R> key, V value) {
        getRule(key).set(value);
    }

    public Set<RuleKey<?, ?>> rules() {
        return Collections.unmodifiableSet(rules.keySet());
    }

    private Map<RuleKey<?, ?>, RuleKey.Value<?>> entries() {
        return rules.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> RuleValue.makeUnsafe(e.getKey(), e.getValue()).wrapValue()
        ));
    }

    @SuppressWarnings("unchecked")
    private <V, T extends Rule<V>> void changed(RuleKey<V, T> rule, Object oldValue, Object newValue) {
        if (globalCallback != null) {
            globalCallback.onChange(rule, oldValue, newValue);
        }

        var callback = callbacks.getOrDefault(rule, null);

        if (callback != null) {
            ((RuleChangeCallback<V>) callback).onChange((V) oldValue, (V) newValue);
        }
    }

    public void whenChanged(GlobalRuleChangeCallback globalCallback) {
        if (this.globalCallback == null) {
            this.globalCallback = globalCallback;
            return;
        }

        // combine callbacks
        GlobalRuleChangeCallback oldCallback = this.globalCallback;

        this.globalCallback = (ruleKey, oldValue, newValue) -> {
            oldCallback.onChange(ruleKey, oldValue, newValue);
            globalCallback.onChange(ruleKey, oldValue, newValue);
        };
    }

    @SuppressWarnings("unchecked")
    public <V, R extends Rule<V>> void whenChanged(RuleKey<V, R> rule, RuleChangeCallback<V> callback) {
        callbacks.compute(rule, (ruleKey, oldCallback) -> {
            if (oldCallback == null) {
                return callback;
            }

            return (RuleChangeCallback<V>) (oldValue, newValue) -> {
                ((RuleChangeCallback<V>) oldCallback).onChange(oldValue, newValue);
                callback.onChange(oldValue, newValue);
            };
        });
    }

    public static void each(Consumer<RuleKey<?, ?>> action) {
        RULE_REGISTRY.stream().forEach(action);
    }

    public interface RuleChangeCallback<V> {
        void onChange(V oldValue, V newValue);
    }

    public interface GlobalRuleChangeCallback {
        void onChange(RuleKey<?, ?> ruleKey, Object oldValue, Object newValue);
    }

    private record RuleValue<V, R extends Rule<V>>(RuleKey<V, R> factory, V value) {

        @SuppressWarnings("unchecked")
        public static <V, R extends Rule<V>> RuleValue<V, R> makeUnsafe(RuleKey<V, R> factory, RuleKey.Value<?> wrapper) {
            return new RuleValue<>(factory, (V) wrapper.value());
        }

        @SuppressWarnings("unchecked")
        public static <V, R extends Rule<V>> RuleValue<V, R> makeUnsafe(RuleKey<V, R> factory, Rule<?> rule) {
            return new RuleValue<>(factory, (V) rule.get());
        }

        public RuleKey.Value<V> wrapValue() {
            return new RuleKey.Value<>(value, factory.valueCodec());
        }

        public R createRule(RuleHandle<Object> handle) {
            return factory.createRule(value, RuleHandle.cast(factory.castHandle(handle)));
        }
    }
}
