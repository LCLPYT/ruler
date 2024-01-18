package work.lclpnet.ruler.world;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import work.lclpnet.ruler.RulerConstants;
import work.lclpnet.ruler.rule.Rules;

public class RulerPersistentState extends PersistentState {

    private static final Type<RulerPersistentState> TYPE = new Type<>(RulerPersistentState::new,
            RulerPersistentState::fromNbt, null);

    private final Rules rules;

    public RulerPersistentState() {
        this.rules = new Rules((ruleKey, oldValue, newValue) -> markDirty());
    }

    public RulerPersistentState(Rules rules) {
        this.rules = rules;
        this.rules.whenChanged((ruleKey, oldValue, newValue) -> markDirty());
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

        RulerPersistentState state = manager.getOrCreate(TYPE, "ruler");

        // write initial data to the disk
        state.markDirty();

        return state;
    }
}
