package work.lclpnet.ruler.rule;

import com.google.common.collect.ImmutableMap;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import work.lclpnet.ruler.rule.rules.BooleanRule;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import static work.lclpnet.ruler.Ruler.identifier;

public class Rules {

    protected static final Map<RuleKey<?>, RuleFactory<?>> RULE_TYPES;
    public static final RuleKey<BooleanRule> WATER_FREEZING;
    public static final RuleKey<BooleanRule> ICE_MELTING;

    static {
        RULE_TYPES = new HashMap<>();

        ICE_MELTING = register(identifier("ice_melting"), BooleanRule.create(true));
        WATER_FREEZING = register(identifier("water_freezing"), BooleanRule.create(true));
    }

    protected static <T extends Rule<?>> RuleKey<T> register(Identifier identifier, RuleFactory<T> factory) {
        var key = new RuleKey<T>(identifier);

        RULE_TYPES.put(key, factory);

        return key;
    }

    private final Map<RuleKey<?>, Rule<?>> rules;
    @Nullable
    private RuleChangeCallback callback;

    public Rules() {
        this(null);
    }

    public Rules(@Nullable RuleChangeCallback callback) {
        rules = RULE_TYPES.entrySet()
                .stream()
                .collect(ImmutableMap.<Map.Entry<RuleKey<?>, RuleFactory<?>>, RuleKey<?>, Rule<?>>toImmutableMap(
                        Map.Entry::getKey,
                        e -> {
                            RuleHandle handle = (oldValue, newValue) -> this.changed(e.getKey(), oldValue, newValue);

                            return e.getValue().create(handle);
                        }
                ));

        this.callback = callback;
    }

    @SuppressWarnings("unchecked")
    @NotNull
    public <T extends Rule<?>> T getRule(RuleKey<T> key) {
        Rule<?> rule = rules.get(key);

        if (rule == null) {
            throw new NullPointerException("Rule of type %s not registered".formatted(key.identifier()));
        }

        return (T) rule;
    }

    public <T> T get(RuleKey<? extends Rule<? extends T>> key) {
        return getRule(key).get();
    }

    public <T> void set(RuleKey<? extends Rule<T>> key, T value) {
        getRule(key).set(value);
    }

    public boolean getBoolean(RuleKey<BooleanRule> key) {
        BooleanRule rule = getRule(key);
        return rule.getBoolean();
    }

    public void set(RuleKey<BooleanRule> key, boolean value) {
        BooleanRule rule = getRule(key);
        rule.setBoolean(value);
    }

    public Set<RuleKey<?>> rules() {
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

    private void changed(RuleKey<?> rule, Object oldValue, Object newValue) {
        if (callback == null) return;

        callback.onChange(rule, oldValue, newValue);
    }

    public void whenChanged(RuleChangeCallback callback) {
        if (this.callback == null) {
            this.callback = callback;
            return;
        }

        // combine callbacks
        RuleChangeCallback oldCallback = this.callback;

        this.callback = (ruleKey, oldValue, newValue) -> {
            oldCallback.onChange(ruleKey, oldValue, newValue);
            callback.onChange(ruleKey, oldValue, newValue);
        };
    }

    public static void each(Consumer<RuleKey<? extends Rule<?>>> action) {
        RULE_TYPES.keySet().forEach(action);
    }
}
