-- function
CREATE OR REPLACE FUNCTION trip_pkg.trip_maint (trip trip_type, maint_ind char(1), maint_usr id) RETURNS integer AS $$
DECLARE
	int return_code := 1;
	int selected := 0;
BEGIN
	CASE maint_ind 
		WHEN 'N', 'n' THEN
			insert into table trip (driver_id, start_date, end_date, start_time, start_loc, end_loc, 
									price, recur_code, status_code, desc_txt, seats, day0_ind, day1_ind, day2_ind, day3_ind,
									day4_ind, day5_ind, day6_ind, rec_creat_ts, rec_creat_usr)
			values (trip.driver_id
					, trip.start_date 
					, trip.end_date 
					, trip.start_time 
					, trip.start_loc 
					, trip.end_loc 
					, trip.price 
					, trip.recur_code
					, 'P'
					, trip.desc_txt 
					, trip.seat 
					, trip.day0_ind 
					, trip.day1_ind 
					, trip.day2_ind 
					, trip.day3_ind 
					, trip.day4_ind 
					, trip.day5_ind 
					, trip.day6_ind
					, sysdate
					, maint_usr);			
		WHEN 'U', 'u' THEN
			select count(*) into selected
			from book
			where trip_id = trip.trip_id
			limit 1;
			
			if selected = 0 then
				update table trip 
				set driver_id = trip.driver_id
					, start_date = trip.start_date 
					, end_date = trip.end_date 
					, start_time = trip.start_time 
					, start_loc = trip.start_loc 
					, end_loc = trip.end_loc 
					, price = trip.price 
					, recur_code = trip.recur_code
					, status_code = 'P'
					, desc_txt = trip.desc_txt 
					, seats = trip.seat 
					, day0_ind = trip.day0_ind 
					, day1_indtrip.day1_ind 
					, day2_indtrip.day2_ind 
					, day3_indtrip.day3_ind 
					, day4_indtrip.day4_ind 
					, day5_indtrip.day5_ind 
					, day6_indtrip.day6_ind
				where trip_id = trip.trip_id;
				return_code = 0;
			end if;
			
		WHEN 'D', 'd' THEN
			select count(*) into selected
			from book
			where trip_id = trip.trip_id
			limit 1;
			
			if selected = 0 then
				update table trip 
				set  status_code = 'D'
				where trip_id = trip.trip_id;
				return_code = 0;
			end if;
			
	END CASE;
	return return_code;
EXCEPTION
	WHEN ERRCODE THEN
		ereport(ERROR, (errcode(ERRCODE)))

	return return_code;
END;
$$ LANGUAGE plpgsql;
