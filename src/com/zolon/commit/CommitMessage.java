package com.zolon.commit;

import org.apache.commons.lang.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.zolon.commit.NormalizeUtils.*;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.lang.StringUtils.trimToEmpty;

/**
 * Base from <a href="https://github.com/MobileTribe/commit-template-idea-plugin">MobileTribe/commit-template-idea-plugin</a>
 *
 * @author Damien Arrachequesne
 * @author manoo
 */
class CommitMessage {

    public static final Pattern COMMIT_FIRST_LINE_FORMAT = Pattern.compile("^\\[([a-z]+)](\\((.+)\\))?: (.+)");

    // header
    private ChangeType type;
    private String scope;
    private String subject;
    // body
    private final String details;
    private String broken;

    // footer
    private String related;
    private boolean wrapText = true;

    private CommitMessage(String message) {
        this.details = toLF(message);
    }

    public CommitMessage(ChangeType type,
                         String scope,
                         String subject,
                         String details,
                         String broken,
                         String relatedIssues,
                         boolean wrapText
    ) {
        this.type = type;
        this.wrapText = wrapText;

        this.scope = normalize(scope);
        this.subject = normalize(subject);

        this.details = normalizeParagraph(details, wrapText);
        this.broken = normalizeBrokeChange(broken, wrapText);
        this.related = normalizeIssues(relatedIssues);
    }

    /*
     * example output:
     * [fix](Smartlanding): v1 版本中 OS 被被更新为 NONE
     *
     * Smartlanding 在 v2 版本中将OS信息移动到common域，并在Apply的时候更新，这导致v1版本os被更新为NONE
     *
     * Related: MAXSTORE-51646
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        ///
        /// header
        ///
        builder.append(normalizeHeader(type, scope, subject))
                .append(System.lineSeparator());
        ///
        /// body
        ///
        if (isNotBlank(details)) {
            builder
                    .append(System.lineSeparator())
                    .append(details)
                    .append(System.lineSeparator())
            ;
        }
        ///
        /// footer
        ///
        if (isNotBlank(broken) || isNotBlank(related)) {
            builder.append(System.lineSeparator());
        }
        if (isNotBlank(broken)) {
            builder.append(broken)
                    .append(System.lineSeparator());
        }

        if (isNotBlank(related)) {
            for (String relatedIssue : related.split(RELATED_ISSUE_SPLIT)) {
                builder.append(RELATED_ISSUE_PREFIX)
                        .append(normalizeIssue(relatedIssue))
                        .append(System.lineSeparator());
            }
        }

        return builder.substring(0, builder.length() - System.lineSeparator().length());
    }


    public static CommitMessage parse(String message) {
        ///
        /// header
        ///
        Matcher matcher = COMMIT_FIRST_LINE_FORMAT.matcher(message);
        if (!matcher.find()) {
            return new CommitMessage(message);
        }

        ChangeType type = ChangeType.valueOf(matcher.group(1).toUpperCase());
        String scope = matcher.group(3);
        String subject = matcher.group(4);
        boolean wrap = true;


        String[] lines = StringUtils.split(toLF(message), "\n");
        if (lines.length < 2) {
            return new CommitMessage(type, scope, subject, null, null, null, wrap);
        }
        ///
        /// details
        ///
        int pos = 1;
        StringBuilder builder = new StringBuilder();
        for (; pos < lines.length; pos++) {
            String line = lines[pos];
            if (isBrokenChange(line) || isRelatedIssue(line)) {
                break;
            }
            builder.append(line).append(System.lineSeparator());
        }
        String details = builder.toString();
        builder.delete(0, builder.length());
        ///
        ///  broken changes
        ///
        for (; pos < lines.length; pos++) {
            String line = lines[pos];
            if (wrap && line.length() > MAX_LINE_LENGTH) {
                wrap = false;
            }
            if (StringUtils.startsWith(line, RELATED_ISSUE_PREFIX)) {
                break;
            }
            builder.append(lines[pos]).append(System.lineSeparator());
        }
        String broken = builder.length() > BROKEN_CHANGES_PREFIX.length() ?
                builder.substring(BROKEN_CHANGES_PREFIX.length()) :
                builder.toString();
        builder.delete(0, builder.length());
        ///
        /// related issues
        ///
        for (; pos < lines.length; pos++) {
            String line = lines[pos];
            if (wrap && line.length() > MAX_LINE_LENGTH) {
                wrap = false;
            }
            if (line.startsWith(RELATED_ISSUE_PREFIX)) {
                String issue = parseFormatRelatedIssue(line);
                builder.append(issue);
                if (StringUtils.isNotBlank(issue)) {
                    builder.append(RELATED_ISSUE_SPLIT);
                }
            }
        }

        // remove last ,
        if (builder.length() > 0) {
            builder.delete(builder.length() - 1, builder.length());
        }

        String related = trimToEmpty(builder.toString());

        return new CommitMessage(type, scope, subject, details, broken, related, wrap);
    }

    public ChangeType getType() {
        return type;
    }

    public String getScope() {
        return scope;
    }

    public String getSubject() {
        return subject;
    }

    public String getDetails() {
        return details;
    }

    public String getBroken() {
        return broken;
    }

    public String getRelated() {
        return related;
    }


    private static boolean isBrokenChange(String line) {
        return StringUtils.startsWith(line, BROKEN_CHANGES_PREFIX);
    }

    private static boolean isRelatedIssue(String line) {
        return StringUtils.startsWith(line, RELATED_ISSUE_PREFIX);
    }

    private static String formatRelatedIssue(String closedIssue) {
        return trimToEmpty(closedIssue);
    }

    private static String parseFormatRelatedIssue(String format) {
        return StringUtils.removeStart(trimToEmpty(format), RELATED_ISSUE_PREFIX);
    }
}