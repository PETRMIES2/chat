package com.sope.domain.chat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.sope.domain.EntityRepository;
import com.sope.domain.firebase.MessageService;

@RunWith(MockitoJUnitRunner.class)
public class ChatServiceTest {

    @Mock
    private ChatRepository chatRepository;
    @Mock
    private EntityRepository entityRepository;
    @Mock
    private MessageService messageService;

    @InjectMocks
    private ChatService chatService;
    
    private ArgumentCaptor<Chat> chatArgumentCaptor = ArgumentCaptor.forClass(Chat.class);

    @Test
    public void shouldCreateNewChatWhenNoneExists() {

    }

    @Test(expected = ChatTopicNotFound.class)
    public void shouldThrowErrorWhenTopicNotFoundWhenRenaming() {
        when(chatRepository.getChatByChatNumber(any(Long.class))).thenReturn(Optional.<Chat> empty());
        chatService.renameHeader(123, "wer", "test");
    }
    
    @Test
    public void shouldRenameChat() {
        when(chatRepository.getChatByChatNumber(any(Long.class))).thenReturn(Optional.of(new Chat()));
        chatService.renameHeader(123, "wer", "test");
        verify(entityRepository).save(isA(Chat.class));
    }

    @Test
    public void shouldRenameChatButCutOffCharsThatAreOverTheLimit() {
        when(chatRepository.getChatByChatNumber(any(Long.class))).thenReturn(Optional.of(new Chat()));
        chatService.renameHeader(123, "wer 13241 åäö2 123 123 123 123132123 ", "test");
        verify(entityRepository).save(chatArgumentCaptor.capture());
        
        assertThat(chatArgumentCaptor.getValue().getHeader()).isEqualTo("wer 13241 åäö2 123 1");
    }
}
