package services;

import model.AppUser;
import model.Message;
import model.constants.MessageStatus;
import model.dto.MessageDTO;
import model.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repositories.IAppUserRepository;
import repositories.IChatMessageRepository;

import java.security.Principal;
import java.util.Date;

@Service
public class MessageService {
    @Autowired
    private IChatMessageRepository chatMessageRepository;
    @Autowired
    private IAppUserRepository appUserRepository;
    public MessageService() {

    }


    @Transactional
    public Message addMessage(Principal principal, int recipientId, MessageDTO messageDTO) {
        AppUser currentUser = appUserRepository.findByUserName(principal.getName())
                .orElseThrow(() -> new NotFoundException("user not found with user name = " + principal.getName()));
        Message message = new Message();
        message.setSenderId(currentUser.getId());
        message.setRecipientId(recipientId);
        message.setTime(new Date());
        message.setContent(messageDTO.getContent());
        message.setMessageStatus(MessageStatus.RECEIVED);
        return chatMessageRepository.save(message);
    }
}
