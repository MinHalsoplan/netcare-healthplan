CREATE OR REPLACE FUNCTION copy_units_to_video() RETURNS void AS $$
DECLARE
       cc integer;
       rec RECORD;
BEGIN
	FOR rec in select id, hsa_id, name from dblink('hplink', 'select id, hsa_id, name from nc_care_unit') as t(id bigint, hsa_id text, name text)
	LOOP
		select count(*) into cc from nc_video_care_unit u where u.hsa_id = rec.hsa_id;
		IF cc = 0 THEN
			insert into nc_video_care_unit(id, hsa_id, name) 
			values (nextval('hibernate_sequence'), rec.hsa_id, rec.name);
		ELSE
			update nc_video_care_unit set name = rec.name where hsa_id = rec.hsa_id;
		END IF;
	END LOOP;
END;
$$ LANGUAGE plpgsql;
