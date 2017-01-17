package com.forgeessentials.chat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Set;

import com.forgeessentials.commons.Censor;
import com.forgeessentials.commons.Censor.CensoredWord;
import com.forgeessentials.commons.output.LoggingHandler;
import com.forgeessentials.core.moduleLauncher.config.ConfigLoaderBase;

import net.minecraftforge.common.config.Configuration;

public class ChatConfig extends ConfigLoaderBase {

	private static final String CATEGORY = ModuleChat.CONFIG_CATEGORY;

	private static final String CAT_GM = CATEGORY + ".Gamemodes";

	public static final String CHAT_FORMAT_HELP = "Format for chat. Always needs to contain all 5 \"%s\" placeholders like the default!";

	private static final String MUTEDCMD_HELP = "All commands in here will be blocked if the player is muted.";

	private static final String WELCOME_MESSAGE = "Welcome messages for new players. Can be color formatted (supports script arguments)";

	private static final String LOGIN_MESSAGE = "Login message shown each time the player logs in (supports script arguments)";

	private static final String DEFAULT_WELCOME_MESSAGE = "New player @player joined the server!";

	private static final String[] DEFAULT_LOGIN_MESSAGE = new String[] { "Welcome @player.",
			"This server is running ForgeEssentials" };

	public static String gamemodeCreative;

	public static String gamemodeAdventure;

	public static String gamemodeSurvival;

	public static String chatFormat = "%s%s<%s>%s%s ";

	public static String welcomeMessage;

	public static String[] loginMessage;

	public static Set<String> mutedCommands = new HashSet<>();

	// censor
	public static final String[] DEFAULT_WORDS = new String[] { "fuck\\S*", "bastard", "moron", "ass", "asshole",
			"bitch", "shit" };

	private static final String CENSOR_HELP = "Words to be censored. Prepend with ! to disable word boundary check.";

	private static List<CensoredWord> filterList = new ArrayList<>();

	public static boolean enabled;

	public static String censorSymbol;

	public static int censorSlap;

	@Override
	public void load(Configuration config, boolean isReload) {
		config.addCustomCategoryComment("Chat", "Chat configuration");

		try {
			chatFormat = config.get("Chat", "ChatFormat", "%s%s<%s>%s%s ", CHAT_FORMAT_HELP).getString();
			String.format(chatFormat, "", "", "", "", "");
		} catch (IllegalFormatException e) {
			LoggingHandler.felog.error("Invalid chat format specified in chat config!");
			chatFormat = "%s%s<%s>%s%s ";
		}

		welcomeMessage = config.get("Chat", "WelcomeMessage", DEFAULT_WELCOME_MESSAGE, WELCOME_MESSAGE).getString();
		loginMessage = config.get("Chat", "LoginMessage", DEFAULT_LOGIN_MESSAGE, LOGIN_MESSAGE).getStringList();

		config.addCustomCategoryComment(CAT_GM, "Gamemode names");
		gamemodeSurvival = config.get(CAT_GM, "Survival", "survival").getString();
		gamemodeCreative = config.get(CAT_GM, "Creative", "creative").getString();
		gamemodeAdventure = config.get(CAT_GM, "Adventure", "adventure").getString();

		mutedCommands.clear();
		for (String cmd : config.get("Chat.mute", "mutedCommands", new String[] { "me" }, MUTEDCMD_HELP)
				.getStringList()) {
			mutedCommands.add(cmd);
		}

		ModuleChat.instance
				.setChatLogging(config.get(CATEGORY, "LogChat", true, "Log all chat messages").getBoolean(true));

		enabled = config.get("Chat.Censor", "enable", true).getBoolean(true);
		censorSlap = config.get("Chat.Censor", "slapDamage", 1, "Damage to a player when he uses a censored word")
				.getInt();
		censorSymbol = config.get("Chat.Censor", "censorSymbol", "#", "Replace censored words with this character")
				.getString();
		if (censorSymbol.length() > 1) {
			LoggingHandler.felog.warn("Censor symbol is too long!");
			censorSymbol = censorSymbol.substring(1);
		} else if (censorSymbol.isEmpty()) {
			LoggingHandler.felog.warn("Censor symbol is empty!");
			censorSymbol = "#";
		}
		filterList.clear();
		for (String word : config.get("Chat.Censor", "words", DEFAULT_WORDS, CENSOR_HELP).getStringList()) {
			filterList.add(new CensoredWord(word));
		}

		ModuleChat.censor = new Censor(filterList, enabled, censorSymbol, censorSlap);
	}

	@Override
	public void save(Configuration config) {
	}

}
