package work.lclpnet.ruler;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import work.lclpnet.kibu.translate.TranslationService;
import work.lclpnet.ruler.cmd.RuleCommand;
import work.lclpnet.ruler.event.WorldListener;

public class RulerInit implements ModInitializer {

	@Override
	public void onInitialize() {
		RulerTranslations.Result translation = RulerTranslations.load(Ruler.LOGGER);
		TranslationService translationService = translation.translationService();

		translation.whenLoaded().thenRun(() -> Ruler.LOGGER.info("Ruler translations loaded."));

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			new RuleCommand(translationService).register(dispatcher);
		});

		WorldListener.register();

		Ruler.LOGGER.info("Initialized.");
	}
}