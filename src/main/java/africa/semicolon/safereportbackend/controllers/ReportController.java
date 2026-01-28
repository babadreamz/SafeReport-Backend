package africa.semicolon.safereportbackend.controllers;

import africa.semicolon.safereportbackend.data.models.ReportStatus;
import africa.semicolon.safereportbackend.dtos.modeldtos.MediaAttachmentDto;
import africa.semicolon.safereportbackend.dtos.modeldtos.ReportSummary;
import africa.semicolon.safereportbackend.dtos.requests.ReportRequest;
import africa.semicolon.safereportbackend.dtos.responses.ReportResponse;
import africa.semicolon.safereportbackend.services.ReportServices;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/report")
@AllArgsConstructor
public class ReportController {
    private final ReportServices reportServices;

    @PostMapping("/submit")
    public ResponseEntity<ReportResponse> submitReport(
            @RequestHeader(value = "X-Device-Signature") String deviceSignature,
            @RequestBody ReportRequest reportRequest){
        return ResponseEntity.ok(reportServices.submitReport(deviceSignature, reportRequest));
    }
    @GetMapping("/status/{reportId}")
    public ResponseEntity<ReportStatus> checkReportStatus(@PathVariable String reportId){
        return ResponseEntity.ok(reportServices.checkReportStatus(reportId));
    }
    @PostMapping("/media/attach/{reportId}")
    public ResponseEntity<MediaAttachmentDto> attachMediaToReport(@PathVariable String reportId, @RequestParam MultipartFile file){
        return ResponseEntity.ok(reportServices.attachMediaToReport(reportId,file));
    }
    @GetMapping("/public")
    public List<ReportSummary> publicReports(){
        return reportServices.findPublicReports();
    }
}
