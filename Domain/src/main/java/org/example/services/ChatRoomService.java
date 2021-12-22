package org.example.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.AppUser;
import org.example.model.ChatRoom;
import org.example.model.Message;
import org.example.model.constants.MessageStatus;
import org.example.model.dto.ChatRoomDTO;
import org.example.model.exceptions.NotFoundException;
import org.example.repositories.IAppUserRepository;
import org.example.repositories.IChatRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChatRoomService {
@Autowired
private IAppUserRepository appUserRepository;
@Autowired
private IChatRoomRepository chatRoomRepository;

    private static final Logger logger = LogManager.getLogger(ChatRoomService.class);

    @Transactional
    public Message addMessageToChatRoom(Message message, Integer recipientId, Principal principal){ //добавить сообщение в чат комнату
        AppUser currentUser = appUserRepository.findByUserName(principal.getName())
                .orElseThrow(() -> new NotFoundException("user not found with user name = " + principal.getName()));
       ChatRoom chatRoomSender = chatRoomRepository.getOneBySenderIdAndRecipient(currentUser.getId(),recipientId)
               .orElseThrow(() -> new NotFoundException("chat room not found with sender id = " + currentUser.getId() + " and recipient id "+ recipientId));
       ChatRoom chatRoomRecipient = chatRoomRepository.getOneBySenderIdAndRecipient(recipientId,currentUser.getId())
               .orElseThrow(() -> new NotFoundException("chat room not found with sender id = " + recipientId + " and recipient id "+ currentUser.getId()));
        chatRoomSender.getMessages().add(message);
       chatRoomRecipient.getMessages().add(message);
       chatRoomRepository.save(chatRoomSender);
       chatRoomRepository.save(chatRoomRecipient);
       return message;
    }

    @Transactional
    public void addChatRoom(Integer idRecipient, Principal principal){ //создать чат-комнату
        AppUser currentUser = appUserRepository.findByUserName(principal.getName())
                .orElseThrow(() -> new NotFoundException("user not found with user name = " + principal.getName()));
        AppUser userRecipient = appUserRepository.findById(idRecipient)
                .orElseThrow(() -> new NotFoundException("user not found with id = " + idRecipient));

        if (chatRoomRepository.getOneBySenderIdAndRecipient(currentUser.getId(),userRecipient.getId()).isEmpty()
                || chatRoomRepository.getOneBySenderIdAndRecipient(userRecipient.getId(), currentUser.getId()).isEmpty()) {
            List<Message> messageListSender = new ArrayList<>();
            List<Message> messageListRecipient = new ArrayList<>();
            ChatRoom chatRoomSender = new ChatRoom();
            chatRoomSender.setSenderId(currentUser.getId());
            chatRoomSender.setRecipientId(userRecipient.getId());
            chatRoomSender.setMessages(messageListSender);
            ChatRoom chatRoomRecipient = new ChatRoom();
            chatRoomRecipient.setSenderId(userRecipient.getId());
            chatRoomRecipient.setRecipientId(currentUser.getId());
            chatRoomRecipient.setMessages(messageListRecipient);
            ChatRoom savedChatRoomSender = chatRoomRepository.save(chatRoomSender);
            ChatRoom savedChatRoomRecipient = chatRoomRepository.save(chatRoomRecipient);
            logger.info("chat room with id = " + savedChatRoomSender.getId() + " is created");
            logger.info("chat room with id = " + savedChatRoomRecipient.getId() + " is created");
        }
    }

    @Transactional(readOnly = true)
    public List<ChatRoomDTO> getAllChatRoomsOfUser(Principal principal) { //все чат-комнаты пользователя
        AppUser user = appUserRepository.findByUserName(principal.getName())
                .orElseThrow(() -> new NotFoundException("user not found with user name = " + principal.getName()));
        List<ChatRoomDTO> chatRoomDTOS;
        chatRoomDTOS = new ArrayList<>();
        chatRoomRepository.findAll().forEach(chatRoom -> {
            if (chatRoom.getSenderId().equals(user.getId())){
                AppUser appUserRecipient = appUserRepository.findById(chatRoom.getRecipientId())
                        .orElseThrow(() -> new NotFoundException("user not found with id " + chatRoom.getRecipientId()));
                chatRoomDTOS.add(new ChatRoomDTO(appUserRecipient.getUserName(),
                        chatRoom.getMessages().get(chatRoom.getMessages().size()-1)));
            }
        });
        return chatRoomDTOS;
    }

    @Transactional
    public ChatRoom getOneChatRoomOfUser(Integer recipientId, Principal principal) { //чат-комната по id получателя
        AppUser user = appUserRepository.findByUserName(principal.getName())
                .orElseThrow(() -> new NotFoundException("user not found with user name = " + principal.getName()));
        ChatRoom chatRoom = chatRoomRepository.getOneBySenderIdAndRecipient(user.getId(), recipientId)
                .orElseThrow(() -> new NotFoundException("chat room not found with user id = " + user.getId() + " and recipient id "+ recipientId));
        List<Message> chatRoomMessages = chatRoom.getMessages();
        chatRoomMessages.forEach(message -> {
            if (message.getSenderId().equals(recipientId)) {
                message.setMessageStatus(MessageStatus.READ);
            }
        });
        chatRoom.setMessages(chatRoomMessages);
        return chatRoomRepository.save(chatRoom);
    }

    @Transactional
    public void deleteChatRoom(Long id, Principal principal) { //удалить чат-комнату
        AppUser user = appUserRepository.findByUserName(principal.getName())
                .orElseThrow(() -> new NotFoundException("user not found with user name = " + principal.getName()));
        ChatRoom chatRoom = chatRoomRepository.getOneByUserIdAndId(id, user.getId())
                .orElseThrow(() -> new NotFoundException("chat room not found with user id = " + user.getId() + " and with chat room id "+ id));
        chatRoomRepository.delete(chatRoom);
        logger.info("chatRoom " + chatRoom.getId() + " is deleted");

    }

    @Transactional
    public void deleteMessageFromChatRoom(Principal principal, Integer recipientId, Long messageId) { //удалить сообщение из чат-комнаты
        AppUser currentUser = appUserRepository.findByUserName(principal.getName())
                .orElseThrow(() -> new NotFoundException("user not found with user name = " + principal.getName()));
        ChatRoom chatRoom = chatRoomRepository.getOneBySenderIdAndRecipient(currentUser.getId(), recipientId)
                .orElseThrow(() -> new NotFoundException("chat room not found with user id = " + currentUser.getId() + " and recipient id "+ recipientId));
        List<Message> chatRoomMessages = chatRoom.getMessages();
        System.out.println(chatRoomMessages.size());
        for (int i = 0; i < chatRoomMessages.size(); i++) {
            if (chatRoomMessages.get(i).getId().equals(messageId)){
                chatRoomMessages.remove(chatRoomMessages.get(i));
                logger.info("message with id = "+ messageId + " is removed from the chat room " + chatRoom.getId());
            }
        }
        chatRoom.setMessages(chatRoomMessages);
        chatRoomRepository.save(chatRoom);
    }
}
