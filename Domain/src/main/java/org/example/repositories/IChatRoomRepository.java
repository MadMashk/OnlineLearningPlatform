package org.example.repositories;

import org.example.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface IChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("SELECT cr FROM ChatRoom cr where cr.senderId = ?1 and  cr.recipientId =?2")
    Optional<ChatRoom> getOneBySenderIdAndRecipient(Integer senderId, Integer RecipientId);

    @Query ("SELECT cr FROM  ChatRoom  cr where cr.id =?1 and cr.senderId =?2")
    Optional<ChatRoom> getOneByUserIdAndId(Long id, Integer id1);
}
