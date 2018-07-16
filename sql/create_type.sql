REATE TYPE offer AS (
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
