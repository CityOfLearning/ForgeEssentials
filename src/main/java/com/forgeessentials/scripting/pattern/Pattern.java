package com.forgeessentials.scripting.pattern;

import java.util.ArrayList;
import java.util.List;

public class Pattern {

	public static enum ArgumentType {
		NONE, PLAYER, GROUP, ZONE, DECIMAL, FLOAT, REST;
	}

	public static class PatternMatchException extends Exception {

		public PatternMatchException(String message) {
			super(message);
		}

	}

	private String pattern;

	java.util.regex.Pattern patternRegex;

	List<ArgumentType> argumentTypes = new ArrayList<>();

	public Pattern(String pattern) throws IllegalArgumentException {
		this.pattern = pattern;
		StringBuilder regex = new StringBuilder();
		boolean mustEnd = false;
		for (String part : pattern.split(" ")) {
			if (part.isEmpty()) {
				continue;
			}
			if (mustEnd) {
				throw new IllegalArgumentException("Pattern must end after @*");
			}
			if (regex.length() > 0) {
				regex.append("\\s+");
			}
			if (part.charAt(0) == '@') {
				switch (part.substring(1)) {
				case "f":
					regex.append("([+-]?\\d+(?:\\.\\d+)?)");
					argumentTypes.add(ArgumentType.FLOAT);
					break;
				case "d":
					regex.append("([+-]?\\d+)");
					argumentTypes.add(ArgumentType.DECIMAL);
					break;
				case "p":
				case "player":
					regex.append("(\\S+)");
					argumentTypes.add(ArgumentType.PLAYER);
					break;
				case "g":
				case "group":
					regex.append("(\\S+)");
					argumentTypes.add(ArgumentType.GROUP);
					break;
				case "zone":
					regex.append("(\\d+)");
					argumentTypes.add(ArgumentType.ZONE);
					break;
				case "*":
					regex.append("(.*)");
					argumentTypes.add(ArgumentType.REST);
					mustEnd = true;
					break;
				case "+":
					regex.append("(.+)");
					argumentTypes.add(ArgumentType.REST);
					mustEnd = true;
					break;
				case "":
					regex.append("(\\S+)");
					argumentTypes.add(ArgumentType.NONE);
					break;
				default:
					throw new IllegalArgumentException(
							String.format("Unknown pattern argument type %s ", part.substring(1)));
				}
			} else {
				regex.append(part);
				// regex.append(java.util.regex.Pattern.quote(part));
			}
		}
		// Cut off final space
		if ((regex.length() > 0) && (regex.charAt(regex.length() - 1) == ' ')) {
			regex.setLength(regex.length() - 1);
		}
		patternRegex = java.util.regex.Pattern.compile(regex.toString(), java.util.regex.Pattern.CASE_INSENSITIVE);
	}

	public List<ArgumentType> getArgumentTypes() {
		return argumentTypes;
	}

	public String getPattern() {
		return pattern;
	}

	public java.util.regex.Pattern getPatternRegex() {
		return patternRegex;
	}

	public void onMatch(List<String> arguments) {
		/* do nothing */
	}

}