package work.lclpnet.ruler.api;

import net.minecraft.server.level.ServerLevel;
import work.lclpnet.ruler.rule.Rules;

public interface RuleManager {

    Rules getRules(ServerLevel world);
}
