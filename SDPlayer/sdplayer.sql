drop database if exists SDPlayer;

CREATE DATABASE SDPlayer CHARACTER SET utf8 COLLATE utf8_general_ci;

use SDPlayer;

create table Section (
  scheduledTime int(20) primary key,
  catName varchar(30) not null,
  startToon bit(1) default 0 not null,
  endToon bit(1) default 0 not null,
  popunitiDoKraja  bit(1) default 0 not null,
  prioritet int(2) default 6 not null
);

create table Trait (
  id int(20) primary key,
  traitName varchar(50) not null,
  abrev varchar(10) not null,
  parentTrait int(20) not null default -1
);

insert into Trait (id, traitName, abrev) values (1, 'root', 'root');

create table Song (
  id int(20) primary key,
  rank int(2) not null,
  fullPath varchar(250) not null
);

create table Song_Trait (
  song_id int(20) not null,
  trait_id int (20) not null,
  primary key (song_id, trait_id)
);

create table Jingle (
  id int(11) primary key,
  song_id int(20) not null
);

create table ListItem (
  id int(20) primary key,
  section_time int(20) not null,
  song_id int(20) not null
);