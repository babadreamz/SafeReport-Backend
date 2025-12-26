package africa.semicolon.safereportbackend.data.repositories;

import africa.semicolon.safereportbackend.data.models.MediaAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaAttachments extends JpaRepository<MediaAttachment,String> {
}
