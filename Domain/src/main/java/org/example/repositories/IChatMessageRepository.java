package org.example.repositories;

import org.example.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface IChatMessageRepository extends JpaRepository<Message, Long> {


}
