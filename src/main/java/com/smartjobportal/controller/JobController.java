package com.smartjobportal.controller;

import com.smartjobportal.dto.response.ApiResponse;
import com.smartjobportal.dto.response.JobResponse;
import com.smartjobportal.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    /**
     * GET /api/jobs?keyword=java&location=bangalore&jobType=FULL_TIME&page=0&size=10
     * PUBLIC endpoint - no auth required
     * Response: paginated list of active jobs
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<JobResponse>>> searchJobs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String jobType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "postedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<JobResponse> jobs = jobService.searchJobs(keyword, location, jobType, pageable);
        return ResponseEntity.ok(ApiResponse.success("Jobs retrieved", jobs));
    }

    /**
     * GET /api/jobs/{id}
     * PUBLIC endpoint - no auth required
     * Response: single job detail
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JobResponse>> getJobById(@PathVariable Long id) {
        JobResponse job = jobService.getJobById(id);
        return ResponseEntity.ok(ApiResponse.success("Job detail", job));
    }
}
