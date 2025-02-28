package work.lclpnet.ruler;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import work.lclpnet.kibu.translate.Translations;
import work.lclpnet.kibu.translate.util.ModTranslations;
import work.lclpnet.ruler.cmd.RuleCommand;
import work.lclpnet.ruler.event.WorldListener;

public class RulerInit implements ModInitializer {

	@Override
	public void onInitialize() {
		var translation = ModTranslations.fromAssets(RulerConstants.MOD_ID, Ruler.LOGGER);
		Translations translationService = translation.translations();

		translation.whenLoaded().thenRun(() -> Ruler.LOGGER.info("Ruler translations loaded."));

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment)
				-> new RuleCommand(translationService).register(dispatcher));

		new WorldListener(Ruler.getApi().getRuleManager()).register();

		Ruler.LOGGER.info("Initialized.");
	}
}