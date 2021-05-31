package com.sope.cache;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;

import com.sope.SopeCache;
import com.sope.domain.firebase.MessageToChat;

public class SopeCacheTest {

	@Before
	public void setup() {
		SopeCache.chatUserCache.removeAll();
	}

	@Test
	public void shouldReturnEmptyOldMessageList() {
		assertThat(SopeCache.getMessageForChat(3L)).isEmpty();
	}

	@Test
	public void shouldHoldOnlyGivenAmountOfElements() {
		final Long chatNumber = 323L;
		for (int i = 0; i <= SopeCache.MAX_OLD_MESSAGES_FOR_CHAT; ++i) {
			SopeCache.putChatMessageToCache(new MessageToChat("Test " + i, "AUTHOR" + i,
					LocalDateTime.now().minusDays(i).toDate(), chatNumber, "Testi"));

		}
		final List<MessageToChat> oldMessages = SopeCache.getMessageForChat(chatNumber);
		assertThat(oldMessages).hasSize(SopeCache.MAX_OLD_MESSAGES_FOR_CHAT);
		assertThat(oldMessages.get(SopeCache.MAX_OLD_MESSAGES_FOR_CHAT - 1).getAuthor()).isEqualTo("AUTHOR1");
	}

	@Test
	public void shouldReturnChatUserInOrder() {
		final Long chatNumber = 234L;
		SopeCache.updateChatUserCache("Mika", chatNumber);
		SopeCache.updateChatUserCache("Janne", chatNumber);
		SopeCache.updateChatUserCache("Nöfö", 333L);

		final List<String> usersInChat = SopeCache.getChatUsers(chatNumber);
		assertThat(usersInChat).hasSize(2);
		assertThat(usersInChat).containsOnly("Janne", "Mika");
	}

	@Test
	public void shouldNotContainUserWhenUserMovedToAnotherChat() {
		final Long chatNumber = 234L;
		SopeCache.updateChatUserCache("Mika", chatNumber);
		SopeCache.updateChatUserCache("Mika", 555L);

		final List<String> usersInChat = SopeCache.getChatUsers(chatNumber);
		assertThat(usersInChat).isEmpty();
	}
}
