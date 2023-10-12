package work.lclpnet.ruler.cmd.arg;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;

import java.util.concurrent.CompletableFuture;

public class RuleSuggestionProvider implements SuggestionProvider<ServerCommandSource> {

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        ServerWorld world = context.getSource().getWorld();


        return null;
    }
}
