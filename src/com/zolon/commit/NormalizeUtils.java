package com.zolon.commit;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.trimToEmpty;

/**
 * @author manoo
 */
public class NormalizeUtils {
    public static final int MAX_LINE_LENGTH = 72; // https://stackoverflow.com/a/2120040/5138796

    public static final String BROKEN_CHANGES_PREFIX = "Broken: ";

    public static final String RELATED_ISSUE_PREFIX = "Related: ";
    public static final String RELATED_ISSUE_SPLIT = ",";
    public static final String NUM_ISSUE_PREFIX = "#";
    public static final String EMPTY_STR = "";

    public static String normalizeHeader(ChangeType type, String scope, String subject) {
        StringBuilder builder = new StringBuilder();

        /// header
        builder.append("[").append(type.label()).append("]");
        if (!isBlank(scope)) {
            builder.append('(').append(scope).append(')');
        }

        builder.append(": ").append(subject);
        return builder.toString();
    }

    public static String normalizeIssues(String input) {
        String[] issues = StringUtils.split(normalize(input), RELATED_ISSUE_SPLIT);
        return Arrays.stream(issues)
                .map(NormalizeUtils::normalizeIssue)
                .collect(Collectors.joining(RELATED_ISSUE_SPLIT));

    }

    public static String normalizeIssue(String input) {
        String issue = normalize(input);
        return isNumeric(issue) ? NUM_ISSUE_PREFIX + issue : issue;
    }


    public static String normalizeParagraph(String input, boolean wrapText) {
        String normal = normalizeParagraph(input);
        return wrapText ? WordUtils.wrap(normal, MAX_LINE_LENGTH) : normal;
    }

    public static String normalizeBrokeChange(String input, boolean wrapText) {
        String broken = normalize(input);
        if (isBlank(broken)) {
            return EMPTY_STR;
        }
        if (StringUtils.startsWith(broken, BROKEN_CHANGES_PREFIX)) {
            return broken;
        }
        return normalizeParagraph(BROKEN_CHANGES_PREFIX + broken, wrapText);
    }

    public static String normalize(String input) {
        return trimToEmpty(input);
    }

    /**
     * Base from {@link StringUtils#isNumeric}
     */
    public static boolean isNumeric(String str) {
        if (str == null || isBlank(str)) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; ++i) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static String normalizeParagraph(String input) {
        return StringUtils.replace(toLF(input), "\n", " ");
    }

    public static String toLF(String line) {
        return StringUtils.remove(normalize(line), '\r');
    }
}
