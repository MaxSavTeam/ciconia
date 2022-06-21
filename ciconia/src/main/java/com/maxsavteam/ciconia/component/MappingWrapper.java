package com.maxsavteam.ciconia.component;

import com.maxsavteam.ciconia.annotation.Mapping;
import com.maxsavteam.ciconia.annotation.RequestMethod;
import com.maxsavteam.ciconia.exception.InvalidPathVariableException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MappingWrapper {

	private static final Pattern VARIABLE_NAME_PATTERN = Pattern.compile("^[a-zA-Z_]+$");

	private final Mapping mapping;

	private final List<String> pathVariables;

	private final Pattern pattern;

	public MappingWrapper(Mapping mapping) {
		this.mapping = mapping;
		pathVariables = resolvePathVariables();
		pattern = createRegexPattern();
	}

	private List<String> resolvePathVariables() {
		List<String> variables = new ArrayList<>();
		String path = mapping.value();
		for (int i = 0; i < path.length(); i++) {
			char c = path.charAt(i);
			if (c == '{') {
				int end = path.indexOf('}', i);
				if (end == -1) {
					throw new InvalidPathVariableException("Unclosed path variable at position " + i + ": " + path);
				}
				String variableName = path.substring(i + 1, end);

				if (!VARIABLE_NAME_PATTERN.matcher(variableName).matches()) {
					throw new InvalidPathVariableException(
							String.format(
									"Path variable name (%s) does not match pattern: %s",
									variableName,
									VARIABLE_NAME_PATTERN.pattern()
							)
					);
				}

				if (variables.contains(variableName)) {
					throw new InvalidPathVariableException("Duplicate path variable: " + variableName);
				}

				variables.add(variableName);

				i = end;
			}
		}
		return variables;
	}

	private Pattern createRegexPattern() {
		String path = mapping.value();
		final String variablePattern = "(?<%s>[\\w]*)";
		StringBuilder sb = new StringBuilder();
		boolean escapeIntervalStarted = false;
		for (int i = 0; i < path.length(); i++) {
			char c = path.charAt(i);
			if (c == '{') {
				if (escapeIntervalStarted) {
					sb.append("\\E");
					escapeIntervalStarted = false;
				}
				int end = path.indexOf('}', i); // never -1
				String variableName = path.substring(i + 1, end);
				sb.append(String.format(variablePattern, variableName));
				i = end;
			} else {
				if (!escapeIntervalStarted) {
					sb.append("\\Q");
					escapeIntervalStarted = true;
				}
				sb.append(c);
			}
		}
		if (escapeIntervalStarted) {
			sb.append("\\E");
		}

		return Pattern.compile("^" + sb + "$");
	}

	public Mapping getMapping() {
		return mapping;
	}

	public String getMappingName() {
		return mapping.value();
	}

	public List<String> getPathVariables() {
		return pathVariables;
	}

	public boolean containsRequestMethod(RequestMethod requestMethod) {
		for (RequestMethod method : mapping.method()) {
			if (method == requestMethod) {
				return true;
			}
		}
		return false;
	}

	public boolean matches(String path) {
		return pattern.matcher(path).matches();
	}

	public Map<String, String> extractPathVariables(String path) {
		Matcher matcher = pattern.matcher(path);
		if (!matcher.matches()) {
			throw new IllegalArgumentException("Path does not match mapping pattern");
		}
		Map<String, String> pathVariables = new HashMap<>();
		for (String variable : this.pathVariables) {
			pathVariables.put(variable, matcher.group(variable));
		}
		return pathVariables;
	}

}
