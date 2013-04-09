/* TODO - Make sure this is the right way to add the column */
alter table nc_health_plan add column active boolean not null;

update nc_health_plan set active=false where archived=true;
update nc_health_plan set active=true where archived=false or archived is null;

alter table nc_health_plan drop column archived;
alter table nc_health_plan drop column iteration;