package com.forgeessentials.commons;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Strings;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;

public class Censor {

	private static List<CensoredWord> filterList = new ArrayList<>();
	
	static {
		for(String word : new String[] { "fuck\\S*", "bastard", "moron", "ass", "asshole",
			"bitch", "shit" }){
			filterList.add(new CensoredWord(word));
		}
	}

	private static boolean enabled = true;

	private static String censorSymbol = "#";

	private static int censorSlap = 1;
	
	public static class CensoredWord {

		public String word;

		public String blank;

		public Pattern pattern;

		public CensoredWord(String word) {
			if (word.startsWith("!")) {
				word = word.substring(1);
			} else {
				word = "\\b" + word + "\\b";
			}
			pattern = Pattern.compile(word, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE);
		}

	}

	public static boolean containsSwear(String message) {
		return !message.equals(filter(message));
	}

	public static String filter(String message) {
		return filter(message, null);
	}

	public static String filter(String message, EntityPlayer player) {
		if (!enabled) {
			return message;
		}
		for (CensoredWord filter : filterList) {
			Matcher m = filter.pattern.matcher(message);
			if (m.find()) {
				if (filter.blank == null) {
					filter.blank = Strings.repeat(censorSymbol, m.end() - m.start());
				}
				message = m.replaceAll(filter.blank);
				if ((player != null) && (censorSlap != 0)) {
					player.attackEntityFrom(DamageSource.generic, censorSlap);
				}
			}
		}
		return message;
	}
	
	public Censor(List words, boolean isEnabled, String symbol, int damage){
		filterList = words;
		enabled = isEnabled;
		censorSymbol = symbol;
		censorSlap = damage;
		
	}
}
