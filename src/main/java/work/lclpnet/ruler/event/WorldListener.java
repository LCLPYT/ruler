package work.lclpnet.ruler.event;

import net.minecraft.block.FarmlandBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import work.lclpnet.kibu.behaviour.world.ServerWorldBehaviour;
import work.lclpnet.kibu.hook.world.BlockModificationHooks;
import work.lclpnet.kibu.hook.world.FarmlandMoistureChangeCallback;
import work.lclpnet.kibu.hook.world.ServerWorldHooks;
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

        FarmlandMoistureChangeCallback.HOOK.register((serverWorld, blockPos, moisture)
                -> moisture < serverWorld.getBlockState(blockPos).get(FarmlandBlock.MOISTURE)
                && shouldCancel(serverWorld, Rules.FARMLAND_DRY_OUT));

        ServerWorldHooks.LOAD.register((server, world) -> {
            Rules rules = ruleManager.getRules(world);

            rules.whenChanged(Rules.FLUID_FLOW, (oldValue, newValue)
                    -> ServerWorldBehaviour.setFluidTicksEnabled(world, newValue));

            ServerWorldBehaviour.setFluidTicksEnabled(world, rules.getBoolean(Rules.FLUID_FLOW));
        });
    }

    private boolean shouldCancel(World world, RuleKey<Boolean, BooleanRule> rule) {
        return world instanceof ServerWorld serverWorld && !ruleManager.getRules(serverWorld).getBoolean(rule);
    }
}
