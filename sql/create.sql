drop database if exists ride;
create database ride; 
drop user if exists ride;
create user ride with password 'ride';
-- alter database ride owner to ride;
-- GRANT ALL PRIVILEGES ON DATABASE ride to ride;
\c ride
grant all on all tables in schema public to ride;
CREATE EXTENSION pgcrypto;
CREATE EXTENSION  "uuid-ossp";


CREATE DOMAIN email AS TEXT
CHECK(
   VALUE ~ '^([a-zA-Z0-9_\-\.]+)@([a-zA-Z0-9_\-\.]+)\.([a-zA-Z]{2,5})$'
);

CREATE DOMAIN id as uuid not null default uuid_generate_v4();
CREATE DOMAIN sys_ts timestamp with time zone not null default clock_timestamp();
CREATE DOMAIN score integer  CHECK ( value in (1,2,3,4,5));

CREATE TYPE location AS
   (
	address	text 
	, lat	double precision
	, long	double precision
	, display_name	text		-- reverse geocoded
	, country	text		-- reverse geocoded
	, state		text		-- reverse geocoded
	, city		text		-- reverse geocoded
   );

create table usr
(
	usr_id 			id
	, email 		email
	, bank_email 		email
	, member_since 		sys_ts
	, trips_posted 		integer not null default 0
	, trips_completed 	integer not null default 0
	, rating		real not null default 0
	, balance		real not null default 0
	, oauth_id		text
	, oauth_host		text not null default 'linkedin'
        , c_ts 			sys_ts
        , m_ts 			sys_ts
	, constraint pk_usr PRIMARY KEY (usr_id)
) ;

create table trip
(
	trip_id 		id
	, driver_id		id
	, c_ts 			sys_ts
	, m_ts 			sys_ts
	, trip_start		date	not null
	, trip_end		date	-- last date if recurring, null otherwise
	, departure_time	time	not null
	, start_loc 		location
	, end_loc 		location
	, distance		real	not null default 0
	, price			real	not null default 0.1 -- price per mile
	, recuring		boolean not null default false
	, status		char(1) not null default  'P' -- Editing, Posted, Removed, Expired
	, description		text
	, seats			integer	not null default 3
	, day0			boolean	not null default false		-- sunday
	, day1			boolean	not null default false
	, day2			boolean	not null default false
	, day3			boolean not null default false
	, day4			boolean not null default false
	, day5			boolean not null default false
	, day6			boolean not null default false
	, constraint pk_trip PRIMARY KEY (trip_id)
);
-- select (start_loc).display_name, (start_loc).city from trip;

create table book_status(
	book_status_cd 	char(1) not null
	, description	text
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
	book_id			id
	, trip_id		id
	, rider_id		id
	, trip_date		date not null
	, price			real not null
	, money_to_driver	real
	, money_to_rider	real
	, book_status_cd	char(1) not null default  'C' -- Considering, insufficient Balance, Resered, trip Started, Finished, cancelled by Rider, cancelled by Driver
	, rider_score		score
	, driver_score		score
	, rider_comment		text
	, driver_comment	text
	, book_ts		sys_ts
	, driver_cancel_ts	timestamp with time zone
	, rider_cancel_ts	timestamp with time zone
	, finish_ts		timestamp with time zone
	, constraint pk_book PRIMARY KEY (book_id)
);

create table money_trnx (
	money_trnx_id	id
	, usr_id	id
	, trnx_type	char(1) -- Deposit, Withdraw
	, amount	real	
	, trnx_ts	sys_ts
	, reference_no	text
	, cmnt 		text
	, constraint pk_money_trnx PRIMARY KEY (money_trnx_id)
);

alter table trip add FOREIGN KEY (driver_id) REFERENCES usr (usr_id);
alter table book add FOREIGN KEY (rider_id) REFERENCES usr (usr_id);
alter table book add FOREIGN KEY (book_id) REFERENCES trip (trip_id);
alter table money_trnx add FOREIGN KEY (usr_id) REFERENCES usr (usr_id);
alter table book add FOREIGN KEY (book_status_cd) REFERENCES book_status (book_status_cd);

grant all on public.usr to ride;
grant all on public.trip to ride;
grant all on public.book to ride;
grant all on public.money_trnx to ride;
