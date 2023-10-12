package work.lclpnet.ruler;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import work.lclpnet.ruler.cmd.RuleCommand;

public class RulerInit implements ModInitializer {

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			new RuleCommand().register(dispatcher);
		});

		Ruler.LOGGER.info("Initialized.");
	}
}