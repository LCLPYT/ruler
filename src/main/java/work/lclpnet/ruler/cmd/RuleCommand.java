package work.lclpnet.ruler.cmd;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class RuleCommand {

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(command());
    }

    private LiteralArgumentBuilder<ServerCommandSource> command() {
        return literal("rule")
                .requires(s -> s.hasPermissionLevel(2))
                .then(literal("set"))
                .then(literal("get"));
    }
}
