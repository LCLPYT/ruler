package work.lclpnet.ruler;

import net.minecraft.resources.Identifier;

public class RulerConstants {

    public static final String RULES_KEY = "Rules";
    public static final String MOD_ID = "ruler";

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
