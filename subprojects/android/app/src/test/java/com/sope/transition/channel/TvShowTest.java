//package com.sope.transition.channel;
//
//import com.sope.domain.tvshow.TvShowDTO;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import static junit.framework.Assert.assertEquals;
//
///**
// * Created by petra on 21.6.2016.
// */
//
//public class TvShowTest {
//
//    private TvShowDTO tvShow;
//
//    @Before
//    public void setUp() {
//        tvShow = new TvShowDTO(0L, null, null, null,null,0);
//    }
//
//    @Test
//    public void shouldReturnCorrectParticipantTextWith0() {
//        assertEquals("0", tvShow.getParticipants());
//    }
//
//    @Test
//    public void shouldReturnCorrectParticipantTextWith143() {
//        tvShow.setTotalChatters(143);
//        assertEquals("143", tvShow.getParticipants());
//    }
//
//    @Test
//    public void shouldReturnCorrectParticipansTextWith1343() {
//        tvShow.setTotalChatters(1343);
//        assertEquals("1,3k", tvShow.getParticipants());
//    }
//
//    @Test
//    public void shouldReturnCorrectParticipansTextWith43433() {
//        tvShow.setTotalChatters(13433);
//        assertEquals("13,4k", tvShow.getParticipants());
//    }
//
//    @Test
//    public void shouldReturnCorrectParticipansTextWith400433() {
//        tvShow.setTotalChatters(400433);
//        assertEquals("400,4k", tvShow.getParticipants());
//    }
//}
