drop database if exists ride;
create database ride; 
drop user if exists ride;
create user ride with password 'ride';
-- alter database ride owner to ride;
GRANT ALL PRIVILEGES ON DATABASE ride to ride;
\c ride
grant all on all tables in schema public to ride;
CREATE EXTENSION pgcrypto;
CREATE EXTENSION  "uuid-ossp";


-- IMPORTANT:
-- All columns must be not null because play cannot handle null when converting JSON to case class

CREATE DOMAIN email AS TEXT 
CHECK(
   VALUE ~ '^([a-zA-Z0-9_\-\.]+)@([a-zA-Z0-9_\-]{1,30}\.){1,4}([a-zA-Z]{2,5})$' 
);


--slick cannot handle uuid 
CREATE DOMAIN sys_id as uuid not null default uuid_generate_v4() ;
CREATE DOMAIN textwithdefault as text not null default '' ;
CREATE DOMAIN sys_ts timestamp with time zone not null default clock_timestamp();
CREATE DOMAIN tswithepoch timestamp with time zone not null default '1970-01-01 00:00:00Z' ;
CREATE DOMAIN score integer  CHECK ( value in (1,2,3,4,5));

--CREATE TYPE location AS
   --(
	--address	text 
	--, lat	double precision
	--, long	double precision
	--, display_name	text		-- reverse geocoded
	--, country	text		-- reverse geocoded
	--, state		text		-- reverse geocoded
	--, city		text		-- reverse geocoded
   --);

create table usr
(
	usr_id 			sys_id
	, first_name		textwithdefault
	, last_name		textwithdefault
	, email 		email 
	, bank_email 		email
	, member_since 		sys_ts
	, trips_posted 		integer not null default 0
	, trips_completed 	integer not null default 0
	, rating		real not null default 0
	, balance		real not null default 0
	, oauth_id		textwithdefault
	, oauth_host		text not null default 'linkedin'
        , c_ts 			sys_ts
        , m_ts 			sys_ts
	, constraint pk_usr PRIMARY KEY (usr_id)
	, constraint uk_usr unique  (oauth_id)
) ;

CREATE TABLE trip
(
        trip_id                 sys_id
        , driver_id             sys_id
        , start_date            date    not null
        , end_date              date    -- last date if recurring, null otherwise
        , start_time    	time    not null default current_time
	, start_loc		textwithdefault
	, start_display_name	textwithdefault
	, start_lat		double precision not null default 0
	, start_lon		double precision not null default 0
	, end_loc		textwithdefault
	, end_display_name	textwithdefault
	, end_lat		double precision not null default 0
	, end_lon		double precision not null default 0
        , distance              real    not null default 0
        , price                 real    not null default 0.1 -- price per mile
        , recur_ind             boolean not null default false
        , status_code           char(1) not null default  'P' -- Pending, Active,  Cancelled,  Expired
        , desc_txt              textwithdefault
        , seats                 integer not null default 3
        , day0_ind              boolean not null default false          -- sunday
        , day1_ind              boolean not null default false
        , day2_ind              boolean not null default false
        , day3_ind              boolean not null default false
        , day4_ind              boolean not null default false
        , day5_ind              boolean not null default false
        , day6_ind              boolean not null default false
        , c_ts                  sys_ts
        , m_ts                  sys_ts
        , rec_creat_usr 	textwithdefault
        , constraint pk_trip PRIMARY KEY (trip_id)
);


create table book_status(
	book_status_cd 	char(1) not null
	, description	textwithdefault
	, constraint pk_book_status PRIMARY KEY (book_status_cd)
);

insert into book_status 
values 
  ('C', 'Considering')
, ('I', 'Insufficient Balance')
, ('B', 'Booked')
, ('S', 'trip started')
, ('R', 'cancelled by Rider')
, ('D', 'cancelled by Driver')
, ('F', 'Finished')
;

create table book
(
	book_id			sys_id
	, trip_id		uuid not null 
	, rider_id		uuid not null 
	, trip_date		date not null
	, price			real not null
	, money_to_driver	real not null default 0
	, money_to_rider	real not null default 0
	, book_status_cd	char(1) not null default  'C' -- Considering, insufficient Balance, Resered, trip Started, Finished, cancelled by Rider, cancelled by Driver
	, rider_score		smallint  CHECK ( rider_score in (1,2,3,4,5))
	, driver_score		smallint  CHECK ( rider_score in (1,2,3,4,5))
	, rider_comment		textwithdefault
	, driver_comment	textwithdefault
	, book_ts		sys_ts
	, driver_cancel_ts	tswithepoch
	, rider_cancel_ts	tswithepoch
	, finish_ts		tswithepoch
	, constraint pk_book PRIMARY KEY (book_id)
);

create table money_trnx (
	money_trnx_id	sys_id
	, usr_id	sys_id
	, trnx_cd	textwithdefault
	, amount	real not null
	, trnx_ts	sys_ts
	, reference_no	textwithdefault
	, cmnt 		textwithdefault
	, constraint pk_money_trnx PRIMARY KEY (money_trnx_id)
);

alter table trip add FOREIGN KEY (driver_id) REFERENCES usr (usr_id);
alter table book add FOREIGN KEY (rider_id) REFERENCES usr (usr_id);
alter table book add FOREIGN KEY (trip_id) REFERENCES trip (trip_id);
alter table money_trnx add FOREIGN KEY (usr_id) REFERENCES usr (usr_id);
alter table book add FOREIGN KEY (book_status_cd) REFERENCES book_status (book_status_cd);

grant all on public.usr to ride;
grant all on public.trip to ride;
grant all on public.book to ride;
grant all on public.money_trnx to ride;
