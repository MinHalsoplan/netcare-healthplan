#!/bin/bash

# backup database
dir=/home/callista

. $dir/.netcarerc

cd $dir/jobs

echo "Start at $(date)"
psql -U netcare_video -w netcare_video_db <<EOF 
\set AUTOCOMMIT off
select dblink_connect('hplink', 'dbname=netcare_healthplan_db user=netcare_healthplan password=*****');
select "copy_to_video_batch"();
commit;
EOF
echo "Exit with code $?"
echo "----"


