package work.lclpnet.ruler.rule;

import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import work.lclpnet.ruler.rule.rules.BooleanRule;

import java.util.*;
import java.util.function.Consumer;

import static work.lclpnet.ruler.Ruler.identifier;

public class Rules {

    protected static final Map<RuleKey<?, ?>, RuleFactory<?, ?>> RULE_TYPES = new HashMap<>();

    public static final RuleKey<Boolean, BooleanRule>
            WATER_FREEZING = register(identifier("water_freezing"), BooleanRule.create(true)),
            ICE_MELTING = register(identifier("ice_melting"), BooleanRule.create(true)),
            CORAL_DEATH = register(identifier("coral_death"), BooleanRule.create(true)),
            FARMLAND_TRAMPLING = register(identifier("farmland_trampling"), BooleanRule.create(true)),
            TURTLE_EGG_TRAMPLING = register(identifier("turtle_egg_trampling"), BooleanRule.create(true)),
            FARMLAND_DRY_OUT = register(identifier("farmland_dry_out"), BooleanRule.create(true)),
            FLUID_FLOW = register(identifier("fluid_flow"), BooleanRule.create(true));

    protected static <V, T extends Rule<V>> RuleKey<V, T> register(Identifier identifier, RuleFactory<V, T> factory) {
        var key = new RuleKey<V, T>(identifier);

        RULE_TYPES.put(key, factory);

        return key;
    }

    private final Map<RuleKey<?, ?>, Rule<?>> rules;
    private final Map<RuleKey<?, ?>, RuleChangeCallback<?>> callbacks = new HashMap<>();
    @Nullable
    private GlobalRuleChangeCallback globalCallback;

    public Rules() {
        this(null);
    }

    public Rules(@Nullable GlobalRuleChangeCallback globalCallback) {
        rules = RULE_TYPES.entrySet()
                .stream()
                .collect(ImmutableMap.<Map.Entry<RuleKey<?, ?>, RuleFactory<?, ?>>, RuleKey<?, ?>, Rule<?>>toImmutableMap(
                        Map.Entry::getKey,
                        e -> {
                            var handle = e.getKey().cast((oldValue, newValue) -> this.changed(e.getKey(), oldValue, newValue));

                            return e.getValue().create(RuleHandle.cast(handle));
                        }
                ));

        this.globalCallback = globalCallback;
    }

    @SuppressWarnings("unchecked")
    @NotNull
    public <V, T extends Rule<V>> T getRule(RuleKey<V, T> key) {
        Rule<?> rule = rules.get(key);

        if (rule == null) {
            throw new NullPointerException("Rule of type %s not registered".formatted(key.identifier()));
        }

        return (T) rule;
    }

    public <V, T extends Rule<V>> V get(RuleKey<V, T> key) {
        return getRule(key).get();
    }

    public <T> void set(RuleKey<T, ? extends Rule<T>> key, T value) {
        getRule(key).set(value);
    }

    public boolean getBoolean(RuleKey<Boolean, BooleanRule> key) {
        BooleanRule rule = getRule(key);
        return rule.getBoolean();
    }

    public void set(RuleKey<Boolean, BooleanRule> key, boolean value) {
        BooleanRule rule = getRule(key);
        rule.setBoolean(value);
    }

    public Set<RuleKey<?, ?>> rules() {
        return Collections.unmodifiableSet(rules.keySet());
    }

    public void load(NbtCompound nbt) {
        rules.forEach((key, rule) -> {
            String id = key.identifier().toString();

            if (!nbt.contains(id, NbtElement.STRING_TYPE)) return;

            String value = nbt.getString(id);

            rule.deserialize(value);
        });
    }

    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();

        rules.forEach((key, rule) -> nbt.putString(key.identifier().toString(), rule.serialized()));

        return nbt;
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

    public <T extends Rule<?>> void whenChanged(GlobalRuleChangeCallback globalCallback) {
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
    public <V, T extends Rule<V>> void whenChanged(RuleKey<V, T> rule, RuleChangeCallback<V> callback) {
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

    public static void each(Consumer<RuleKey<?, ? extends Rule<?>>> action) {
        RULE_TYPES.keySet().forEach(action);
    }

    @Nullable
    public static SuggestionProvider<ServerCommandSource> suggestions(RuleKey<?, ? extends Rule<?>> rule) {
        return Objects.requireNonNull(RULE_TYPES.get(rule), () -> "Unknown rule %s".formatted(rule.identifier()))
                .getSuggestions();
    }

    public interface RuleChangeCallback<V> {
        void onChange(V oldValue, V newValue);
    }

    public interface GlobalRuleChangeCallback {
        void onChange(RuleKey<?, ?> ruleKey, Object oldValue, Object newValue);
    }
}
