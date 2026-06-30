package com.smartjobportal.ai;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SkillNormalizer Unit Tests")
class SkillNormalizerTest {

    private SkillNormalizer normalizer;

    @BeforeEach
    void setUp() {
        normalizer = new SkillNormalizer();
    }

    @Test
    @DisplayName("Lowercases skill names")
    void lowercases() {
        assertThat(normalizer.normalize("Java")).isEqualTo("java");
        assertThat(normalizer.normalize("REACT")).isEqualTo("react");
    }

    @Test
    @DisplayName("Resolves known aliases")
    void resolvesAliases() {
        assertThat(normalizer.normalize("js")).isEqualTo("javascript");
        assertThat(normalizer.normalize("k8s")).isEqualTo("kubernetes");
        assertThat(normalizer.normalize("node")).isEqualTo("node.js");
        assertThat(normalizer.normalize("ts")).isEqualTo("typescript");
    }

    @Test
    @DisplayName("Trims whitespace")
    void trimsWhitespace() {
        assertThat(normalizer.normalize("  java  ")).isEqualTo("java");
    }

    @Test
    @DisplayName("Handles null gracefully")
    void handlesNull() {
        assertThat(normalizer.normalize(null)).isEmpty();
    }

    @Test
    @DisplayName("Normalizes a set and deduplicates")
    void normalizesSet() {
        Set<String> input  = Set.of("JS", "javascript", "  React  ", "k8s");
        Set<String> result = normalizer.normalizeAll(input);

        assertThat(result).contains("javascript", "react", "kubernetes");
        // JS and javascript both map to javascript — deduplicated
        assertThat(result.stream().filter(s -> s.equals("javascript")).count()).isEqualTo(1);
    }
}
