package work.lclpnet.ruler.impl;

import net.minecraft.server.level.ServerLevel;
import work.lclpnet.ruler.api.RuleManager;
import work.lclpnet.ruler.rule.Rules;
import work.lclpnet.ruler.world.RulerPersistentState;

public class RuleManagerImpl implements RuleManager {

    @Override
    public Rules getRules(ServerLevel world) {
        RulerPersistentState state = RulerPersistentState.get(world);

        return state.getRules();
    }
}
