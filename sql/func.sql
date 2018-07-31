\c ride

drop schema if exists funcs cascade;
create schema funcs ;
grant all on schema funcs to ride;
--grant all on all functions in schema funcs to ride;

create or replace function funcs.updateusr(
        		  	       in_oauth_id    varchar 
				     , in_first_name  varchar default null
        			     , in_last_name   varchar default null
                                     , in_headline    varchar default null
        		             , in_email       varchar default null
        			     , in_bank_email  varchar default null
        		  	     --, in_balance     real = 0
        			     , in_oauth_host  varchar default null
				)
  returns usr
as
$body$
 -- if an input field is null, the field will not be updated
 -- if an input field is '', the field will be updated to ''
	insert into usr ( oauth_id) values (in_oauth_id)
	on conflict on constraint uk_usr
	do nothing ;

	update usr u
		  set first_name=coalesce( in_first_name, u.first_name ) 
		  , last_name	=coalesce(in_headline, u.last_name )
		  , headline	=coalesce(in_headline, u.headline) 
		  , email	=coalesce(in_email, u.email)
		  , bank_email  =coalesce(in_bank_email, u.bank_email)
		  , oauth_host  =coalesce(in_oauth_host, u.oauth_host) 
		  , m_ts = now()
	where u.oauth_id = in_oauth_id 
	returning u.*
$body$
language sql;

create or replace function funcs.updatetrip(
          trip_id               text default null
        , driver_id             text default null
        , start_date            text default null
        , end_date              text default null
        , departure_time        text default null
        , start_loc             text default null
        , start_display_name    text default null
        , start_lat             text default null
        , start_lon             text default null
        , end_loc               text default null
        , end_display_name      text default null
        , end_lat               text default null
        , end_lon               text default null
        , distance              text default null
        , price                 text default null
        , recur_ind             text default null
        , status_code           text default null
        , description           text default null
        , seats                 text default null
        , day0_ind              text default null
        , day1_ind              text default null
        , day2_ind              text default null
        , day3_ind              text default null
        , day4_ind              text default null
        , day5_ind              text default null
        , day6_ind              text default null
        , c_ts                  text default null
        , m_ts                  text default null
        , c_usr                 text default null
)
  returns trip
as
$body$
DECLARE
  s0 RECORD ;
  i1 RECORD ;
  u1 RECORD ;
