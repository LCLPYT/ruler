package work.lclpnet.ruler.cmd;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.ChatFormatting;
import net.minecraft.server.permissions.PermissionLevel;
import work.lclpnet.kibu.translate.Translations;
import work.lclpnet.ruler.Ruler;
import work.lclpnet.ruler.cmd.arg.WorldSuggestionProvider;
import work.lclpnet.ruler.rule.RuleKey;
import work.lclpnet.ruler.rule.Rules;

import static me.lucko.fabric.api.permissions.v0.Permissions.require;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;
import static work.lclpnet.kibu.translate.text.FormatWrapper.styled;
import static work.lclpnet.ruler.Ruler.permission;

public class RuleCommand {

    private final Translations translationService;

    public RuleCommand(Translations translationService) {
        this.translationService = translationService;
    }

    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(command());
    }

    private LiteralArgumentBuilder<CommandSourceStack> command() {
        var set = literal("set")
                    .requires(require(permission("command.rule.set"), PermissionLevel.GAMEMASTERS));

        var get = literal("get")
                    .requires(require(permission("command.rule.get"), PermissionLevel.GAMEMASTERS));

        WorldSuggestionProvider dimensions = new WorldSuggestionProvider();

        Rules.each(ruleKey -> {
            String id = ruleKey.identifier().toString();

            var valueArg = argument("value", StringArgumentType.string())
                    .executes(ctx -> setRuleValue(ctx, ruleKey))
                    .then(argument("dimension", IdentifierArgument.id())
                            .suggests(dimensions)
                            .executes(ctx -> setDimensionRuleValue(ctx, ruleKey)));

            suggestValues(valueArg, ruleKey);

            set.then(literal(id)
                    .requires(require(permission("command.rule.set." + id), PermissionLevel.GAMEMASTERS))
                    .then(valueArg));

            get.then(literal(id)
                    .requires(require(permission("command.rule.get." + id), PermissionLevel.GAMEMASTERS))
                    .executes(ctx -> getRuleValue(ctx, ruleKey))
                    .then(argument("dimension", IdentifierArgument.id())
                            .suggests(dimensions)
                            .executes(ctx -> getDimensionRuleValue(ctx, ruleKey))));
        });

        return literal("rule")
                .requires(require(permission("command.rule"), PermissionLevel.GAMEMASTERS))
                .then(set)
                .then(get);
    }

    private void suggestValues(RequiredArgumentBuilder<CommandSourceStack, String> argument, RuleKey<?, ?> ruleKey) {
        var suggestions = ruleKey.getSuggestions();

        if (suggestions != null) {
            argument.suggests(suggestions);
        }
    }

    private int getRuleValue(CommandContext<CommandSourceStack> ctx, RuleKey<?, ?> key) {
        ServerLevel world = ctx.getSource().getLevel();

        return getRule(ctx, key, world);
    }

    private int getDimensionRuleValue(CommandContext<CommandSourceStack> ctx, RuleKey<?, ?> key) throws CommandSyntaxException {
        ServerLevel world = WorldSuggestionProvider.getWorld(ctx, "dimension");

        return getRule(ctx, key, world);
    }

    private int setRuleValue(CommandContext<CommandSourceStack> ctx, RuleKey<?, ?> key) {
        String value = StringArgumentType.getString(ctx, "value");
        ServerLevel world = ctx.getSource().getLevel();

        return setRule(ctx, key, world, value);
    }

    private int setDimensionRuleValue(CommandContext<CommandSourceStack> ctx, RuleKey<?, ?> key) throws CommandSyntaxException {
        String value = StringArgumentType.getString(ctx, "value");
        ServerLevel world = WorldSuggestionProvider.getWorld(ctx, "dimension");

        return setRule(ctx, key, world, value);
    }

    private int getRule(CommandContext<CommandSourceStack> ctx, RuleKey<?, ?> key, ServerLevel world) {
        CommandSourceStack source = ctx.getSource();

        Rules rules = Ruler.getApi().getRuleManager().getRules(world);

        var rule = rules.getRule(key);

        source.sendSystemMessage(translationService.translateText(source, "ruler.cmd.rule.current",
                        styled(key.identifier(), ChatFormatting.YELLOW),
                        styled(rule.get().toString(), ChatFormatting.YELLOW))
                .withStyle(ChatFormatting.GREEN));

        return 1;
    }

    private int setRule(CommandContext<CommandSourceStack> ctx, RuleKey<?, ?> key, ServerLevel world, String value) {
        CommandSourceStack source = ctx.getSource();

        Rules rules = Ruler.getApi().getRuleManager().getRules(world);

        var rule = rules.getRule(key);
        rule.changeFromInput(value);

        source.sendSystemMessage(translationService.translateText(source, "ruler.cmd.rule.updated",
                        styled(key.identifier(), ChatFormatting.YELLOW),
                        styled(rule.get().toString(), ChatFormatting.YELLOW))
                .withStyle(ChatFormatting.GREEN));

        return 1;
    }
}
