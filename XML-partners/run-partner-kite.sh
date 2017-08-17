#!/bin/bash

cd /home/palantir/kite-partners/
KITE_OUTPUT_HOME=/opt/palantir/pXML-Prod/CMSPartner

echo "Running CMS Partner Kite scripts in $KITE_OUTPUT_HOME!"

global_begin_time=$(date +%s)
    
local_begin_time=$(date +%s)
echo " Running: kite for ContactPartner.xml"
/opt/palantir/services/staging_main/dispatchServer/bin/unix/kite.sh -c /home/palantir/kite-partners/ContactPartner.xml -i 
local_end_time=$(date +%s)
diffsec="$(expr $local_end_time - $local_begin_time)"
echo | awk -v D=$diffsec '{printf " Elapsed time: %02d:%02d:%02d\n",D/(60*60),D%(60*60)/60,D%60}'

local_begin_time=$(date +%s)
echo " Running: kite for HCOPartner.xml"
/opt/palantir/services/staging_main/dispatchServer/bin/unix/kite.sh -c /home/palantir/kite-partners/HCOPartner.xml -i 
local_end_time=$(date +%s)
diffsec="$(expr $local_end_time - $local_begin_time)"
echo | awk -v D=$diffsec '{printf " Elapsed time: %02d:%02d:%02d\n",D/(60*60),D%(60*60)/60,D%60}'

local_begin_time=$(date +%s)
echo " Running: kite for SupplierPartner.xml"
/opt/palantir/services/staging_main/dispatchServer/bin/unix/kite.sh -c /home/palantir/kite-partners/SupplierPartner.xml -i 
local_end_time=$(date +%s)
diffsec="$(expr $local_end_time - $local_begin_time)"
echo | awk -v D=$diffsec '{printf " Elapsed time: %02d:%02d:%02d\n",D/(60*60),D%(60*60)/60,D%60}'

global_end_time=$(date +%s)
diffsec="$(expr $global_end_time - $global_begin_time)"
echo | awk -v D=$diffsec '{printf "Global Elapsed time for Partners: %02d:%02d:%02d\n",D/(60*60),D%(60*60)/60,D%60}'


