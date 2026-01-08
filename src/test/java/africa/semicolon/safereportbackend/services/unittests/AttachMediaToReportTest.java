package africa.semicolon.safereportbackend.services.unittests;

import africa.semicolon.safereportbackend.data.models.Report;
import africa.semicolon.safereportbackend.data.repositories.GhostReporters;
import africa.semicolon.safereportbackend.data.repositories.MediaAttachments;
import africa.semicolon.safereportbackend.data.repositories.Reports;
import africa.semicolon.safereportbackend.dtos.modeldtos.MediaAttachmentDto;
import africa.semicolon.safereportbackend.services.AnonymityServices;
import africa.semicolon.safereportbackend.services.GeoCodingServiceImpl;
import africa.semicolon.safereportbackend.services.MediaStorageService;
import africa.semicolon.safereportbackend.services.ReportServicesImpl;
import africa.semicolon.safereportbackend.utils.mappers.MediaAttachmentMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AttachMediaToReportTest {
    @Mock private MediaStorageService mediaStorageService;
    @Mock private MediaAttachments mediaAttachments;
    @Mock private Reports reports;
    @Mock private AnonymityServices anonymityServices;
    @Mock private GhostReporters ghostReporters;
    @Mock private GeoCodingServiceImpl geocodingServices;
    @Mock private MediaAttachmentMapper mediaAttachmentMapper;
    @InjectMocks
    private ReportServicesImpl reportServices;

    @Test
    void testThatMediaCanBeAttachedToReport_MockDataNotSavedToCloudinary(){
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "some fake data".getBytes()
        );
        String reportId = "12345";
        Report mockReport = new Report();
        when(reports.findById(reportId)).thenReturn(Optional.of(new Report()));
        when(anonymityServices.calculateFileHash(any())).thenReturn("fake-hash-123");
        when(mediaStorageService.uploadFile(any())).thenReturn("https://cloudinary.com/fake-url");
        MediaAttachmentDto mediaDto = reportServices.attachMediaToReport(reportId,mockFile);
        verify(mediaStorageService,times(1)).uploadFile(any());
        verify(mediaAttachments,times(1)).save(any());
    }
}
