package work.lclpnet.ruler.rule;

import net.minecraft.util.Identifier;

public record RuleKey<T extends Rule<?>>(Identifier identifier) {

}
