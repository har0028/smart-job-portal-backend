package com.smartjobportal.ai;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RecommendationExplainer Unit Tests")
class RecommendationExplainerTest {

    private RecommendationExplainer explainer;

    @BeforeEach
    void setUp() {
        explainer = new RecommendationExplainer();
    }

    @Test
    @DisplayName("Perfect match explanation contains 'all' keyword")
    void perfectMatchExplanation() {
        MatchResult result = MatchResult.builder()
                .skillMatchScore(100.0)
                .matchedCount(3)
                .totalRequiredSkills(3)
                .matchedSkills(Set.of("java", "spring boot", "mysql"))
                .missingSkills(Set.of())
                .build();

        String explanation = explainer.explain(result);

        assertThat(explanation).containsIgnoringCase("all");
        assertThat(explanation).contains("100.0%");
    }

    @Test
    @DisplayName("Partial match explanation lists missing skills")
    void partialMatchListsMissing() {
        MatchResult result = MatchResult.builder()
                .skillMatchScore(50.0)
                .matchedCount(2)
                .totalRequiredSkills(4)
                .matchedSkills(Set.of("java", "spring boot"))
                .missingSkills(Set.of("kubernetes", "docker"))
                .build();

        String explanation = explainer.explain(result);

        assertThat(explanation).containsAnyOf("kubernetes", "Kubernetes");
        assertThat(explanation).containsAnyOf("docker", "Docker");
        assertThat(explanation).contains("50.0%");
    }

    @Test
    @DisplayName("Zero match explanation mentions low match")
    void zeroMatchExplanation() {
        MatchResult result = MatchResult.builder()
                .skillMatchScore(0.0)
                .matchedCount(0)
                .totalRequiredSkills(4)
                .matchedSkills(Set.of())
                .missingSkills(Set.of("java", "spring boot", "mysql", "docker"))
                .build();

        String explanation = explainer.explain(result);

        assertThat(explanation).containsIgnoringCase("low");
        assertThat(explanation).contains("0.0%");
    }
}
