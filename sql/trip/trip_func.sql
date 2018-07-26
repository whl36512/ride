drop function trip_pkg.trip_maint;
-- function
CREATE OR REPLACE FUNCTION trip_pkg.trip_maint (
	trip_id 		id
	, driver_id		id
	, start_date	date	
	, end_date		date	
	, start_time	time	
	, start_loc 	location
	, end_loc 		location
	, distance		real	
	, price			real	
	, recur_ind		boolean 
	, status_code	char(1) 
	, desc_txt		text
	, seats			integer	
	, day0_ind		boolean	
	, day1_ind		boolean	
	, day2_ind		boolean	
	, day3_ind		boolean 
	, day4_ind		boolean 
	, day5_ind		boolean 
	, day6_ind		boolean 
	, rec_creat_ts 	sys_ts
	, rec_mod_ts 	sys_ts
	, rec_creat_usr	id
	, maint_ind     char(1)
	, maint_usr id) 
RETURNS int AS $$
DECLARE
	return_code int default 1;
	selected int default 0;
BEGIN
--     raise info 'enter';
	CASE maint_ind 
		WHEN 'N', 'n' THEN
			with a as (
				insert into trip (rec_creat_ts, rec_creat_usr)
						values (current_time, maint_usr)																																												
				returning *)	
			update trip
			set driver_id = driver_id
				, start_date = start_date
				, end_date = end_date
				, start_time = start_time
				, start_loc = start_loc
				, end_loc = end_loc
				, price = price
				, recur_ind = recur_ind
				, status_code = 'P'
				, desc_txt = desc_txt
				, seat = seat
				, day0_ind = day0_ind
				, day1_ind = day1_ind
				, day2_ind = day2_ind 
				, day3_ind = day3_ind
				, day4_ind = day4_ind
				, day5_ind = day5_ind
				, day6_ind = day6_ind
				, rec_mod_ts = current_timestamp
			where trip_id = (select trip_id from a);
-- 			raise info 'insert';
				return_code = 0;
-- 			raise info '%', return_code;
-- 		WHEN 'U', 'u' THEN
-- 			select count(*) into selected
-- 			from book
-- 			where trip_id = trip.trip_id;
			
-- 			if selected = 0 then
-- 				update trip 
-- 				set driver_id = trip.driver_id
-- 					, start_date = trip.start_date 
-- 					, end_date = trip.end_date 
-- 					, start_time = trip.start_time 
-- 					, start_loc = trip.start_loc 
-- 					, end_loc = trip.end_loc 
-- 					, price = trip.price 
-- 					, recur_code = trip.recur_code
-- 					, status_code = 'P'
-- 					, desc_txt = trip.desc_txt 
-- 					, seats = trip.seat 
-- 					, day0_ind = trip.day0_ind 
-- 					, day1_ind = trip.day1_ind 
-- 					, day2_ind = trip.day2_ind 
-- 					, day3_ind = trip.day3_ind 
-- 					, day4_ind = trip.day4_ind 
-- 					, day5_ind = trip.day5_ind 
-- 					, day6_ind = trip.day6_ind
-- 				where trip_id = trip.trip_id;
-- 				return_code = 0;
--  			else 
--  				raise exception 'Can not update booked trip';
-- 			end if;
			
-- 		WHEN 'D', 'd' THEN
-- 			select count(*) --into selected
-- 			from trip
-- 			where trip_id = 'e4d766ba-d9dd-4e63-a64e-1dbb7811c6e2'
-- 			;
			
-- 			if selected = 0 then
-- 				raise exception 'Trip not exist';
-- 			elsif  selected = 1 then
-- 				update trip 
-- 				set  status_code = 'D'
-- 				where trip_id = trip.trip_id;
				return_code = 0;
-- 			elsif selected = 2 then
-- 				raise exception 'More than 1 trip with the same ID';
-- 			end if;
			
	END CASE;
	return return_code;
EXCEPTION 
	WHEN others THEN
		raise notice '% %', SQLERRM, SQLSTATE;
		return return_code;
END;
$$ LANGUAGE plpgsql;
