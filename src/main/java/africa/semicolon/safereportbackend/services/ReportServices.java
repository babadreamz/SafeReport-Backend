package africa.semicolon.safereportbackend.services;

import africa.semicolon.safereportbackend.data.models.Agency;
import africa.semicolon.safereportbackend.data.models.ReportStatus;
import africa.semicolon.safereportbackend.data.models.ResponderUnit;
import africa.semicolon.safereportbackend.dtos.modeldtos.MediaAttachmentDto;
import africa.semicolon.safereportbackend.dtos.requests.ReportRequest;
import africa.semicolon.safereportbackend.dtos.responses.ReportResponse;
import org.springframework.web.multipart.MultipartFile;



public interface ReportServices {
    ReportResponse submitReport(String deviceSignature, ReportRequest request);
    ReportStatus checkReportStatus(String reportId);
    MediaAttachmentDto attachMediaToReport(String reportId, MultipartFile file);
    ResponderUnit findNearestResponder(Agency agency, double incidentLatitude, double incidentLongitude);
}
