package work.lclpnet.ruler.event;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import work.lclpnet.kibu.hook.world.BlockModificationHooks;
import work.lclpnet.kibu.hook.world.WorldPhysicsHooks;
import work.lclpnet.ruler.api.RuleManager;
import work.lclpnet.ruler.rule.RuleKey;
import work.lclpnet.ruler.rule.Rules;
import work.lclpnet.ruler.rule.rules.BooleanRule;

public class WorldListener {

    private final RuleManager ruleManager;

    public WorldListener(RuleManager ruleManager) {
        this.ruleManager = ruleManager;
    }

    public void register() {
        WorldPhysicsHooks.MELT.register((world, pos) -> shouldCancel(world, Rules.ICE_MELTING));
        WorldPhysicsHooks.FREEZE.register((world, pos) -> shouldCancel(world, Rules.WATER_FREEZING));
        WorldPhysicsHooks.CORAL_DEATH.register((world, pos) -> shouldCancel(world, Rules.CORAL_DEATH));
        BlockModificationHooks.TRAMPLE_FARMLAND.register((world, pos, entity) -> shouldCancel(world, Rules.FARMLAND_TRAMPLING));
        BlockModificationHooks.TRAMPLE_TURTLE_EGG.register((world, pos, entity) -> shouldCancel(world, Rules.TURTLE_EGG_TRAMPLING));
    }

    private boolean shouldCancel(World world, RuleKey<BooleanRule> rule) {
        return world instanceof ServerWorld serverWorld && !ruleManager.getRules(serverWorld).getBoolean(rule);
    }
}
