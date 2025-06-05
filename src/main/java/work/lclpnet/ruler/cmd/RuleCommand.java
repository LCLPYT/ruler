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
import net.minecraft.util.Formatting;
import work.lclpnet.kibu.translate.Translations;
import work.lclpnet.ruler.Ruler;
import work.lclpnet.ruler.cmd.arg.WorldSuggestionProvider;
import work.lclpnet.ruler.rule.RuleKey;
import work.lclpnet.ruler.rule.Rules;

import static me.lucko.fabric.api.permissions.v0.Permissions.require;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import static work.lclpnet.kibu.translate.text.FormatWrapper.styled;
import static work.lclpnet.ruler.Ruler.permission;

public class RuleCommand {

    private final Translations translationService;

    public RuleCommand(Translations translationService) {
        this.translationService = translationService;
    }

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(command());
    }

    private LiteralArgumentBuilder<ServerCommandSource> command() {
        var set = literal("set")
                    .requires(require(permission("command.rule.set"), 2));

        var get = literal("get")
                    .requires(require(permission("command.rule.get"), 2));

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
                    .requires(require(permission("command.rule.set." + id), 2))
                    .then(valueArg));

            get.then(literal(id)
                    .requires(require(permission("command.rule.get." + id), 2))
                    .executes(ctx -> getRuleValue(ctx, ruleKey))
                    .then(argument("dimension", IdentifierArgumentType.identifier())
                            .suggests(dimensions)
                            .executes(ctx -> getDimensionRuleValue(ctx, ruleKey))));
        });

        return literal("rule")
                .requires(require(permission("command.rule"), 2))
                .then(set)
                .then(get);
    }

    private void suggestValues(RequiredArgumentBuilder<ServerCommandSource, String> argument, RuleKey<?, ?> ruleKey) {
        var suggestions = ruleKey.getSuggestions();

        if (suggestions != null) {
            argument.suggests(suggestions);
        }
    }

    private int getRuleValue(CommandContext<ServerCommandSource> ctx, RuleKey<?, ?> key) {
        ServerWorld world = ctx.getSource().getWorld();

        return getRule(ctx, key, world);
    }

    private int getDimensionRuleValue(CommandContext<ServerCommandSource> ctx, RuleKey<?, ?> key) throws CommandSyntaxException {
        ServerWorld world = WorldSuggestionProvider.getWorld(ctx, "dimension");

        return getRule(ctx, key, world);
    }

    private int setRuleValue(CommandContext<ServerCommandSource> ctx, RuleKey<?, ?> key) {
        String value = StringArgumentType.getString(ctx, "value");
        ServerWorld world = ctx.getSource().getWorld();

        return setRule(ctx, key, world, value);
    }

    private int setDimensionRuleValue(CommandContext<ServerCommandSource> ctx, RuleKey<?, ?> key) throws CommandSyntaxException {
        String value = StringArgumentType.getString(ctx, "value");
        ServerWorld world = WorldSuggestionProvider.getWorld(ctx, "dimension");

        return setRule(ctx, key, world, value);
    }

    private int getRule(CommandContext<ServerCommandSource> ctx, RuleKey<?, ?> key, ServerWorld world) {
        ServerCommandSource source = ctx.getSource();

        Rules rules = Ruler.getApi().getRuleManager().getRules(world);

        var rule = rules.getRule(key);

        source.sendMessage(translationService.translateText(source, "ruler.cmd.rule.current",
                        styled(key.identifier(), Formatting.YELLOW),
                        styled(rule.get().toString(), Formatting.YELLOW))
                .formatted(Formatting.GREEN));

        return 1;
    }

    private int setRule(CommandContext<ServerCommandSource> ctx, RuleKey<?, ?> key, ServerWorld world, String value) {
        ServerCommandSource source = ctx.getSource();

        Rules rules = Ruler.getApi().getRuleManager().getRules(world);

        var rule = rules.getRule(key);
        rule.changeFromInput(value);

        source.sendMessage(translationService.translateText(source, "ruler.cmd.rule.updated",
                        styled(key.identifier(), Formatting.YELLOW),
                        styled(rule.get().toString(), Formatting.YELLOW))
                .formatted(Formatting.GREEN));

        return 1;
    }
}
