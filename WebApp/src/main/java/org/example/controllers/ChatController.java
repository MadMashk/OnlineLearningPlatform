package org.example.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.model.ChatRoom;
import org.example.model.Message;
import org.example.model.dto.ChatRoomDTO;
import org.example.model.dto.MessageDTO;
import org.example.services.ChatRoomService;
import org.example.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;


@Tag(name = "чаты", description = "управление чатом")
@RestController
@RequestMapping(path = ("/chats"))
public class ChatController {

    @Autowired
    private  ChatRoomService chatRoomService;
    @Autowired
    private  MessageService messageService;


    @Operation(summary = "показывает все чат-комнаты определенного пользователя, авторизовнного в системе")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    @GetMapping(value = "/")
    public List<ChatRoomDTO> getChatRooms(Principal principal){
      return chatRoomService.getAllChatRoomsOfUser(principal);

    }

    @Operation(summary = "показывает одну чат комнату по id получателя сообщений")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    @GetMapping(value = "/rooms/")
    public ChatRoom getOneChatRoom(@RequestParam("recipientId") @Parameter(description = "id получателя") Integer recipientId,
                                          Principal principal){
        return chatRoomService.getOneChatRoomOfUser(recipientId,principal);

    }

    @Operation(summary = "удаляет определенную чат-комнату")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    @DeleteMapping(value = "/{id}") //удалить чат-комнату
    public void deleteChatRoom(@PathVariable("id") @Parameter(description = "id чат-комнаты") Long chatRoomId,
                                           Principal principal) {
         chatRoomService.deleteChatRoom(chatRoomId, principal);
    }

    @Operation(summary = "отправка сообщения")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    @PostMapping(value = "/rooms/messages/")
    public Message sendPrivateMessage(@RequestParam("id") @Parameter(description = "id получателя") Integer recipientId,
                                      @Parameter(description = "сообщение") @RequestBody MessageDTO messageDTO,
                                      Principal principal){
        Message savedMessage = messageService.addMessage(principal, recipientId, messageDTO);
        chatRoomService.addChatRoom(recipientId, principal);
        return chatRoomService.addMessageToChatRoom(savedMessage, recipientId, principal);
    }

    @Operation(summary = "удаление сообщения из чат-комнаты")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    @DeleteMapping(value = "rooms/messages/{messageId}")
    public void deleteMessage(@RequestParam("recipientId") @Parameter(description = "id получателя") Integer recipientId,
                              @PathVariable("messageId") @Parameter(description = "id сообщения") Long messageId,
                              Principal principal){ //удалить сообщение из чат-комнаты
        chatRoomService.deleteMessageFromChatRoom(principal, recipientId, messageId);
    }

}