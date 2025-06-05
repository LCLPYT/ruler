package work.lclpnet.ruler.rule.rules;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.serialization.Codec;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import work.lclpnet.ruler.rule.Rule;
import work.lclpnet.ruler.rule.RuleKey;
import work.lclpnet.ruler.rule.RuleHandle;

import java.util.function.Function;

public class BooleanRule implements Rule<Boolean> {

    private final RuleHandle<Boolean> handle;
    private boolean value;

    public BooleanRule(RuleHandle<Boolean> handle, boolean initialValue) {
        this.handle = handle;
        this.value = initialValue;
    }

    @Override
    public Boolean get() {
        return getBoolean();
    }

    @Override
    public void set(Boolean value) {
        setBoolean(value);
    }

    @Override
    public void changeFromInput(String input) {
        setBoolean(Boolean.parseBoolean(input));
    }

    public boolean getBoolean() {
        return value;
    }

    public void setBoolean(boolean value) {
        boolean old = this.value;
        this.value = value;
        handle.onChange(old, this.value);
    }

    public static Function<Identifier, RuleKey<Boolean, BooleanRule>> create(boolean defaultValue) {
        return id -> new BoolKey(id, defaultValue);
    }

    private record BoolKey(Identifier identifier, Boolean defaultValue) implements RuleKey<Boolean, BooleanRule> {

        @Override
        public BooleanRule createRule(Boolean initialValue, RuleHandle<Boolean> handle) {
            return new BooleanRule(handle, initialValue);
        }

        @Override
        public Codec<Boolean> valueCodec() {
            return Codec.withAlternative(Codec.BOOL, Codec.STRING.xmap(Boolean::parseBoolean, b -> Boolean.toString(b)));
        }

        @Override
        public SuggestionProvider<ServerCommandSource> getSuggestions() {
            return (context, builder) -> builder
                    .suggest("true")
                    .suggest("false")
                    .buildFuture();
        }
    }
}
