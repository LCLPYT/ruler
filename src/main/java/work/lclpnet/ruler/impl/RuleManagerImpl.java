package work.lclpnet.ruler.impl;

import net.minecraft.server.world.ServerWorld;
import work.lclpnet.ruler.api.RuleManager;
import work.lclpnet.ruler.rule.Rules;
import work.lclpnet.ruler.world.RulerPersistentState;

public class RuleManagerImpl implements RuleManager {

    @Override
    public Rules getRules(ServerWorld world) {
        RulerPersistentState state = RulerPersistentState.get(world);

        return state.getRules();
    }
}