BEGIN
  select 
      trip_id               ::uuid
    , driver_id             ::uuid
    , start_date            ::date
    , end_date              ::date
    , departure_time        ::time
    , start_loc             
    , start_display_name    
    , start_lat             ::decimal
    , start_lon             ::decimal
    , end_loc               
    , end_display_name      
    , end_lat               ::decimal
    , end_lon               ::decimal
    , distance              ::decimal
    , price                 ::decimal
    , recur_ind             ::boolean
    , status_code           
    , description              
    , seats                 ::integer 
    , day0_ind              ::boolean                -- postgres converts 'on','y','yes' to true
    , day1_ind              ::boolean
    , day2_ind              ::boolean
    , day3_ind              ::boolean
    , day4_ind              ::boolean
    , day5_ind              ::boolean
    , day6_ind              ::boolean
    , c_ts                  ::timestamp with time zone
    , m_ts                  ::timestamp with time zone
    , c_usr
    into s0
  ;

  insert into trip ( driver_id, start_date) 
  select  s0.driver_id, s0.start_date
  where s0.trip_id is null
  returning * into i1 
  ;

  update trip t
  set 
      start_date          = coalesce(s0.start_date          , t.start_date        ) 
    , end_date            = coalesce(s0.end_date            , t.end_date          )
    , departure_time      = coalesce(s0.departure_time      , t.departure_time    )
    , start_loc           = coalesce(s0.start_loc           , t.start_loc         )
    , start_display_name  = coalesce(s0.start_display_name  , t.start_display_name)
    , start_lat           = coalesce(s0.start_lat           , t.start_lat         )
    , start_lon           = coalesce(s0.start_lon           , t.start_lon         )
    , end_loc             = coalesce(s0.end_loc             , t.end_loc           )
    , end_display_name    = coalesce(s0.end_display_name    , t.end_display_name  )
    , end_lat             = coalesce(s0.end_lat             , t.end_lat           )
    , end_lon             = coalesce(s0.end_lon             , t.end_lon           )
    , distance            = coalesce(s0.distance            , t.distance          )
    , price               = coalesce(s0.price               , t.price             )
    , recur_ind           = coalesce(s0.recur_ind           , t.recur_ind         )
    , status_code         = coalesce(s0.status_code         , t.status_code       )
    , description         = coalesce(s0.description         , t.description       )
    , seats               = coalesce(s0.seats               , t.seats             )
    , day0_ind            = coalesce(s0.day0_ind            , t.day0_ind          )
    , day1_ind            = coalesce(s0.day0_ind            , t.day1_ind          )
    , day2_ind            = coalesce(s0.day0_ind            , t.day2_ind          )
    , day3_ind            = coalesce(s0.day0_ind            , t.day3_ind          )
    , day4_ind            = coalesce(s0.day0_ind            , t.day4_ind          )
    , day5_ind            = coalesce(s0.day0_ind            , t.day5_ind          )
    , day6_ind            = coalesce(s0.day0_ind            , t.day6_ind          )
    , c_ts                = coalesce(s0.c_ts                , t.c_ts              )
    , m_ts                = clock_timestamp()
    , c_usr               = coalesce(s0.c_usr               , t.c_usr             )
  where t.trip_id in ( s0.trip_id, i1.trip_id)
  returning t.* into u1 
  ;
  
  return u1;
END
$body$
language plpgsql;
create or replace function funcs.update_money_trnx(
        money_trnx_id           text default null
        , usr_id                text default null
        , trnx_cd               text default null
        , requested_amount      text default null
        , actual_amount         text default null
        , request_ts            text default null
        , actual_ts             text default null
        , bank_email            text default null
        , reference_no          text default null
        , cmnt                  text default null
        , c_ts                  text default null
        , m_ts                  text default null
)
  returns money_trnx
as
$body$
DECLARE
  s0 RECORD ;
  i1 RECORD ;
  u1 RECORD ;
BEGIN
  select 
          money_trnx_id        ::uuid
        , usr_id             ::uuid
        , trnx_cd            
        , requested_amount   ::ridemoney
        , actual_amount      ::ridemoney
        , request_ts         ::timestamp with time zone
        , actual_ts          ::timestamp with time zone
        , bank_email         ::email
        , reference_no       
        , cmnt      
        , c_ts               ::timestamp with time zone
        , m_ts               ::timestamp with time zone
    into s0
  ;

  insert into money_trnx ( usr_id, trnx_cd) 
  select  s0.usr_id, s0.trnx_cd
  where s0.money_trnx_id is null
  returning * into i1 
  ;

  update money_trnx t
  set 
      trnx_cd            = coalesce(s0.trnx_cd            , t.trnx_cd         )   
    , requested_amount   = coalesce(s0.requested_amount   , t.requested_amount  )
    , actual_amount      = coalesce(s0.actual_amount      , t.actual_amount     )
    , request_ts         = case when s0.requested_amount  is null then t.request_ts        else clock_timestamp()    end
    , actual_ts          = case when s0.actual_amount     is null then t.actual_ts         else clock_timestamp()    end
    , bank_email         = coalesce(s0.bank_email         , t.bank_email        )
    , reference_no       = coalesce(s0.reference_no       , t.reference_no      )
    , cmnt               = coalesce(s0.cmnt               , t.cmnt              )
    , c_ts               = coalesce(s0.c_ts               , t.c_ts              )
    , m_ts               = coalesce(s0.m_ts               , t.clock_timestamp() )
  where t.money_trnx_id in ( s0.money_trnx_id, i1.money_trnx_id)
  returning t.* into u1 
  ;
  
  return u1;
END
$body$
language plpgsql;
