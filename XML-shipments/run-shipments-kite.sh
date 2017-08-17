#!/bin/bash

cd /home/palantir/kite-shipments/
KITE_OUTPUT_HOME=/opt/palantir/pXML-Prod/shipments

echo "Running Shipment and Shipment Item Kite scripts in $KITE_OUTPUT_HOME!"

global_begin_time=$(date +%s)
    
local_begin_time=$(date +%s)
echo " Running: kite for Shipment.xml"
/opt/palantir/services/staging_main/dispatchServer/bin/unix/kite.sh -c /home/palantir/kite-shipments/Shipment.xml -i 
local_end_time=$(date +%s)
diffsec="$(expr $local_end_time - $local_begin_time)"
echo | awk -v D=$diffsec '{printf " Elapsed time: %02d:%02d:%02d\n",D/(60*60),D%(60*60)/60,D%60}'

local_begin_time=$(date +%s)
echo " Running: kite for ShipmentItem.xml"
/opt/palantir/services/staging_main/dispatchServer/bin/unix/kite.sh -c /home/palantir/kite-shipments/ShipmentItem.xml -i 
local_end_time=$(date +%s)
diffsec="$(expr $local_end_time - $local_begin_time)"
echo | awk -v D=$diffsec '{printf " Elapsed time: %02d:%02d:%02d\n",D/(60*60),D%(60*60)/60,D%60}'

global_end_time=$(date +%s)
diffsec="$(expr $global_end_time - $global_begin_time)"
echo | awk -v D=$diffsec '{printf "Global Elapsed time for Shipments: %02d:%02d:%02d\n",D/(60*60),D%(60*60)/60,D%60}'


