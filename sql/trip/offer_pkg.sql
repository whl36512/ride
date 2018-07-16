-- schema
-- SCHEMA: offer_pkg

-- DROP SCHEMA offer_pkg ;

CREATE SCHEMA offer_pkg
    AUTHORIZATION ride;

GRANT ALL ON SCHEMA offer_pkg TO ride;

-- ALTER DEFAULT PRIVILEGES IN SCHEMA offer_pkg
-- GRANT ALL ON TABLES TO ride;

-- ALTER DEFAULT PRIVILEGES IN SCHEMA offer_pkg
-- GRANT SELECT, USAGE ON SEQUENCES TO ride;

ALTER DEFAULT PRIVILEGES IN SCHEMA offer_pkg
GRANT EXECUTE ON FUNCTIONS TO ride;

ALTER DEFAULT PRIVILEGES IN SCHEMA offer_pkg
GRANT USAGE ON TYPES TO ride;

-- type
-- DROP TYPE offer_pkg.offer_type;

CREATE OR REPLACE TYPE offer_pkg.offer_type AS (
	offer_id id
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

