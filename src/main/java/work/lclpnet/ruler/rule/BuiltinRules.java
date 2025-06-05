package work.lclpnet.ruler.rule;

import work.lclpnet.ruler.rule.rules.BooleanRule;

import static work.lclpnet.ruler.Ruler.identifier;

public class BuiltinRules {

    public static final RuleKey<Boolean, BooleanRule>
            WATER_FREEZING = Rules.register(identifier("water_freezing"), BooleanRule.create(true)),
            ICE_MELTING = Rules.register(identifier("ice_melting"), BooleanRule.create(true)),
            CORAL_DEATH = Rules.register(identifier("coral_death"), BooleanRule.create(true)),
            FARMLAND_TRAMPLING = Rules.register(identifier("farmland_trampling"), BooleanRule.create(true)),
            TURTLE_EGG_TRAMPLING = Rules.register(identifier("turtle_egg_trampling"), BooleanRule.create(true)),
            FARMLAND_DRY_OUT = Rules.register(identifier("farmland_dry_out"), BooleanRule.create(true)),
            FLUID_FLOW = Rules.register(identifier("fluid_flow"), BooleanRule.create(true));

    public static void initialize() {
        /* NO-OP */
    }
}
