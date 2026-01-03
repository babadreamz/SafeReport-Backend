package africa.semicolon.safereportbackend.services;

import org.springframework.web.multipart.MultipartFile;

public interface MediaStorageService {
    String uploadFile(MultipartFile file);
}
