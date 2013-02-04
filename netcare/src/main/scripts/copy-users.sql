CREATE OR REPLACE FUNCTION copy_users_to_video() RETURNS void AS $$
DECLARE
       newId BIGINT;
       cc integer;
       rec RECORD;
BEGIN
	FOR rec in select id, email, first_name, sur_name, civic_reg_number, phone_number from dblink('hplink', 'select u.id, u.email, u.first_name, u.sur_name, p.civic_reg_number, p.phone_number from nc_user u, nc_patient p where p.id = u.id') as t(id bigint, email text, first_name text, sur_name text, civic_reg_number text, phone_number text)
	LOOP
		select count(*) into cc from nc_video_patient p where p.civic_reg_number = rec.civic_reg_number;
		IF cc = 0 THEN
			newId := nextval('hibernate_sequence');

			insert into nc_video_user(id, email, name) 
			values (newId, rec.email, rec.first_name || ' ' || rec.sur_name);

			insert into nc_video_patient(id, civic_reg_number, phone_number)
			values (newId, rec.civic_reg_number, rec.phone_number);
		ELSE
			select id into newId from nc_video_patient p where p.civic_reg_number = rec.civic_reg_number;
			update nc_video_user set name = rec.first_name || ' ' || rec.sur_name where id = newId;
			update nc_video_patient set phone_number = rec.phone_number where id = newId;
		END IF;
	END LOOP;
END;
$$ LANGUAGE plpgsql;
