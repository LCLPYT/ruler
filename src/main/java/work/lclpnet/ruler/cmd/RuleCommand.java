package work.lclpnet.ruler.cmd;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import work.lclpnet.ruler.Ruler;
import work.lclpnet.ruler.cmd.arg.WorldSuggestionProvider;
import work.lclpnet.ruler.rule.Rule;
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

        WorldSuggestionProvider dimensions = new WorldSuggestionProvider();

        Rules.each(ruleKey -> {
            String id = ruleKey.identifier().toString();

            var valueArg = argument("value", StringArgumentType.string())
                    .executes(ctx -> setRuleValue(ctx, ruleKey))
                    .then(argument("dimension", IdentifierArgumentType.identifier())
                            .suggests(dimensions)
                            .executes(ctx -> setDimensionRuleValue(ctx, ruleKey)));

            suggestValues(valueArg, ruleKey);

            set.then(literal(id)
                    .then(valueArg));

            get.then(literal(id)
                    .executes(ctx -> getRuleValue(ctx, ruleKey))
                    .then(argument("dimension", IdentifierArgumentType.identifier())
                            .suggests(dimensions)
                            .executes(ctx -> getDimensionRuleValue(ctx, ruleKey))));
        });

        return literal("rule")
                .requires(s -> s.hasPermissionLevel(2))
                .then(set)
                .then(get);
    }

    private void suggestValues(RequiredArgumentBuilder<ServerCommandSource, String> argument, RuleKey<? extends Rule<?>> ruleKey) {
        var suggestions = Rules.suggestions(ruleKey);

        if (suggestions != null) {
            argument.suggests(suggestions);
        }
    }

    private int getRuleValue(CommandContext<ServerCommandSource> ctx, RuleKey<?> key) {
        ServerWorld world = ctx.getSource().getWorld();

        return getRule(ctx, key, world);
    }

    private int getDimensionRuleValue(CommandContext<ServerCommandSource> ctx, RuleKey<?> key) throws CommandSyntaxException {
        ServerWorld world = WorldSuggestionProvider.getWorld(ctx, "dimension");

        return getRule(ctx, key, world);
    }

    private int setRuleValue(CommandContext<ServerCommandSource> ctx, RuleKey<?> key) {
        String value = StringArgumentType.getString(ctx, "value");
        ServerWorld world = ctx.getSource().getWorld();

        return setRule(ctx, key, world, value);
    }

    private int setDimensionRuleValue(CommandContext<ServerCommandSource> ctx, RuleKey<?> key) throws CommandSyntaxException {
        String value = StringArgumentType.getString(ctx, "value");
        ServerWorld world = WorldSuggestionProvider.getWorld(ctx, "dimension");

        return setRule(ctx, key, world, value);
    }

    private int getRule(CommandContext<ServerCommandSource> ctx, RuleKey<?> key, ServerWorld world) {
        ServerCommandSource source = ctx.getSource();

        Rules rules = Ruler.getApi().getRuleManager().getRules(world);

        var rule = rules.getRule(key);

        source.sendMessage(Text.translatable("ruler.cmd.rule.current", key.identifier(), rule.serialized()));

        return 1;
    }

    private int setRule(CommandContext<ServerCommandSource> ctx, RuleKey<?> key, ServerWorld world, String value) {
        ServerCommandSource source = ctx.getSource();

        Rules rules = Ruler.getApi().getRuleManager().getRules(world);

        var rule = rules.getRule(key);
        rule.changeFromInput(value);

        source.sendMessage(Text.translatable("ruler.cmd.rule.updated", key.identifier(), rule.serialized()));

        return 1;
    }
}
