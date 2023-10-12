package work.lclpnet.ruler.impl;

import work.lclpnet.ruler.api.RuleManager;
import work.lclpnet.ruler.api.RulerApi;

public class RulerApiImpl implements RulerApi {

    private final RuleManagerImpl ruleManager;

    public RulerApiImpl() {
        ruleManager = new RuleManagerImpl();
    }

    @Override
    public RuleManager getRuleManager() {
        return ruleManager;
    }
}
