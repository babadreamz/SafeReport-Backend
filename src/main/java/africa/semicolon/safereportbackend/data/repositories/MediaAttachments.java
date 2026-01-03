package africa.semicolon.safereportbackend.data.repositories;

import africa.semicolon.safereportbackend.data.models.MediaAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MediaAttachments extends JpaRepository<MediaAttachment,String> {
    Optional<MediaAttachment> findFirstByHash(String hash);
}
