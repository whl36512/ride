-- function
CREATE OR REPLACE FUNCTION trip_pkg.trip_maint (trip trip_type, maint_ind char(1), maint_usr id) RETURNS integer AS $$
DECLARE
	return_code = 1;
BEGIN
	CASE maint_ind 
		WHEN 'N', 'n' THEN
			insert into table trip (driver_id, start_date, end_date, start_time, start_loc, end_loc, 
									price, recur_code, desc_txt, seats, day0_ind, day1_ind, day2_ind, day3_ind,
									day4_ind, day5_ind, day6_ind, rec_creat_ts, rec_creat_usr)
			values (trip.driver_id
					, trip.start_date 
					, trip.end_date 
					, trip.start_time 
					, trip.start_loc 
					, trip.end_loc 
					, trip.price 
					, trip.recur_code
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
			update table trip 
			set driver_id, start_date, end_date, start_time, start_loc, end_loc, 
									price, recur_code, desc_txt, seats, day0_ind, day1_ind, day2_ind, day3_ind,
									day4_ind, day5_ind, day6_ind, rec_creat_ts, rec_creat_usr)
		    where trip_id = trip.trip_id;
				
			values (trip.driver_id
					, trip.start_date 
					, trip.end_date 
					, trip.start_time 
					, trip.start_loc 
					, trip.end_loc 
					, trip.price 
					, trip.recur_code
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
			return_code = 0;
		WHEN 'D', 'd' THEN
			
	END CASE;
	return return_code;
EXCEPTION
	WHEN ERRCODE THEN
		ereport(ERROR, (errcode(ERRCODE)))

	return return_code;
END;
$$ LANGUAGE plpgsql;
