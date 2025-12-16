package work.lclpnet.ruler;

import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import work.lclpnet.ruler.api.RulerApi;
import work.lclpnet.ruler.impl.RulerApiImpl;

import static work.lclpnet.ruler.RulerConstants.MOD_ID;

public final class Ruler {

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private Ruler() {}

    public static RulerApi getApi() {
        return ApiHolder.instance;
    }

    /**
     * Creates an identifier namespaced with the identifier of the mod.
     * @param path The path.
     * @return An identifier of this mod with the given path.
     */
    public static ResourceLocation identifier(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static String permission(String suffix) {
        return MOD_ID + "." + suffix;
    }

    private static class ApiHolder {
        private static final RulerApi instance = new RulerApiImpl();
    }
}
