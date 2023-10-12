package work.lclpnet.ruler.rule.rules;

import work.lclpnet.ruler.rule.Rule;
import work.lclpnet.ruler.rule.RuleFactory;

public class BooleanRule implements Rule<Boolean> {

    private boolean value;

    public BooleanRule(boolean initialValue) {
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

    public boolean getBoolean() {
        return value;
    }

    public void setBoolean(boolean value) {
        this.value = value;
    }

    public static RuleFactory<BooleanRule> create(boolean value) {
        return () -> new BooleanRule(value);
    }
}
