package work.lclpnet.ruler;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import work.lclpnet.ruler.cmd.RuleCommand;

public class RulerInit implements ModInitializer {

	public static final String MOD_ID = "ruler";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			new RuleCommand().register(dispatcher);
		});

		LOGGER.info("Initialized.");
	}

	/**
	 * Creates an identifier namespaced with the identifier of the mod.
	 * @param path The path.
	 * @return An identifier of this mod with the given path.
	 */
	public static Identifier identifier(String path) {
		return new Identifier(MOD_ID, path);
	}

	/**
	 * Creates an identifier namespaced with the identifier of the mod.
	 * Uses string {@link java.util.Formatter} to format the given path with the given substitutes.
	 * @param format The path with formatting identifiers like '%s'.
	 * @param substitutes The substitutes passed to the formatter.
	 * @return A formatted identifier of this mod with the given path.
	 */
	public static Identifier identifier(String format, Object... substitutes) {
		return identifier(String.format(format, substitutes));
	}
}