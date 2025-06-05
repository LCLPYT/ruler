package work.lclpnet.ruler.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.PersistentStateType;
import work.lclpnet.ruler.RulerConstants;
import work.lclpnet.ruler.RulerInit;
import work.lclpnet.ruler.api.RulerApi;
import work.lclpnet.ruler.rule.Rules;

public class RulerPersistentState extends PersistentState {

    private static final Codec<RulerPersistentState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Rules.CODEC.fieldOf(RulerConstants.RULES_KEY).forGetter(RulerPersistentState::getRules)
    ).apply(instance, RulerPersistentState::new));

    private static final PersistentStateType<RulerPersistentState> TYPE = new PersistentStateType<>(
            RulerConstants.MOD_ID, RulerPersistentState::new, CODEC, null);

    private final Rules rules;

    public RulerPersistentState() {
        this.rules = new Rules((ruleKey, oldValue, newValue) -> markDirty());
    }

    public RulerPersistentState(Rules rules) {
        this.rules = rules;
        this.rules.whenChanged((ruleKey, oldValue, newValue) -> markDirty());
    }

    public Rules getRules() {
        return rules;
    }

    public static RulerPersistentState get(ServerWorld world) {
        RulerPersistentState state = world.getPersistentStateManager().getOrCreate(TYPE);

        // write initial data to the disk
        state.markDirty();

        return state;
    }
}
