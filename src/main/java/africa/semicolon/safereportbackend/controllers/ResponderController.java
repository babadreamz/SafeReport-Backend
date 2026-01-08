package africa.semicolon.safereportbackend.controllers;

import africa.semicolon.safereportbackend.data.models.EvidenceStatus;
import africa.semicolon.safereportbackend.data.models.ReportStatus;
import africa.semicolon.safereportbackend.dtos.modeldtos.MediaAttachmentDto;
import africa.semicolon.safereportbackend.dtos.modeldtos.ReportSummary;
import africa.semicolon.safereportbackend.dtos.requests.LoginRequest;
import africa.semicolon.safereportbackend.dtos.responses.LoginResponse;
import africa.semicolon.safereportbackend.services.ResponderServices;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/responder")
@RequiredArgsConstructor
public class ResponderController {
    @Autowired
    private final ResponderServices responderServices;
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request){
        return ResponseEntity.ok(responderServices.login(request));
    }
    @DeleteMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization")String authHeader){
        return ResponseEntity.ok(responderServices.logout(authHeader));
    }
    @GetMapping("/feed")
    public List<ReportSummary> getResponderFeed(@RequestParam String responderId,
                                                @RequestParam ReportStatus reportStatus){
        return responderServices.getResponderFeed(responderId,reportStatus);
    }
    @GetMapping("/media/{reportId}")
    public List<MediaAttachmentDto> getMediaForReport(@PathVariable String reportId, @RequestParam String responderUsername){
        return responderServices.getMediaForReport(reportId,responderUsername);
    }
    @PatchMapping("/flag/{mediaAttachmentId}")
    public void flagMediaEvidence(@PathVariable String mediaAttachmentId, @RequestParam String reason, @RequestParam EvidenceStatus evidenceStatus){
        responderServices.flagMediaEvidence(mediaAttachmentId,reason,evidenceStatus);
    }
    @PatchMapping("/update/{reportId}")
    public ReportSummary updateReportStatus(@PathVariable String reportId,@RequestParam ReportStatus newStatus,@RequestParam String responderId){
        return responderServices.updateReportStatus(reportId,newStatus,responderId);
    }
    @GetMapping("/summary/{reportId}")
    public ReportSummary getReportDetails(@PathVariable String reportId,@RequestParam String responderId){
        return responderServices.getReportDetails(reportId,responderId);
    }

}
