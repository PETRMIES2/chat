package com.sope.controller;

public interface ApiStructure {
    String VERSION = "/v1";
	String BASE = VERSION + "/api/";
	String USERS = BASE + "users";
    String USERNAMECHECK = BASE + "username";
	String SHOWS = BASE + "shows";
    String GENERAL = BASE + "general";
	String CHAT = BASE + "chats";
    String MESSAGES = BASE + "messages";
    String PUBLIC = BASE + "public";


}
