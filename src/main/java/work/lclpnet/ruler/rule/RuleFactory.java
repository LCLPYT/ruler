package work.lclpnet.ruler.rule;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.Nullable;

public interface RuleFactory<V, R extends Rule<V>> {

    R create(RuleHandle<V> args);

    @Nullable
    default SuggestionProvider<ServerCommandSource> getSuggestions() {
        return null;
    }
}
