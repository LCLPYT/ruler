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
import work.lclpnet.ruler.rule.BuiltinRules;
import work.lclpnet.ruler.rule.RuleKey;
import work.lclpnet.ruler.rule.Rules;

public class WorldListener {

    private final RuleManager ruleManager;

    public WorldListener(RuleManager ruleManager) {
        this.ruleManager = ruleManager;
    }

    public void register() {
        WorldPhysicsHooks.MELT.register((world, pos) -> shouldCancel(world, BuiltinRules.ICE_MELTING));
        WorldPhysicsHooks.FREEZE.register((world, pos) -> shouldCancel(world, BuiltinRules.WATER_FREEZING));
        WorldPhysicsHooks.CORAL_DEATH.register((world, pos) -> shouldCancel(world, BuiltinRules.CORAL_DEATH));
        BlockModificationHooks.TRAMPLE_FARMLAND.register((world, pos, entity) -> shouldCancel(world, BuiltinRules.FARMLAND_TRAMPLING));
        BlockModificationHooks.TRAMPLE_TURTLE_EGG.register((world, pos, entity) -> shouldCancel(world, BuiltinRules.TURTLE_EGG_TRAMPLING));

        FarmlandMoistureChangeCallback.HOOK.register((serverWorld, blockPos, moisture)
                -> moisture < serverWorld.getBlockState(blockPos).get(FarmlandBlock.MOISTURE)
                && shouldCancel(serverWorld, BuiltinRules.FARMLAND_DRY_OUT));

        ServerWorldHooks.LOAD.register((server, world) -> {
            Rules rules = ruleManager.getRules(world);

            rules.whenChanged(BuiltinRules.FLUID_FLOW, (oldValue, newValue)
                    -> ServerWorldBehaviour.setFluidTicksEnabled(world, newValue));

            ServerWorldBehaviour.setFluidTicksEnabled(world, rules.get(BuiltinRules.FLUID_FLOW));
        });
    }

    private boolean shouldCancel(World world, RuleKey<Boolean, ?> rule) {
        return world instanceof ServerWorld serverWorld && !ruleManager.getRules(serverWorld).get(rule);
    }
}
