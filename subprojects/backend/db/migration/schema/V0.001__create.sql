 create table ChatSequence (value bigint not null) Engine=MYISAM;
 insert into ChatSequence values(0);
 

    create table Category (
        id bigint not null auto_increment,
        active bit default false not null,
        created datetime,
        company varchar(255) default '' not null,
        endTime datetime,
        iconName varchar(255) default '' not null,
        name varchar(255) default '' not null,
        ordinal integer not null,
        place varchar(255) default '' not null,
        startTime datetime,
        timezone varchar(255) default '' not null,
        type varchar(255) not null,
        validFrom datetime,
        validTo datetime,
        primary key (id)
    ) ENGINE=InnoDB;

    create table Chat (
        id bigint not null auto_increment,
        active bit default false not null,
        created datetime,
        chatNumber bigint,
        header varchar(255) default '' not null,
        lastMessageSent datetime,
        userCount integer default 0 not null,
        category_id bigint,
        primary key (id)
    ) ENGINE=InnoDB;

    create table ChatMessage (
        id bigint not null auto_increment,
        message text(2000),
        sendTime datetime,
        username varchar(255),
        chat_id bigint,
        primary key (id)
    ) ENGINE=InnoDB;

    create table User (
        id bigint not null auto_increment,
        active bit default false not null,
        created datetime,
        accountType integer,
        birthDate datetime,
        coins integer default 0 not null,
        email varchar(255) not null,
        firebaseToken varchar(255) default '',
        firstname varchar(255) default '' not null,
        lastUsageDate datetime,
        lastname varchar(255) default '' not null,
        password varchar(255) not null,
        banDateTime datetime,
        banReleaseDateTime datetime,
        lastBanDate datetime,
        totalBanTimes integer default 0 not null,
        warningCount integer default 0 not null,
        username varchar(255) default '' not null,
        primary key (id)
    ) ENGINE=InnoDB;

    alter table Chat 
        add constraint FKdpb8sv83ba25t76hk7kb25fbj 
        foreign key (category_id) 
        references Category (id);

    alter table ChatMessage 
        add constraint FKo280e91rigfru9okctgbakgsi 
        foreign key (chat_id) 
        references Chat (id);

   
ALTER TABLE ChatMessage 
  ADD CONSTRAINT Chat_FK 
  FOREIGN KEY (chat_id) 
  REFERENCES Chat(id) 
  ON DELETE CASCADE;

SET NAMES utf8mb4;
ALTER DATABASE Sope CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;


CREATE INDEX ChatMessageIndex ON ChatMessage (chatNumber);
CREATE INDEX CategoryNameIndex ON Category (name);
CREATE INDEX CategoryStartTimeIndex ON Category (startTime);
CREATE INDEX CategoryTypeIndex ON Category (type);


