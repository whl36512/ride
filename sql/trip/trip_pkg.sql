-- table
DROP TABLE trip;

CREATE TABLE trip
(
	trip_id 		id
	, driver_id		id
	, start_date		date	not null
	, end_date		date	-- last date if recurring, null otherwise
	, start_time	time	not null
	, start_loc 		location
	, end_loc 		location
	, distance		real	not null default 0
	, price			real	not null default 0.1 -- price per mile
	, recur_ind		boolean not null default false
	, status_code		char(1) not null default  'P' -- Editing, Posted, Removed, Expired
	, desc_txt		text
	, seats			integer	not null default 3
	, day0_ind		boolean	not null default false		-- sunday
	, day1_ind		boolean	not null default false
	, day2_ind		boolean	not null default false
	, day3_ind		boolean not null default false
	, day4_ind		boolean not null default false
	, day5_ind		boolean not null default false
	, day6_ind		boolean not null default false
	, rec_creat_ts 			sys_ts
	, rec_mod_ts 			sys_ts
	, rec_creat_usr	id
	, constraint pk_trip PRIMARY KEY (trip_id)
);
-- select (start_loc).display_name, (start_loc).city from trip;

-- schema
-- SCHEMA: trip_pkg

-- DROP SCHEMA trip_pkg ;

CREATE SCHEMA trip_pkg
    AUTHORIZATION ride;

GRANT ALL ON SCHEMA trip_pkg TO ride;

-- ALTER DEFAULT PRIVILEGES IN SCHEMA trip_pkg
-- GRANT ALL ON TABLES TO ride;

-- ALTER DEFAULT PRIVILEGES IN SCHEMA trip_pkg
-- GRANT SELECT, USAGE ON SEQUENCES TO ride;

ALTER DEFAULT PRIVILEGES IN SCHEMA trip_pkg
GRANT EXECUTE ON FUNCTIONS TO ride;

ALTER DEFAULT PRIVILEGES IN SCHEMA trip_pkg
GRANT USAGE ON TYPES TO ride;

-- type
-- DROP TYPE trip_pkg.trip_type;

CREATE TYPE trip_pkg.trip_type AS (
	trip_id id
	, driver_id id
	, start_date date
	, end_date date
	, start_time time
	, start_loc location
	, end_loc location
	, distance real
	, price real
	, recuring_ind boolean
	, maint_ind char(1)
	, desc_txt text
	, seat integer
	, day0_ind boolean
	, day1_ind boolean
	, day2_ind boolean
	, day3_ind boolean
	, day4_ind boolean
	, day5_ind boolean
	, day6_ind boolean
);

