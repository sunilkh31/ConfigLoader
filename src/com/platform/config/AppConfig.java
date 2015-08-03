package com.platform.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class AppConfig {

    private static final Pattern sectionPattern = Pattern.compile("\\s*\\[([^]]*)\\]\\s*");
    private static final Pattern keyValuePattern = Pattern.compile("\\s*([^=]*)=(.*)");
    private static final Pattern overridePattern = Pattern.compile("\\s*(.*)<(.*)>\\s*");
    private static final Pattern commentPattern = Pattern.compile("\\s*;(.*)");
    private static final Character START_QUOTE = '“';
    private static final Character END_QUOTE = '”';
    private static final String ARRAY_SEPARATOR = ",";

    private HashMap<String, HashMap<String, String>> configDetails = new HashMap<>();

    // An instance of AppConfig can only be created by using load method
    private AppConfig() {
    }

    /**
     * Loads the configuration file in memory and drops all the unrelated overrides configuration.
     * 
     * @param filePath
     * @param overrides
     * @return
     * @throws IOException
     */
    public static AppConfig load(String filePath, String... overrides) throws IOException {
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("Invalid filename");
        }
        if (overrides == null) {
            overrides = new String[0];
        }

        HashSet<String> overrideSet = new HashSet<>();
        overrideSet.addAll(Arrays.asList(overrides));

        File userFileName = new File(filePath);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(userFileName));
            String lastSection = "";
            AppConfig appConfig = new AppConfig();

            while (true) {
                String line = reader.readLine();

                if (line == null)
                    break;

                if (!shouldProcessLine(line))
                    continue;

                if (isSectionStart(line)) {
                    lastSection = addNewSection(appConfig, line);
                } else {
                    addSectionDetails(overrideSet, lastSection, appConfig, line);
                }
            }
            return appConfig;
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    /**
     * Tells whether a given line should be processed or not
     * 
     * @param line
     * @return
     */
    private static boolean shouldProcessLine(String line) {
        // IgnoreLines with comments
        return (!(commentPattern.matcher(line).matches() || line.isEmpty()));
    }

    /**
     * Adds section details
     * 
     * @param overrideSet
     * @param lastSection
     * @param appConfig
     * @param line
     */
    private static void addSectionDetails(HashSet<String> overrideSet, String lastSection, AppConfig appConfig,
            String line) {
        String[] entry = extractEntry(line, overrideSet);
        if (lastSection == "") {
            throw new IllegalArgumentException("No valid section found for the given section detail " + line);
        }
        if (entry != null) {
            appConfig.configDetails.get(lastSection).put(entry[0], entry[1]);
        }
    }

    /**
     * Adds a new section, NOP if a section with the given name is already found. Thus allowing a section to be
     * partitioned across the configuration file
     * 
     * @param appConfig
     * @param line
     * @return
     */
    private static String addNewSection(AppConfig appConfig, String line) {
        String lastSection = getSectionName(line);
        if (!appConfig.configDetails.containsKey(lastSection)) {
            appConfig.configDetails.put(lastSection, new HashMap<>());
        }
        return lastSection;
    }

    /**
     * Extracts the valid entry from the given line after taking care of the overrides. Returns null if the entry is not
     * a valid one
     * 
     * @param line
     * @return
     */
    private static String[] extractEntry(String line, HashSet<String> overrides) {
        Matcher matcher = keyValuePattern.matcher(line);
        if (matcher.matches()) {
            String[] keyValue = new String[2];
            keyValue[0] = matcher.group(1).trim();
            keyValue[1] = matcher.group(2).trim();
            return filterByOverride(overrides, keyValue);
        }
        return null;
    }

    /**
     * Drops or updates the keyValue based on overrides
     * 
     * @param overrides
     * @param keyValue
     */
    private static String[] filterByOverride(HashSet<String> overrides, String[] keyValue) {
        Matcher overrideMatcher = overridePattern.matcher(keyValue[0]);
        if (overrideMatcher.matches()) {
            String override = overrideMatcher.group(2).trim();
            if (overrides.contains(override)) {
                keyValue[0] = overrideMatcher.group(1).trim();
            } else {
                return null;
            }
        }
        return keyValue;
    }

    /**
     * Extracts the section name from the given line
     * 
     * @param line
     * @return
     */
    private static String getSectionName(String line) {
        Matcher matcher = sectionPattern.matcher(line);
        if (matcher.matches()) {
            // Validate section name
            return matcher.group(1).trim();
        }
        return null;
    }

    /**
     * Tells whether the given line is a section or not
     * 
     * @param line
     * @return
     */
    private static boolean isSectionStart(String line) {
        return sectionPattern.matcher(line).matches();
    }

    /**
     * Returns the value for the given key in the configuration file
     * 
     * @param key
     * @return
     */
    public Object get(String key) {
        String[] split = key.split("\\.");
        if (split.length != 0 && !split[0].isEmpty() && split.length == 2) {
            HashMap<String, String> section = configDetails.get(split[0]);
            if (section != null && split[1] != null) {
                return toConvertedValue(section.get(split[1]));
            }
        } else if (split.length == 1) {
            HashMap<String, String> details = configDetails.get(split[0]);
            if (details != null) {
                return details.toString();
            }
        }
        return null;
    }

    /**
     * Converts the given value in either String or String[]
     * 
     * @param string
     * @return
     */
    private Object toConvertedValue(String value) {
        if (value == null) {
            return value;
        }
        if (isString(value)) {
            return value;
        } else if (isArray(value)) {
            return value.split(ARRAY_SEPARATOR);
        } else {
            return value;
        }
    }

    /**
     * Returns true if the string contains a comma
     * 
     * @param value
     * @return
     */
    private boolean isArray(String value) {
        return value != null && !value.isEmpty() && !isString(value) && value.contains(",");
    }

    /**
     * Returns true if the value starts with quote and ends with quote
     * 
     * @param value
     * @return
     */
    private boolean isString(String value) {
        return (value != null && !value.isEmpty() && value.charAt(0) == START_QUOTE && value.charAt(value.length() - 1) == END_QUOTE);
    }
}
