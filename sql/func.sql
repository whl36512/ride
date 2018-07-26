\c ride

drop schema if exists funcs cascade;
create schema funcs ;
grant all on schema funcs to ride;
--grant all on all functions in schema funcs to ride;

create or replace function funcs.updateusr(
        		  	       in_oauth_id    varchar 
				     , in_first_name  varchar default ''
        			     , in_last_name   varchar default ''
        		             , in_email       varchar default ''
        			     , in_bank_email  varchar default ''
        		  	     --, in_balance     real = 0
        			     , in_oauth_host  varchar default ''
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
		  set first_name=case when in_first_name = '' then u.first_name else in_first_name end
		  , last_name	=case when in_last_name  = '' then u.last_name  else in_last_name end
		  , email	=case when in_email      = '' then u.email      else in_email end
		  , bank_email  =case when in_bank_email = '' then u.bank_email else in_bank_email end
		  , oauth_host  =case when in_oauth_host = '' then u.oauth_host else in_oauth_host end
		  , m_ts = now()
	where u.oauth_id = in_oauth_id 
	returning u.*
$body$
language sql;

create or replace function funcs.savetrip(
)
  returns trip
as
$body$
	select	 * from trip;
$body$
language sql;

create or replace function funcs.getusr(
)
  returns usr
as
$body$
	select	 * from usr;
$body$
language sql;


