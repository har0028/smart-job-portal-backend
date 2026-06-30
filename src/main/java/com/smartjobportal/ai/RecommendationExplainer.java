package com.smartjobportal.ai;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Generates a human-readable recommendation reason string
 * based on the match result data.
 */
@Component
public class RecommendationExplainer {

    public String explain(MatchResult result) {
        StringBuilder sb = new StringBuilder();

        double score = result.getSkillMatchScore();
        int matched = result.getMatchedCount();
        int total = result.getTotalRequiredSkills();
        Set<String> missing = result.getMissingSkills();

        // Opening statement
        if (score == 100.0) {
            sb.append("Excellent match! You have all ").append(total)
              .append(" required skills for this position.");
        } else if (score >= 75.0) {
            sb.append("Strong match. You have ").append(matched)
              .append(" of ").append(total).append(" required skills.");
        } else if (score >= 50.0) {
            sb.append("Good match. You have ").append(matched)
              .append(" of ").append(total).append(" required skills.");
        } else if (score >= 25.0) {
            sb.append("Partial match. You have ").append(matched)
              .append(" of ").append(total).append(" required skills.");
        } else {
            sb.append("Low match. You have ").append(matched)
              .append(" of ").append(total).append(" required skills.");
        }

        // Matched skills
        if (!result.getMatchedSkills().isEmpty()) {
            String matchedList = result.getMatchedSkills().stream()
                    .map(this::capitalize)
                    .sorted()
                    .collect(Collectors.joining(", "));
            sb.append(" Matched: ").append(matchedList).append(".");
        }

        // Missing skills
        if (!missing.isEmpty()) {
            String missingList = missing.stream()
                    .map(this::capitalize)
                    .sorted()
                    .collect(Collectors.joining(", "));
            sb.append(" Skills to develop: ").append(missingList).append(".");
        }

        // Score summary
        sb.append(String.format(" Overall match score: %.1f%%.", score));

        return sb.toString();
    }

    private String capitalize(String s) {
        if (s == null || s.isBlank()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
