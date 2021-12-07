package repositories;

import model.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IAttachmentRepository extends JpaRepository<Attachment, Integer> {
}
