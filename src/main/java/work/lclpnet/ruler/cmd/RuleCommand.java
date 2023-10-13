package work.lclpnet.ruler.cmd;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import work.lclpnet.ruler.Ruler;
import work.lclpnet.ruler.rule.RuleKey;
import work.lclpnet.ruler.rule.Rules;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class RuleCommand {

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(command());
    }

    private LiteralArgumentBuilder<ServerCommandSource> command() {
        var set = literal("set");
        var get = literal("get");

        Rules.each(ruleKey -> {
            String id = ruleKey.identifier().toString();

            set.then(literal(id)
                    .then(argument("value", StringArgumentType.string())
                            .executes(ctx -> setRuleValue(ctx, ruleKey))));

            get.then(literal(id)
                    .executes(ctx -> getRuleValue(ctx, ruleKey)));
        });

        return literal("rule")
                .requires(s -> s.hasPermissionLevel(2))
                .then(set)
                .then(get);
    }

    private int getRuleValue(CommandContext<ServerCommandSource> ctx, RuleKey<?> key) {
        ServerCommandSource source = ctx.getSource();
        ServerWorld world = source.getWorld();

        Rules rules = Ruler.getApi().getRuleManager().getRules(world);

        var rule = rules.getRule(key);

        source.sendMessage(Text.translatable("ruler.cmd.rule.current", key.identifier(), rule.serialized()));

        return 1;
    }

    private int setRuleValue(CommandContext<ServerCommandSource> ctx, RuleKey<?> key) {
        String value = StringArgumentType.getString(ctx, "value");

        ServerCommandSource source = ctx.getSource();
        ServerWorld world = source.getWorld();

        Rules rules = Ruler.getApi().getRuleManager().getRules(world);

        var rule = rules.getRule(key);
        rule.changeFromInput(value);

        source.sendMessage(Text.translatable("ruler.cmd.rule.updated", key.identifier(), rule.serialized()));

        return 1;
    }
}
