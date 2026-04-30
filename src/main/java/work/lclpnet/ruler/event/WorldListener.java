package work.lclpnet.ruler.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FarmlandBlock;
import work.lclpnet.kibu.behaviour.level.ServerLevelBehaviour;
import work.lclpnet.kibu.hook.level.BlockModificationHooks;
import work.lclpnet.kibu.hook.level.FarmlandMoistureChangeCallback;
import work.lclpnet.kibu.hook.level.LevelPhysicsHooks;
import work.lclpnet.kibu.hook.level.ServerLevelHooks;
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
        LevelPhysicsHooks.MELT.register((world, pos) -> shouldCancel(world, BuiltinRules.ICE_MELTING));
        LevelPhysicsHooks.FREEZE.register((world, pos) -> shouldCancel(world, BuiltinRules.WATER_FREEZING));
        LevelPhysicsHooks.CORAL_DEATH.register((world, pos) -> shouldCancel(world, BuiltinRules.CORAL_DEATH));
        BlockModificationHooks.TRAMPLE_FARMLAND.register((world, pos, entity) -> shouldCancel(world, BuiltinRules.FARMLAND_TRAMPLING));
        BlockModificationHooks.TRAMPLE_TURTLE_EGG.register((world, pos, entity) -> shouldCancel(world, BuiltinRules.TURTLE_EGG_TRAMPLING));

        FarmlandMoistureChangeCallback.HOOK.register((serverWorld, blockPos, moisture)
                -> moisture < serverWorld.getBlockState(blockPos).getValue(FarmlandBlock.MOISTURE)
                && shouldCancel(serverWorld, BuiltinRules.FARMLAND_DRY_OUT));

        ServerLevelHooks.LOAD.register((server, world) -> {
            Rules rules = ruleManager.getRules(world);

            rules.whenChanged(BuiltinRules.FLUID_FLOW, (oldValue, newValue)
                    -> ServerLevelBehaviour.setFluidTicksEnabled(world, newValue));

            ServerLevelBehaviour.setFluidTicksEnabled(world, rules.get(BuiltinRules.FLUID_FLOW));
        });
    }

    private boolean shouldCancel(Level world, RuleKey<Boolean, ?> rule) {
        return world instanceof ServerLevel serverWorld && !ruleManager.getRules(serverWorld).get(rule);
    }
}
