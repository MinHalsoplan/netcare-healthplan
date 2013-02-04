CREATE OR REPLACE FUNCTION copy_givers_to_video() RETURNS void AS $$
DECLARE
       newId BIGINT;
       cc integer;
       eid integer;
       rec RECORD;
BEGIN
        FOR rec in select id, email, first_name, sur_name, hsa_id, ehsa_id from dblink('hplink', 'select u.id, u.email, u.first_name, u.sur_name, g.hsa_id, e.hsa_id from nc_user u, nc_care_giver g, nc_care_unit e  where g.id = u.id and e.id = g.care_unit_id') as t(id bigint, email text, first_name text, sur_name text, hsa_id text, ehsa_id text)
	LOOP
		select count(*) into cc from nc_video_care_giver g where g.hsa_id = rec.hsa_id;
		IF cc = 0 THEN
			newId := nextval('hibernate_sequence');

			insert into nc_video_user(id, email, name) 
			values (newId, rec.email, rec.first_name || ' ' || rec.sur_name);

			select id into eid from nc_video_care_unit where hsa_id = rec.ehsa_id;

			insert into nc_video_care_giver(id, hsa_id, care_unit_id)
			values (newId, rec.hsa_id, eid);
		ELSE
			select id into newId from nc_video_care_giver g where g.hsa_id = rec.hsa_id;
			update nc_video_user set name = rec.first_name || ' ' || rec.sur_name where id = newId;
		END IF;
	END LOOP;
END;
$$ LANGUAGE plpgsql;
