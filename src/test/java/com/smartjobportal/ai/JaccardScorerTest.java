package com.smartjobportal.ai;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JaccardScorer Unit Tests")
class JaccardScorerTest {

    private JaccardScorer scorer;

    @BeforeEach
    void setUp() {
        scorer = new JaccardScorer();
    }

    @Test
    @DisplayName("Perfect match — all required skills present")
    void perfectMatch() {
        Set<String> seeker   = Set.of("java", "spring boot", "mysql");
        Set<String> required = Set.of("java", "spring boot", "mysql");

        JaccardScorer.ScoreDetail result = scorer.score(seeker, required);

        assertThat(result.getScore()).isEqualTo(100.0);
        assertThat(result.getMatchedSkills()).containsExactlyInAnyOrder("java", "spring boot", "mysql");
        assertThat(result.getMissingSkills()).isEmpty();
        assertThat(result.getMatchedCount()).isEqualTo(3);
        assertThat(result.getTotalRequired()).isEqualTo(3);
    }

    @Test
    @DisplayName("Zero match — no overlapping skills")
    void zeroMatch() {
        Set<String> seeker   = Set.of("python", "django");
        Set<String> required = Set.of("java", "spring boot", "mysql");

        JaccardScorer.ScoreDetail result = scorer.score(seeker, required);

        assertThat(result.getScore()).isEqualTo(0.0);
        assertThat(result.getMatchedSkills()).isEmpty();
        assertThat(result.getMissingSkills()).containsExactlyInAnyOrder("java", "spring boot", "mysql");
    }

    @Test
    @DisplayName("Partial match — 50% coverage")
    void partialMatch() {
        Set<String> seeker   = Set.of("java", "spring boot", "python");
        Set<String> required = Set.of("java", "spring boot", "mysql", "docker");

        JaccardScorer.ScoreDetail result = scorer.score(seeker, required);

        assertThat(result.getScore()).isEqualTo(50.0);
        assertThat(result.getMatchedSkills()).containsExactlyInAnyOrder("java", "spring boot");
        assertThat(result.getMissingSkills()).containsExactlyInAnyOrder("mysql", "docker");
    }

    @Test
    @DisplayName("Empty required skills — returns 100%")
    void emptyRequiredSkills() {
        Set<String> seeker   = Set.of("java", "react");
        Set<String> required = Set.of();

        JaccardScorer.ScoreDetail result = scorer.score(seeker, required);

        assertThat(result.getScore()).isEqualTo(100.0);
    }

    @Test
    @DisplayName("Extra seeker skills do not reduce score")
    void extraSeekerSkillsNopenalty() {
        Set<String> seeker   = Set.of("java", "spring boot", "mysql", "docker", "kubernetes", "aws");
        Set<String> required = Set.of("java", "spring boot", "mysql");

        JaccardScorer.ScoreDetail result = scorer.score(seeker, required);

        assertThat(result.getScore()).isEqualTo(100.0);
    }

    @Test
    @DisplayName("Score is correctly rounded to 2 decimal places")
    void scoreRounding() {
        Set<String> seeker   = Set.of("java");
        Set<String> required = Set.of("java", "spring boot", "mysql");

        JaccardScorer.ScoreDetail result = scorer.score(seeker, required);

        // 1/3 * 100 = 33.33
        assertThat(result.getScore()).isEqualTo(33.33);
    }
}
