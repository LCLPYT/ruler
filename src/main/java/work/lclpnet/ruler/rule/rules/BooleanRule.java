package work.lclpnet.ruler.rule.rules;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.server.command.ServerCommandSource;
import work.lclpnet.ruler.rule.Rule;
import work.lclpnet.ruler.rule.RuleFactory;
import work.lclpnet.ruler.rule.RuleHandle;

public class BooleanRule implements Rule<Boolean> {

    private final RuleHandle args;
    private boolean value;

    public BooleanRule(RuleHandle args, boolean initialValue) {
        this.args = args;
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
    public String serialized() {
        return Boolean.toString(this.value);
    }

    @Override
    public void deserialize(String serialized) {
        this.value = Boolean.parseBoolean(serialized);
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
        args.onChange(old, this.value);
    }

    public static RuleFactory<BooleanRule> create(boolean value) {
        return new Factory(value);
    }

    private record Factory(boolean value) implements RuleFactory<BooleanRule> {

        @Override
        public BooleanRule create(RuleHandle args) {
            return new BooleanRule(args, value);
        }

        @Override
        public SuggestionProvider<ServerCommandSource> getSuggestions() {
            return (context, builder) -> builder.suggest("true").suggest("false").buildFuture();
        }
    }
}
