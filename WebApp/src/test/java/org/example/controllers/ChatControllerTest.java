package org.example.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.example.model.AppUser;
import org.example.model.ChatRoom;
import org.example.model.Message;
import org.example.model.dto.ChatRoomDTO;
import org.example.model.dto.MessageDTO;
import org.example.model.exceptions.NotFoundException;
import org.example.services.ChatRoomService;
import org.example.services.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ChatRoomService chatRoomService;
    @MockBean
    private MessageService messageService;

    ArgumentCaptor<Principal> principalArgumentCaptor1 = ArgumentCaptor.forClass(Principal.class);

    ChatRoom chatRoom1;
    ChatRoom chatRoom2;

    Message message;

    AppUser appUser1;
    AppUser appUser2;

    List<ChatRoom> chatRooms;
    List<ChatRoomDTO> chatRoomDTOS;

    ChatRoomDTO chatRoomDTO;
    MessageDTO messageDTO;

    @BeforeEach
    void setUp() {
        chatRoom1 = new ChatRoom();
        chatRoom1.setId((long)1);
        chatRoom1.setSenderId(1);
        chatRoom1.setRecipientId(2);

        chatRoom2 = new ChatRoom();
        chatRoom2.setId((long)2);
        chatRoom2.setSenderId(2);
        chatRoom2.setRecipientId(1);

        message = new Message();
        message.setId((long)1);

        appUser1 = new AppUser();
        appUser1.setId(1);
        appUser1.setUserName("MashaUser");

        appUser2 = new AppUser();
        appUser2.setId(2);
        appUser2.setUserName("PashaUser");

        chatRooms = new ArrayList<>();
        chatRooms.add(chatRoom1);

        chatRoomDTO = new ChatRoomDTO(appUser2.getUserName(),message);

        messageDTO = new MessageDTO();
        messageDTO.setContent("hi");

        chatRoomDTOS = new ArrayList<>();
        chatRoomDTOS.add(chatRoomDTO);

    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "MashaUser", password = "12345", roles = "STUDENT")
    void getChatRoomsSuccessfully() {
        when(chatRoomService.getAllChatRoomsOfUser(any(Principal.class))).thenReturn(chatRoomDTOS);

        mockMvc.perform(MockMvcRequestBuilders.get("/chats/")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].lastMessage.id", equalTo(1)));

        verify(chatRoomService).getAllChatRoomsOfUser(principalArgumentCaptor1.capture());
        assertThat(principalArgumentCaptor1.getValue().getName(), equalTo("MashaUser"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "MashaUser", password = "12345", roles = "STUDENT")
    void getChatRoomsFail404NotFound() {
        when(chatRoomService.getAllChatRoomsOfUser(any(Principal.class))).thenThrow(new NotFoundException("NotFound"));

        mockMvc.perform(MockMvcRequestBuilders.get("/chats/")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(chatRoomService).getAllChatRoomsOfUser(principalArgumentCaptor1.capture());
        assertThat(principalArgumentCaptor1.getValue().getName(), equalTo("MashaUser"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "MashaUser", password = "12345", roles = "STUDENT")
    void getOneChatRoom1Successfully() {
        when(chatRoomService.getOneChatRoomOfUser(eq(2),any(Principal.class))).thenReturn(chatRoom1);

        mockMvc.perform(MockMvcRequestBuilders.get("/chats/rooms/")
                .contentType(MediaType.APPLICATION_JSON)
                .param("recipientId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)));

        verify(chatRoomService).getOneChatRoomOfUser(eq(2), principalArgumentCaptor1.capture());
        assertThat(principalArgumentCaptor1.getValue().getName(), equalTo("MashaUser"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "MashaUser", password = "12345", roles = "STUDENT")
    void getOneChatRoomFail404NotFound() {
        when(chatRoomService.getOneChatRoomOfUser(eq(2),any(Principal.class))).thenThrow(new NotFoundException("NOT FOUND"));

        mockMvc.perform(MockMvcRequestBuilders.get("/chats/rooms/")
                .contentType(MediaType.APPLICATION_JSON)
                .param("recipientId", "2"))
                .andExpect(status().isNotFound());

        verify(chatRoomService).getOneChatRoomOfUser(eq(2), principalArgumentCaptor1.capture());
        assertThat(principalArgumentCaptor1.getValue().getName(), equalTo("MashaUser"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "MashaUser", password = "12345", roles = "STUDENT")
    void deleteChatRoom1Successfully() {
        mockMvc.perform(MockMvcRequestBuilders.delete("/chats/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(chatRoomService).deleteChatRoom(eq((long)1), principalArgumentCaptor1.capture());
        assertThat(principalArgumentCaptor1.getValue().getName(), equalTo("MashaUser"));
    }

    @Test
    @SneakyThrows
    @WithMockUser(username = "MashaUser", password = "12345", roles = "STUDENT")
    void shouldSendPrivateMessageToUser2() {
        when(messageService.addMessage(any(Principal.class), eq(2), eq(messageDTO))).thenReturn(message);
        when(chatRoomService.addMessageToChatRoom(eq(message), eq(2), any(Principal.class))).thenReturn(message);

        mockMvc.perform(MockMvcRequestBuilders.post("/chats/rooms/messages/")
                .contentType(MediaType.APPLICATION_JSON)
                .param("id", "2")
                .content(asJsonString(messageDTO)))
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(status().isOk());
        verify(messageService).addMessage(principalArgumentCaptor1.capture(), eq(2), eq(messageDTO));
        verify(chatRoomService).addMessageToChatRoom(eq(message), eq(2), principalArgumentCaptor1.capture());

        assertThat(principalArgumentCaptor1.getValue().getName(), equalTo("MashaUser"));
        assertThat(principalArgumentCaptor1.getValue().getName(),equalTo("MashaUser"));
    }


    @Test
    @SneakyThrows
    @WithMockUser(username = "MashaUser", password = "12345", roles = "STUDENT")
    void shouldDeleteMessage1FromChatRoomWithRecipient2() {
        mockMvc.perform(MockMvcRequestBuilders.delete("/chats/rooms/messages/{messageId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .param("recipientId", "2"))
                .andExpect(status().isOk());

        verify(chatRoomService).deleteMessageFromChatRoom(principalArgumentCaptor1.capture(), eq(2), eq((long)1));

        assertThat(principalArgumentCaptor1.getValue().getName(), equalTo("MashaUser"));
    }


    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}