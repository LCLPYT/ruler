package work.lclpnet.ruler.cmd.arg;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public class WorldSuggestionProvider implements SuggestionProvider<ServerCommandSource> {

    private static final DynamicCommandExceptionType INVALID_DIMENSION_EXCEPTION = new DynamicCommandExceptionType(id -> Text.translatable("argument.dimension.invalid", id));
    private final Predicate<ServerWorld> predicate;

    public WorldSuggestionProvider() {
        this(world -> true);
    }

    public WorldSuggestionProvider(Predicate<ServerWorld> predicate) {
        this.predicate = predicate;
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        MinecraftServer server = context.getSource().getServer();
        if (server == null) return builder.buildFuture();

        for (var key : server.getWorldRegistryKeys()) {
            ServerWorld world = server.getWorld(key);
            if (world == null) continue;

            if (predicate.test(world)) {
                builder.suggest(key.getValue().toString());
            }
        }

        return builder.buildFuture();
    }

    @NotNull
    public static ServerWorld getWorld(CommandContext<ServerCommandSource> ctx, String name) throws CommandSyntaxException {
        Identifier worldId = IdentifierArgumentType.getIdentifier(ctx, name);

        ServerCommandSource source = ctx.getSource();
        MinecraftServer server = source.getServer();

        RegistryKey<World> key = RegistryKey.of(RegistryKeys.WORLD, worldId);

        ServerWorld world = server.getWorld(key);

        if (world == null) {
            throw INVALID_DIMENSION_EXCEPTION.create(worldId);
        }

        return world;
    }
}
