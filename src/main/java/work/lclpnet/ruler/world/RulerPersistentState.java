package work.lclpnet.ruler.world;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import work.lclpnet.ruler.RulerConstants;
import work.lclpnet.ruler.rule.Rules;

public class RulerPersistentState extends PersistentState {

    private final Rules rules;

    public RulerPersistentState() {
        this.rules = new Rules();
    }

    public RulerPersistentState(Rules rules) {
        this.rules = rules;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.put(RulerConstants.RULES_KEY, rules.toNbt());
        return nbt;
    }

    public Rules getRules() {
        return rules;
    }

    public static RulerPersistentState fromNbt(NbtCompound tag) {
        Rules rules = new Rules();
        rules.load(tag.getCompound(RulerConstants.RULES_KEY));

        return new RulerPersistentState(rules);
    }

    public static RulerPersistentState get(ServerWorld world) {
        PersistentStateManager manager = world.getPersistentStateManager();
        return manager.getOrCreate(RulerPersistentState::fromNbt, RulerPersistentState::new, "ruler");
    }
}
