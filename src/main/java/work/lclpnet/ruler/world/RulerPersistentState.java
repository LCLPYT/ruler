package work.lclpnet.ruler.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import work.lclpnet.ruler.RulerConstants;
import work.lclpnet.ruler.rule.Rules;

public class RulerPersistentState extends SavedData {

    private static final Codec<RulerPersistentState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Rules.CODEC.fieldOf(RulerConstants.RULES_KEY).forGetter(RulerPersistentState::getRules)
    ).apply(instance, RulerPersistentState::new));

    private static final SavedDataType<RulerPersistentState> TYPE = new SavedDataType<>(
            RulerConstants.id("rules"), RulerPersistentState::new, CODEC, null);

    private final Rules rules;

    public RulerPersistentState() {
        this.rules = new Rules((_, _, _) -> setDirty());
    }

    public RulerPersistentState(Rules rules) {
        this.rules = rules;
        this.rules.whenChanged((_, _, _) -> setDirty());
    }

    public Rules getRules() {
        return rules;
    }

    public static RulerPersistentState get(ServerLevel world) {
        RulerPersistentState state = world.getDataStorage().computeIfAbsent(TYPE);

        // write initial data to the disk
        state.setDirty();

        return state;
    }
}
