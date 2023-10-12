package work.lclpnet.ruler.api;

import net.minecraft.server.world.ServerWorld;
import work.lclpnet.ruler.rule.Rules;

public interface RuleManager {

    Rules getRules(ServerWorld world);
}
