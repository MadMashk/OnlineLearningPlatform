package org.example.services;


import org.example.model.AppUser;
import org.example.model.ChatRoom;
import org.example.model.Message;
import org.example.model.dto.ChatRoomDTO;
import org.example.repositories.IAppUserRepository;
import org.example.repositories.IChatRoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatRoomServiceTest {

    @Mock
    private IAppUserRepository appUserRepository;
    @Mock
    private IChatRoomRepository iChatRoomRepository;
    @InjectMocks
    private ChatRoomService chatRoomService;
    @Mock
    private Principal principal;

    ArgumentCaptor<ChatRoom> chatRoomArgumentCaptor1 = ArgumentCaptor.forClass(ChatRoom.class);

    ChatRoom chatRoom1;
    ChatRoom chatRoom2;

    ChatRoomDTO chatRoomDTO1;

    Message message1;

    AppUser appUser1;
    AppUser appUser2;

    List<Message> messages1;
    List<Message> messages2;
    List<ChatRoom> chatRooms;
    List<ChatRoomDTO> chatRoomDTOS;

    @BeforeEach
    void setUp() {
        chatRoom1 = new ChatRoom();
        chatRoom1.setId((long) 1);
        chatRoom1.setRecipientId(2);
        chatRoom1.setSenderId(1);

        chatRoom2 = new ChatRoom();
        chatRoom2.setId((long) 2);
        chatRoom2.setSenderId(2);
        chatRoom2.setRecipientId(1);

        appUser1= new AppUser();
        appUser1.setId(1);
        appUser1.setUserName("MashaUser");

        appUser2 = new AppUser();
        appUser2.setId(2);
        appUser2.setUserName("PashaUser");

        messages1 = new ArrayList<>();
        messages2 = new ArrayList<>();

        message1 = new Message();
        message1.setId((long) 1);

        chatRoom1.setMessages(messages1);
        chatRoom2.setMessages(messages2);

        chatRoomDTO1 = new ChatRoomDTO(appUser2.getUserName(), message1);

        chatRoomDTOS= new ArrayList<>();
        chatRoomDTOS.add(chatRoomDTO1);

        chatRooms = new ArrayList<>();
        chatRooms.add(chatRoom1);
        chatRooms.add(chatRoom2);
    }

    @Test
    public void addMessageToChatRoomShouldAddMessage1ToChatRoom1AndToChatRoom2() {
        when(principal.getName()).thenReturn("MashaUser");
        when(appUserRepository.findByUserName("MashaUser")).thenReturn(Optional.of(appUser1));
        when(iChatRoomRepository.getOneBySenderIdAndRecipient(1,2)).thenReturn(Optional.of(chatRoom1));
        when(iChatRoomRepository.getOneBySenderIdAndRecipient(2,1)).thenReturn(Optional.of(chatRoom2));
        when(iChatRoomRepository.save(chatRoom1)).thenReturn(chatRoom1);
        when(iChatRoomRepository.save(chatRoom2)).thenReturn(chatRoom2);


        Message message = chatRoomService.addMessageToChatRoom(message1,2,principal);

        verify(iChatRoomRepository, times(2)).save(chatRoomArgumentCaptor1.capture());
        List<ChatRoom> chatRooms = chatRoomArgumentCaptor1.getAllValues();

        assertTrue(chatRooms.get(0).getMessages().contains(message1));
        assertThat(chatRooms.get(0).getSenderId(), equalTo(1));
        assertThat(chatRooms.get(0).getRecipientId(), equalTo(2));
        assertTrue(chatRooms.get(1).getMessages().contains(message1));
        assertThat(chatRooms.get(1).getSenderId(), equalTo(2));
        assertThat(chatRooms.get(1).getRecipientId(), equalTo(1));
        assertThat(message, equalTo(message1));
    }

    @Test
    public void addChatRoomShouldAddChatRoom1andChatRoom2() {
        when(principal.getName()).thenReturn("MashaUser");
        when(appUserRepository.findByUserName("MashaUser")).thenReturn(Optional.of(appUser1));
        when(appUserRepository.findById(2)).thenReturn(Optional.of(appUser2));
        when(iChatRoomRepository.save(any())).thenReturn(chatRoom1, chatRoom2);


        chatRoomService.addChatRoom(2, principal);

        verify(iChatRoomRepository, times(2)).save(chatRoomArgumentCaptor1.capture());

        List<ChatRoom> chatRooms = chatRoomArgumentCaptor1.getAllValues();

        assertThat(chatRooms.get(0).getSenderId(), equalTo(1));
        assertThat(chatRooms.get(0).getRecipientId(), equalTo(2));
    }

    @Test
    public void getAllChatRoomsOfUserShouldAddChatRoomDTOS() {
        messages1.add(message1);

        when(principal.getName()).thenReturn("MashaUser");
        when(appUserRepository.findByUserName("MashaUser")).thenReturn(Optional.of(appUser1));
        when(iChatRoomRepository.findAll()).thenReturn(chatRooms);
        when(appUserRepository.findById(2)).thenReturn(Optional.of(appUser2));

        List<ChatRoomDTO> chatRoomDTOS1 = chatRoomService.getAllChatRoomsOfUser(principal);

        assertThat(chatRoomDTOS1, equalTo(chatRoomDTOS));
    }

    @Test
    public void getOneChatRoomOfUserShouldGetChatRoomOfClient1AsSenderAndClient2AsRecipient() {

        when(principal.getName()).thenReturn("MashaUser");
        when(appUserRepository.findByUserName("MashaUser")).thenReturn(Optional.of(appUser1));
        when(iChatRoomRepository.getOneBySenderIdAndRecipient(1,2)).thenReturn(Optional.of(chatRoom1));

        chatRoomService.getOneChatRoomOfUser(2, principal);

        verify(iChatRoomRepository).save(chatRoomArgumentCaptor1.capture());
        assertThat(chatRoomArgumentCaptor1.getValue().getSenderId(), equalTo(1));
        assertThat(chatRoomArgumentCaptor1.getValue().getRecipientId(), equalTo(2));
    }

    @Test
    public void deleteChatRoomShouldDeleteChatRoom1() {
        when(principal.getName()).thenReturn("MashaUser");
        when(appUserRepository.findByUserName("MashaUser")).thenReturn(Optional.of(appUser1));
        when(iChatRoomRepository.getOneByUserIdAndId((long) 1,1)).thenReturn(Optional.of(chatRoom1));

        chatRoomService.deleteChatRoom((long) 1, principal);

        verify(iChatRoomRepository).delete(chatRoomArgumentCaptor1.capture());
        assertThat(chatRoomArgumentCaptor1.getValue(), equalTo(chatRoom1));
    }

    @Test
    public void ShouldDeleteMessage1FromChatRoom1() {
        messages1.add(message1);
        when(principal.getName()).thenReturn("MashaUser");
        when(appUserRepository.findByUserName("MashaUser")).thenReturn(Optional.of(appUser1));
        when(iChatRoomRepository.getOneBySenderIdAndRecipient(1,2)).thenReturn(Optional.of(chatRoom1));

        chatRoomService.deleteMessageFromChatRoom(principal,2,(long) 1);

        verify(iChatRoomRepository).save(chatRoomArgumentCaptor1.capture());

        assertTrue(chatRoomArgumentCaptor1.getValue().getMessages().isEmpty());

    }

}
