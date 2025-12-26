package africa.semicolon.safereportbackend.services;

import africa.semicolon.safereportbackend.data.models.ReportStatus;
import africa.semicolon.safereportbackend.dtos.requests.ReportRequest;
import africa.semicolon.safereportbackend.dtos.responses.ReportResponse;

public class ReportServicesImpl implements ReportServices {
    @Override
    public ReportResponse submitReport(ReportRequest request) {
        return null;
    }

    @Override
    public ReportStatus checkReportStatus(String reportId) {
        return null;
    }

//    private final StringRedisTemplate redisTemplate;
//    private final ReportRepository reportRepository;
//
//    // CONFIG: Max 5 reports per hour (Adjust as needed)
//    private static final int MAX_REPORTS = 5;
//    private static final long REPORT_WINDOW_HOURS = 1;
//
//    public ReportServiceImpl(StringRedisTemplate redisTemplate, ReportRepository reportRepository) {
//        this.redisTemplate = redisTemplate;
//        this.reportRepository = reportRepository;
//    }
//
//    public void submitReport(ReportRequest request, String ghostId) {
//
//        // 1. CHECK RATE LIMIT (Spam Check)
//        // We check against the GhostID so a specific user cannot flood the system
//        checkReportRateLimit(ghostId);
//
//        // 2. Process and Save Report
//        Report report = new Report();
//        report.setGhostId(ghostId);
//        report.setContent(request.getContent());
//        // ... map other fields ...
//
//        reportRepository.save(report);
//    }
//
//    /**
//     * Private Helper to limit report frequency.
//     * Prevents a single user from sending too many reports in a short time.
//     */
//    private void checkReportRateLimit(String ghostId) {
//        // Different Key: "spam:reporting:{ghostId}"
//        String redisKey = "spam:reporting:" + ghostId;
//
//        Long currentCount = redisTemplate.opsForValue().increment(redisKey);
//
//        if (currentCount == null) {
//            throw new RuntimeException("Error connecting to rate limiter");
//        }
//
//        // Set expiry on first request
//        if (currentCount == 1) {
//            redisTemplate.expire(redisKey, REPORT_WINDOW_HOURS, TimeUnit.HOURS);
//        }
//
//        if (currentCount > MAX_REPORTS) {
//            throw new RuntimeException("You are sending reports too quickly. Please wait a while.");
//        }
//    }
//}
}
