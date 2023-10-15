package work.lclpnet.ruler.event;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import work.lclpnet.kibu.hook.world.WorldPhysicsHooks;
import work.lclpnet.ruler.Ruler;
import work.lclpnet.ruler.rule.Rules;

public class WorldListener {

    public static void register() {
        WorldPhysicsHooks.MELT.register(WorldListener::onMelt);
        WorldPhysicsHooks.FREEZE.register(WorldListener::onFreeze);
        WorldPhysicsHooks.CORAL_DEATH.register(WorldListener::onCoralDeath);
    }

    private static boolean onFreeze(World world, BlockPos blockPos) {
        if (!(world instanceof ServerWorld serverWorld)) return false;

        Rules rules = Ruler.getApi().getRuleManager().getRules(serverWorld);

        return !rules.getBoolean(Rules.WATER_FREEZING);
    }

    private static boolean onMelt(World world, BlockPos pos) {
        if (!(world instanceof ServerWorld serverWorld)) return false;

        Rules rules = Ruler.getApi().getRuleManager().getRules(serverWorld);

        return !rules.getBoolean(Rules.ICE_MELTING);
    }

    private static boolean onCoralDeath(World world, BlockPos blockPos) {
        if (!(world instanceof ServerWorld serverWorld)) return false;

        Rules rules = Ruler.getApi().getRuleManager().getRules(serverWorld);

        return !rules.getBoolean(Rules.CORAL_DEATH);
    }
}
