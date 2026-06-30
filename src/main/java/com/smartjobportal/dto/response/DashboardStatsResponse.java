package com.smartjobportal.dto.response;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardStatsResponse {
    private Long totalUsers;
    private Long totalRecruiters;
    private Long totalJobSeekers;
    private Long totalJobs;
    private Long activeJobs;
    private Long totalApplications;
    private Long pendingApplications;
    private Long blockedUsers;
}
