package work.lclpnet.ruler.rule;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import work.lclpnet.ruler.rule.rules.BooleanRule;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class RulesTest {

    private static final RuleKey<TestRule> TEST_RULE = Rules.register(new Identifier("ruler", "test"),
            TestRule::new);

    @Test
    void rules_default_equals() {
        Rules rules = new Rules();
        var keys = rules.rules();
        assertEquals(Rules.RULE_TYPES.keySet(), keys);
    }

    @Test
    void getRule_existing_succeeds() {
        Rules rules = new Rules();
        rules.getRule(Rules.ICE_MELTING);
    }

    @Test
    void getRule_missing_throws() {
        Rules rules = new Rules();
        var key = new RuleKey<>(new Identifier("test", "test"));

        String msg = "Rule of type %s not registered".formatted(key.identifier());
        assertThrows(NullPointerException.class, () -> rules.getRule(key), msg);
    }

    @ParameterizedTest
    @MethodSource("keys")
    <T, R extends Rule<T>> void get_default_defaultValue(RuleKey<R> key) {
        var rules = new Rules();
        T actual = rules.get(key);

        RuleHandle handle = (oldValue, newValue) -> {};
        var expected = Rules.RULE_TYPES.get(key).create(handle).get();
        assertEquals(expected, actual);
    }

    @Test
    void set_any_updated() {
        var rules = new Rules();

        rules.set(TEST_RULE, "hello world!");

        assertEquals("hello world!", rules.get(TEST_RULE));
    }

    @Test
    void set_boolean_updated() {
        var rules = new Rules();

        @SuppressWarnings("unchecked")
        var ruleFactory = (RuleFactory<BooleanRule>) Rules.RULE_TYPES.get(Rules.ICE_MELTING);
        RuleHandle handle = (oldValue, newValue) -> {};

        boolean expected = !ruleFactory.create(handle).getBoolean();

        rules.set(Rules.ICE_MELTING, expected);

        assertEquals(expected, rules.get(Rules.ICE_MELTING));
    }

    @Test
    void getBoolean_default_defaultValue() {
        var rules = new Rules();

        assertTrue(rules.getBoolean(Rules.ICE_MELTING));
    }

    @Test
    void toNbt_default_stringPairs() {
        var rules = new Rules();
        NbtCompound nbt = rules.toNbt();

        for (String key : nbt.getKeys()) {
            assertTrue(nbt.contains(key, NbtElement.STRING_TYPE), "Non string value");
        }
    }

    @Test
    void toNbt_default_allKeys() {
        var rules = new Rules();
        NbtCompound nbt = rules.toNbt();

        var expected = Rules.RULE_TYPES.keySet().stream()
                .map(RuleKey::identifier)
                .map(Identifier::toString)
                .collect(Collectors.toSet());

        assertEquals(expected, nbt.getKeys());
    }

    @Test
    void load_nbt_succeeds() {
        final var rule = Rules.ICE_MELTING;

        NbtCompound nbt = new NbtCompound();

        RuleHandle handle = (oldValue, newValue) -> {};

        @SuppressWarnings("unchecked")
        boolean def = ((RuleFactory<BooleanRule>) Rules.RULE_TYPES.get(rule)).create(handle).getBoolean();

        nbt.putString(rule.identifier().toString(), Boolean.toString(!def));

        var rules = new Rules();
        rules.load(nbt);

        assertEquals(!def, rules.get(rule));
    }

    @Test
    void callback_given_isCalled() {
        final var rule = Rules.ICE_MELTING;

        AtomicBoolean called = new AtomicBoolean(false);

        var rules = new Rules((ruleKey, oldValue, newValue) -> {
            assertEquals(rule, ruleKey);
            assertEquals(true, oldValue);
            assertEquals(false, newValue);

            called.set(true);
        });

        rules.set(rule, false);

        assertTrue(called.get());
    }

    @Test
    void whenChanged_none_replaced() {
        var rules = new Rules();

        AtomicBoolean called = new AtomicBoolean();

        rules.whenChanged((ruleKey, oldValue, newValue) -> called.set(true));

        rules.set(Rules.ICE_MELTING, true);

        assertTrue(called.get());
    }

    @Test
    void whenChanged_callbackPresent_combined() {
        AtomicBoolean called1 = new AtomicBoolean();
        AtomicBoolean called2 = new AtomicBoolean();

        var rules = new Rules((ruleKey, oldValue, newValue) -> called1.set(true));

        rules.whenChanged((ruleKey, oldValue, newValue) -> called2.set(true));

        rules.set(Rules.ICE_MELTING, true);

        assertTrue(called1.get());
        assertTrue(called2.get());
    }

    private static Stream<RuleKey<?>> keys() {
        return Rules.RULE_TYPES.keySet().stream();
    }

    private static class TestRule implements Rule<String> {

        private final RuleHandle handle;
        private String value = "test";

        public TestRule(RuleHandle handle) {
            this.handle = handle;
        }

        @Override
        public String get() {
            return value;
        }

        @Override
        public void set(String value) {
            String old = this.value;
            this.value = value;
            handle.onChange(old, this.value);
        }

        @Override
        public String serialized() {
            return value;
        }

        @Override
        public void deserialize(String serialized) {
            this.value = serialized;
        }

        @Override
        public void changeFromInput(String input) {
            set(input);
        }
    }
}