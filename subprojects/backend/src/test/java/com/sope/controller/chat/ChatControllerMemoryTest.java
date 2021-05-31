package com.sope.controller.chat;

import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import utils.database.mysql.MySQLTestDatabase;

@RunWith(SpringJUnit4ClassRunner.class)
public class ChatControllerMemoryTest extends MySQLTestDatabase {
    ////
    //// @Inject
    //// private ChatControllerMock chatControllerMock;
    // @Inject
    // private ChatFixture chatFixture;
    //
    // @Inject
    // private ChatRepository chatRepository;
    //
    // @Inject
    // private TestUser testUser;
    //
    // @Before
    // public void setUp() {
    // initializeTestData(chatFixture);
    // }
    //
//    @Test
    // public void shouldRenameChat() throws Exception {
    // final RenameChatHeaderDTO rename = new RenameChatHeaderDTO("Mikan nimetty
    //// kanava. 20 merkkiä maksimi asdfasdf asfd safd sdasfasd f", "test");
    // chatControllerMock.renameChatHeader(chatFixture.getChatToBeRenamed().getChatNumber(),
    //// rename, testUser.getAuthToken(), status().is2xxSuccessful());
    // getTransactionExecutor().read(() -> {
    // final Chat chat =
    //// chatRepository.getChatByChatNumber(chatFixture.getChatToBeRenamed().getChatNumber()).get();
    // assertThat(chat.getHeader()).isEqualTo("Mikan nimetty kanava");
//
    // });
//    }
//
//    @Test
    // public void shoudReturnNextChat() throws Exception {
    // final Category nextChatForShow = new Category();
    // nextChatForShow.setName(chatFixture.getChatToBeRenamed().getTvShow().getName());
    //
    // final List<ChatDTO> chatDTO =
    //// chatControllerMock.getNextChat(nextChatForShow,
    //// testUser.getAuthToken(), status().isOk());
    // assertThat(chatDTO.get(0).chatHeader).isEqualTo(chatFixture.getChatToBeRenamed().getHeader());
    //
//    }
    //
    //// @Test
    //// public void shouldNotCreateChatWhenTvShowNotFound() throws Exception {
    //// CreateChatDTO newChat = new CreateChatDTO();
    //// newChat.setShow(new TvShowDTO());
    //// newChat.setChatName("TEsti");
    ////
    //// newChat.getShow().setName("OhjelmanNimi");
    //// chatControllerMock.failingCreateChat(newChat, testUser.getAuthToken(),
    //// status().isBadRequest());
    ////
    //// }
    ////
    //// @Test
    //// public void shouldCreateNewChat() throws Exception {
    //// CreateChatDTO newChat = new CreateChatDTO();
    //// newChat.setShow(new TvShowDTO());
    //// newChat.setChatName("TEsti");
    //// newChat.getShow().setName(chatFixture.getChatToBeRenamed().getTvShow().getName());
    //// ChatDTO createdChat = chatControllerMock.createChat(newChat,
    //// testUser.getAuthToken(), status().isOk());
    //// assertThat(createdChat.getChatNumber()).isNotNull();
    //// }

}