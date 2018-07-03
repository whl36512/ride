\c ride

insert into usr (email, bank_email) values ('whl36512@gmail.com'	, 'whl36512@gmail.com');
insert into usr (email, bank_email) values ('weihan.lin@gmail.com'	, 'whl36512@gmail.com');

select * from usr;

insert into trip (
--trip_id
 driver_id     
, trip_start    
, trip_end      
, departure_time
, start_loc     
, end_loc       
, distance      
, price         
, recuring      
, status        
, description   
, seats         
--, day0          
--, day1          
--, day2          
--, day3          
--, day4          
--, day5          
--, day6          
)
select usr_id
	, current_date + interval '10 day'
	, null
	, '8:00:00'
	, ROW(null, 41.895250, -87.631667, null, null, null, null)::location
	, ROW(null, 41.779928, -88.134535, null, null, null, null)::location
	, 100
	, 0.1
	, false
	, 'P'
	, 'non-smoker'
	, 3
from usr
;


select * from trip;
