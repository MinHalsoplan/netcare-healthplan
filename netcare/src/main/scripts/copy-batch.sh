#!/bin/bash

# backup database
dir=/home/callista

. $dir/.netcarerc

cd $dir/jobs

echo "Start at $(date)"
psql -U netcare -w  netcaredb <<EOF 
\set AUTOCOMMIT off
select "copy_to_video_batch"();
commit;
EOF
echo "Exit with code $?"
echo "----"


