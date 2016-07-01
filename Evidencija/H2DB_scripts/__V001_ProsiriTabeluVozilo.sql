alter table potrosac add 
(podrucje varchar(3)
,broj_sedista int not null default 0
,snaga_motora int not null default 0
,tezina int not null default 0
,nosivost int not null default 0
,rb_naloga int not null default 0
,vozaci varchar(255));