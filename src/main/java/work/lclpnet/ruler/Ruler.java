package work.lclpnet.ruler;

import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import work.lclpnet.ruler.api.RulerApi;
import work.lclpnet.ruler.impl.RulerApiImpl;

public final class Ruler {

    public static final String MOD_ID = "ruler";
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
    public static Identifier identifier(String path) {
        return new Identifier(MOD_ID, path);
    }

    private static class ApiHolder {
        private static final RulerApi instance = new RulerApiImpl();
    }
}
