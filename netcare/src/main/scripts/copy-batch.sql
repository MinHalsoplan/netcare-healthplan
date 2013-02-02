CREATE OR REPLACE FUNCTION copy_to_video_batch() RETURNS void AS $$
BEGIN
	perform copy_units_to_video();
	perform copy_users_to_video();
	perform copy_givers_to_video();
END;
$$ LANGUAGE plpgsql;