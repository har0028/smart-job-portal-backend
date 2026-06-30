package com.smartjobportal.ai;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Normalizes skill names for consistent matching.
 * Handles aliases, casing, and common abbreviations.
 */
@Component
public class SkillNormalizer {

    // Alias map: common shorthand -> canonical form
    private static final Map<String, String> ALIAS_MAP = Map.ofEntries(
        Map.entry("js", "javascript"),
        Map.entry("ts", "typescript"),
        Map.entry("node", "node.js"),
        Map.entry("nodejs", "node.js"),
        Map.entry("reactjs", "react"),
        Map.entry("react.js", "react"),
        Map.entry("vuejs", "vue.js"),
        Map.entry("vue", "vue.js"),
        Map.entry("k8s", "kubernetes"),
        Map.entry("py", "python"),
        Map.entry("rb", "ruby"),
        Map.entry("golang", "go"),
        Map.entry("postgres", "postgresql"),
        Map.entry("mongo", "mongodb"),
        Map.entry("spring", "spring boot"),
        Map.entry("ml", "machine learning"),
        Map.entry("ai", "artificial intelligence"),
        Map.entry("aws", "amazon web services"),
        Map.entry("gcp", "google cloud platform"),
        Map.entry("azure", "microsoft azure")
    );

    /**
     * Normalize a single skill name.
     */
    public String normalize(String skillName) {
        if (skillName == null) return "";
        String lowered = skillName.trim().toLowerCase();
        return ALIAS_MAP.getOrDefault(lowered, lowered);
    }

    /**
     * Normalize a collection of skill names into a Set.
     */
    public Set<String> normalizeAll(Set<String> skills) {
        return skills.stream()
                .map(this::normalize)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toSet());
    }
}
