package africa.semicolon.safereportbackend.services;

import africa.semicolon.safereportbackend.exceptions.CloudinaryUploadException;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class CloudinaryServices implements MediaStorageService{
    private final Cloudinary cloudinary;

    @Override
    public String uploadFile(MultipartFile file) {
        try{
            if(file.isEmpty()){
                throw new CloudinaryUploadException("File is empty");
            }
            String fileName = "safereport_img" + UUID.randomUUID().toString();
            Map<String, Object> params = ObjectUtils.asMap(
                    "public_id",fileName,
                    "resource_type","auto"
            );
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),params);
            return (String) uploadResult.get("secure_url");
        }catch (Exception e){
            log.error("Cloudinary Upload Error: {}", e.getMessage());
            throw new CloudinaryUploadException("Failed to upload file to cloudinary");
        }
    }
}
