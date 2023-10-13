package work.lclpnet.ruler.rule;

public interface RuleChangeCallback {

    void onChange(RuleKey<?> ruleKey, Object oldValue, Object newValue);
}
