package work.lclpnet.ruler.rule;

import com.mojang.serialization.Codec;
import net.minecraft.server.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class RulesTest {

    private static final RuleKey<String, TestStringRule> TEST_RULE = Rules.register(
            ResourceLocation.fromNamespaceAndPath("ruler", "test"),
            TestStringRule::create
    );

    @BeforeAll
    public static void setupAll() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        BuiltinRules.initialize();
    }

    @Test
    void rules_default_equals() {
        Rules rules = new Rules();
        var keys = rules.rules();
        assertEquals(Rules.defaultRules().keySet(), keys);
    }

    @Test
    void getRule_existing_succeeds() {
        Rules rules = new Rules();
        rules.getRule(BuiltinRules.ICE_MELTING);
    }

    @Test
    void getRule_missing_throws() {
        Rules rules = new Rules();
        RuleKey<?, ?> key = new StringKey(ResourceLocation.parse("foo"), "bar");

        String msg = "Rule of type %s not registered".formatted(key.identifier());
        assertThrows(NullPointerException.class, () -> rules.getRule(key), msg);
    }

    @ParameterizedTest
    @MethodSource("keys")
    <V, R extends Rule<V>> void get_default_defaultValue(RuleKey<V, R> key) {
        var rules = new Rules();
        V actual = rules.get(key);
        V expected = key.defaultValue();

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
        var rule = BuiltinRules.ICE_MELTING;
        boolean expected = !rule.defaultValue();

        rules.set(rule, expected);

        assertEquals(expected, rules.get(rule));
    }

    @Test
    void callback_given_isCalled() {
        final var rule = BuiltinRules.ICE_MELTING;

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

        rules.set(BuiltinRules.ICE_MELTING, true);

        assertTrue(called.get());
    }

    @Test
    void whenChanged_callbackPresent_combined() {
        AtomicBoolean called1 = new AtomicBoolean();
        AtomicBoolean called2 = new AtomicBoolean();

        var rules = new Rules((ruleKey, oldValue, newValue) -> called1.set(true));

        rules.whenChanged((ruleKey, oldValue, newValue) -> called2.set(true));

        rules.set(BuiltinRules.ICE_MELTING, true);

        assertTrue(called1.get());
        assertTrue(called2.get());
    }

    private static Stream<RuleKey<?, ?>> keys() {
        return Rules.defaultRules().keySet().stream();
    }

    private static class TestStringRule implements Rule<String> {

        private String value;
        private final RuleHandle<String> handle;

        public TestStringRule(String initialValue, RuleHandle<String> handle) {
            this.value = initialValue;
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
        public void changeFromInput(String input) {
            set(input);
        }

        public static RuleKey<String, TestStringRule> create(ResourceLocation id) {
            return new StringKey(id, "test");
        }
    }

    private record StringKey(ResourceLocation identifier, String defaultValue) implements RuleKey<String, TestStringRule> {

        @Override
        public String defaultValue() {
            return defaultValue;
        }

        @Override
        public TestStringRule createRule(String initialValue, RuleHandle<String> args) {
            return new TestStringRule(initialValue, args);
        }

        @Override
        public Codec<String> valueCodec() {
            return Codec.STRING;
        }
    }
}