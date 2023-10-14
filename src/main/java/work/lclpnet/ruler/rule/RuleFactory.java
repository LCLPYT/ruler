package work.lclpnet.ruler.rule;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.Nullable;

public interface RuleFactory<T extends Rule<?>> {

    T create(RuleHandle args);

    @Nullable
    default SuggestionProvider<ServerCommandSource> getSuggestions() {
        return null;
    }
}
