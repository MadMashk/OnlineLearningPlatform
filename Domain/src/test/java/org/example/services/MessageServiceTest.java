package org.example.services;


import org.example.model.AppUser;
import org.example.model.Message;
import org.example.model.constants.MessageStatus;
import org.example.model.dto.MessageDTO;
import org.example.model.exceptions.NotFoundException;
import org.example.repositories.IAppUserRepository;
import org.example.repositories.IChatMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class MessageServiceTest{

    @InjectMocks
    private MessageService messageService;
    @Mock
    private IAppUserRepository appUserRepository;
    @Mock
    private IChatMessageRepository chatMessageRepository;
    @Mock
    private Principal principal;
    @Captor
    private ArgumentCaptor<Message> messageArgumentCaptor;

    AppUser user;

    Message message;

    MessageDTO messageDTO;

    @BeforeEach
    void init(){
        user=new AppUser();
        user.setId(1);
        user.setUserName("Masha");

        message=new Message();
        message.setContent("hello");
        message.setSenderId(1);
        message.setRecipientId(2);
        message.setTime(new Date());
        message.setMessageStatus(MessageStatus.RECEIVED);

        messageDTO=new MessageDTO();
        messageDTO.setContent("context");
    }

    @Test
    void addMessageShouldAddMessage(){
        when(principal.getName()).thenReturn("Masha");
        when(appUserRepository.findByUserName("Masha")).thenReturn(Optional.of(user));

        messageService.addMessage(principal,2,messageDTO);

        verify(chatMessageRepository).save(messageArgumentCaptor.capture());

        assertThat(messageArgumentCaptor.getValue().getRecipientId(),is(2));
        assertThat(messageArgumentCaptor.getValue().getSenderId(),is(1));
        assertThat(messageArgumentCaptor.getValue().getContent(),equalTo(messageDTO.getContent()));
    }

    @Test
    void userNotFound(){
        assertThatThrownBy(()->{messageService.addMessage(principal,2,messageDTO);}).isInstanceOf(NotFoundException.class);
    }
}
