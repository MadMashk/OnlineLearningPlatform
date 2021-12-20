package org.example.repositories;

import org.example.model.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IAttachmentRepository extends JpaRepository<Attachment, Integer> {
}
