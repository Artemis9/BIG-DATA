#!/bin/bash

KITE_OUTPUT_HOME=/opt/palantir/pXML-Prod/HRSA-UDS
echo -e "Removing UDS Grantees, Grantee Sites, UDS Contacts & Reports in $KITE_OUTPUT_HOME! \n(Grantee Internal & External Ids cannot be updated to a DR partner Id, & kite cannot overwrite stub object ids in links)"
find $KITE_OUTPUT_HOME -name GRANTEE_*  -exec rm {} \;
find $KITE_OUTPUT_HOME -name  REPORT_[^S]* -exec rm {} \;
find $KITE_OUTPUT_HOME -name JUST_*  -exec rm {} \;

cd /home/palantir/kite-uds/
echo "Running UDS Kite scripts in $KITE_OUTPUT_HOME!"

global_begin_time=$(date +%s)
    
##local_begin_time=$(date +%s)
##echo " Running: kite for Grantees.xml"
##/opt/palantir/services/staging_main/dispatchServer/bin/unix/kite.sh -c /home/palantir/kite-uds/Grantees.xml -i 
##local_end_time=$(date +%s)
##diffsec="$(expr $local_end_time - $local_begin_time)"
##echo | awk -v D=$diffsec '{printf " Elapsed time: %02d:%02d:%02d\n",D/(60*60),D%(60*60)/60,D%60}'

local_begin_time=$(date +%s)
echo " Running: kite for Grantees-2012.xml"
/opt/palantir/services/staging_main/dispatchServer/bin/unix/kite.sh -c /home/palantir/kite-uds/Grantees-2012.xml -i 
local_end_time=$(date +%s)
diffsec="$(expr $local_end_time - $local_begin_time)"
echo | awk -v D=$diffsec '{printf " Elapsed time: %02d:%02d:%02d\n",D/(60*60),D%(60*60)/60,D%60}'


##local_begin_time=$(date +%s)
##echo " Running: kite for Contacts.xml"
##/opt/palantir/services/staging_main/dispatchServer/bin/unix/kite.sh -c /home/palantir/kite-uds/Contacts.xml -i 
##local_end_time=$(date +%s)
##diffsec="$(expr $local_end_time - $local_begin_time)"
##echo | awk -v D=$diffsec '{printf " Elapsed time: %02d:%02d:%02d\n",D/(60*60),D%(60*60)/60,D%60}'

##local_begin_time=$(date +%s)
##echo " Running: kite for ContactsClinicDir.xml"
##/opt/palantir/services/staging_main/dispatchServer/bin/unix/kite.sh -c /home/palantir/kite-uds/ContactsClinicDir.xml -i 
##local_end_time=$(date +%s)
##diffsec="$(expr $local_end_time - $local_begin_time)"
##echo | awk -v D=$diffsec '{printf " Elapsed time: %02d:%02d:%02d\n",D/(60*60),D%(60*60)/60,D%60}'

##local_begin_time=$(date +%s)
##echo " Running: kite for ContactsExecDir.xml"
##/opt/palantir/services/staging_main/dispatchServer/bin/unix/kite.sh -c /home/palantir/kite-uds/ContactsExecDir.xml -i 
##local_end_time=$(date +%s)
##diffsec="$(expr $local_end_time - $local_begin_time)"
##echo | awk -v D=$diffsec '{printf " Elapsed time: %02d:%02d:%02d\n",D/(60*60),D%(60*60)/60,D%60}'

local_begin_time=$(date +%s)
##echo " Running: kite for ContactsProjectDir2011.xml"
##/opt/palantir/services/staging_main/dispatchServer/bin/unix/kite.sh -c /home/palantir/kite-uds/ContactsProjectDir2011.xml -i 
echo " Running: kite for ContactsProjectDir2012.xml"
/opt/palantir/services/staging_main/dispatchServer/bin/unix/kite.sh -c /home/palantir/kite-uds/ContactsProjectDir2012.xml -i 
local_end_time=$(date +%s)
diffsec="$(expr $local_end_time - $local_begin_time)"
echo | awk -v D=$diffsec '{printf " Elapsed time: %02d:%02d:%02d\n",D/(60*60),D%(60*60)/60,D%60}'

local_begin_time=$(date +%s)
##echo " Running: kite for GranteeSite.xml"
##/opt/palantir/services/staging_main/dispatchServer/bin/unix/kite.sh -c /home/palantir/kite-uds/GranteeSite.xml -i 
echo " Running: kite for GranteeSites-2012.xml"
/opt/palantir/services/staging_main/dispatchServer/bin/unix/kite.sh -c /home/palantir/kite-uds/GrateeSites-2012.xml -i 
local_end_time=$(date +%s)
diffsec="$(expr $local_end_time - $local_begin_time)"
echo | awk -v D=$diffsec '{printf " Elapsed time: %02d:%02d:%02d\n",D/(60*60),D%(60*60)/60,D%60}'

##local_begin_time=$(date +%s)
##echo " Running: kite for Reports.xml"
##/opt/palantir/services/staging_main/dispatchServer/bin/unix/kite.sh -c /home/palantir/kite-uds/Reports.xml -i 
##local_end_time=$(date +%s)
##diffsec="$(expr $local_end_time - $local_begin_time)"
##echo | awk -v D=$diffsec '{printf " Elapsed time: %02d:%02d:%02d\n",D/(60*60),D%(60*60)/60,D%60}'

##local_begin_time=$(date +%s)
##echo " Running: kite for Reports-2011.xml"
##/opt/palantir/services/staging_main/dispatchServer/bin/unix/kite.sh -c /home/palantir/kite-uds/Reports-2011.xml -i 
##local_end_time=$(date +%s)
##diffsec="$(expr $local_end_time - $local_begin_time)"
##echo | awk -v D=$diffsec '{printf " Elapsed time: %02d:%02d:%02d\n",D/(60*60),D%(60*60)/60,D%60}'

local_begin_time=$(date +%s)
echo " Running: kite for Reports-2012.xml"
/opt/palantir/services/staging_main/dispatchServer/bin/unix/kite.sh -c /home/palantir/kite-uds/Reports-2012.xml -i 
local_end_time=$(date +%s)
diffsec="$(expr $local_end_time - $local_begin_time)"
echo | awk -v D=$diffsec '{printf " Elapsed time: %02d:%02d:%02d\n",D/(60*60),D%(60*60)/60,D%60}'

##local_begin_time=$(date +%s)
##echo " Running: kite for Reports-AddOn-2010.xml"
##/opt/palantir/services/staging_main/dispatchServer/bin/unix/kite.sh -c /home/palantir/kite-uds/Reports-AddOn-2010.xml -i 
##local_end_time=$(date +%s)
##diffsec="$(expr $local_end_time - $local_begin_time)"
##echo | awk -v D=$diffsec '{printf " Elapsed time: %02d:%02d:%02d\n",D/(60*60),D%(60*60)/60,D%60}'

##local_begin_time=$(date +%s)
##echo " Running: kite for ZipCodePatients.xml"
##/opt/palantir/services/staging_main/dispatchServer/bin/unix/kite.sh -c /home/palantir/kite-uds/ZipCodePatients.xml -i 
##local_end_time=$(date +%s)
##diffsec="$(expr $local_end_time - $local_begin_time)"
##echo | awk -v D=$diffsec '{printf " Elapsed time: %02d:%02d:%02d\n",D/(60*60),D%(60*60)/60,D%60}'

local_begin_time=$(date +%s)
echo " Running: kite for ZipCodePatients2012.xml"
/opt/palantir/services/staging_main/dispatchServer/bin/unix/kite.sh -c /home/palantir/kite-uds/ZipCodePatients2012.xml -i 
local_end_time=$(date +%s)
diffsec="$(expr $local_end_time - $local_begin_time)"
echo | awk -v D=$diffsec '{printf " Elapsed time: %02d:%02d:%02d\n",D/(60*60),D%(60*60)/60,D%60}'

##local_begin_time=$(date +%s)
##echo " Running: kite for ZipCodes.xml"
##/opt/palantir/services/staging_main/dispatchServer/bin/unix/kite.sh -c /home/palantir/kite-uds/ZipCodes.xml -i 
##local_end_time=$(date +%s)
##diffsec="$(expr $local_end_time - $local_begin_time)"
##echo | awk -v D=$diffsec '{printf " Elapsed time: %02d:%02d:%02d\n",D/(60*60),D%(60*60)/60,D%60}'

local_begin_time=$(date +%s)
echo " Running: kite for ZipCodes2012.xml"
/opt/palantir/services/staging_main/dispatchServer/bin/unix/kite.sh -c /home/palantir/kite-uds/ZipCodes2012.xml -i 
local_end_time=$(date +%s)
diffsec="$(expr $local_end_time - $local_begin_time)"
echo | awk -v D=$diffsec '{printf " Elapsed time: %02d:%02d:%02d\n",D/(60*60),D%(60*60)/60,D%60}'

##local_begin_time=$(date +%s)
##echo " Running: kite for Table3A.xml"
##/opt/palantir/services/staging_main/dispatchServer/bin/unix/kite.sh -c /home/palantir/kite-uds/Table3A.xml -i 
##local_end_time=$(date +%s)
##diffsec="$(expr $local_end_time - $local_begin_time)"
##echo | awk -v D=$diffsec '{printf " Elapsed time: %02d:%02d:%02d\n",D/(60*60),D%(60*60)/60,D%60}'

####local_begin_time=$(date +%s)
####echo " Running: kite for Table3A-2012.xml"
####/opt/palantir/services/staging_main/dispatchServer/bin/unix/kite.sh -c /home/palantir/kite-uds/Table3A-2012.xml -i 
####local_end_time=$(date +%s)
####diffsec="$(expr $local_end_time - $local_begin_time)"
####echo | awk -v D=$diffsec '{printf " Elapsed time: %02d:%02d:%02d\n",D/(60*60),D%(60*60)/60,D%60}'

##local_begin_time=$(date +%s)
##echo " Running: kite for Table3A-2011.xml"
##/opt/palantir/services/staging_main/dispatchServer/bin/unix/kite.sh -c /home/palantir/kite-uds/Table3A-2011.xml -i 
##local_end_time=$(date +%s)
##diffsec="$(expr $local_end_time - $local_begin_time)"
##echo | awk -v D=$diffsec '{printf " Elapsed time: %02d:%02d:%02d\n",D/(60*60),D%(60*60)/60,D%60}'

##local_begin_time=$(date +%s)
##echo " Running: kite for Table3B.xml"
##/opt/palantir/services/staging_main/dispatchServer/bin/unix/kite.sh -c /home/palantir/kite-uds/Table3B.xml -i
##local_end_time=$(date +%s)
##diffsec="$(expr $local_end_time - $local_begin_time)"
##echo | awk -v D=$diffsec '{printf " Elapsed time: %02d:%02d:%02d\n",D/(60*60),D%(60*60)/60,D%60}'

##local_begin_time=$(date +%s)
##echo " Running: kite for Table3B-2011.xml"
##/opt/palantir/services/staging_main/dispatchServer/bin/unix/kite.sh -c /home/palantir/kite-uds/Table3B-2011.xml -i
##local_end_time=$(date +%s)
##diffsec="$(expr $local_end_time - $local_begin_time)"
##echo | awk -v D=$diffsec '{printf " Elapsed time: %02d:%02d:%02d\n",D/(60*60),D%(60*60)/60,D%60}'

####local_begin_time=$(date +%s)
####echo " Running: kite for Table3B-2012.xml"
####/opt/palantir/services/staging_main/dispatchServer/bin/unix/kite.sh -c /home/palantir/kite-uds/Table3B-2012.xml -i
####local_end_time=$(date +%s)
####diffsec="$(expr $local_end_time - $local_begin_time)"
####echo | awk -v D=$diffsec '{printf " Elapsed time: %02d:%02d:%02d\n",D/(60*60),D%(60*60)/60,D%60}'


##local_begin_time=$(date +%s)
##echo " Running: kite for Table4.xml"
##/opt/palantir/services/staging_main/dispatchServer/bin/unix/kite.sh -c /home/palantir/kite-uds/Table4.xml -i
##local_end_time=$(date +%s)
##diffsec="$(expr $local_end_time - $local_begin_time)"
##echo | awk -v D=$diffsec '{printf " Elapsed time: %02d:%02d:%02d\n",D/(60*60),D%(60*60)/60,D%60}'

##local_begin_time=$(date +%s)
##echo " Running: kite for Table4-2011.xml"
##/opt/palantir/services/staging_main/dispatchServer/bin/unix/kite.sh -c /home/palantir/kite-uds/Table4-2011.xml -i
##local_end_time=$(date +%s)
##diffsec="$(expr $local_end_time - $local_begin_time)"
##echo | awk -v D=$diffsec '{printf " Elapsed time: %02d:%02d:%02d\n",D/(60*60),D%(60*60)/60,D%60}'

####local_begin_time=$(date +%s)
####echo " Running: kite for Table4-2012.xml"
####/opt/palantir/services/staging_main/dispatchServer/bin/unix/kite.sh -c /home/palantir/kite-uds/Table4-2012.xml -i
####local_end_time=$(date +%s)
####diffsec="$(expr $local_end_time - $local_begin_time)"
####echo | awk -v D=$diffsec '{printf " Elapsed time: %02d:%02d:%02d\n",D/(60*60),D%(60*60)/60,D%60}'


##local_begin_time=$(date +%s)
##echo " Running: kite for Table5.xml"
##/opt/palantir/services/staging_main/dispatchServer/bin/unix/kite.sh -c /home/palantir/kite-uds/Table5.xml -i
##local_end_time=$(date +%s)
##diffsec="$(expr $local_end_time - $local_begin_time)"
##echo | awk -v D=$diffsec '{printf " Elapsed time: %02d:%02d:%02d\n",D/(60*60),D%(60*60)/60,D%60}'

##local_begin_time=$(date +%s)
##echo " Running: kite for Table5-2011.xml"
##/opt/palantir/services/staging_main/dispatchServer/bin/unix/kite.sh -c /home/palantir/kite-uds/Table5-2011.xml -i 
##local_end_time=$(date +%s)
##diffsec="$(expr $local_end_time - $local_begin_time)"
##echo | awk -v D=$diffsec '{printf " Elapsed time: %02d:%02d:%02d\n",D/(60*60),D%(60*60)/60,D%60}'

####local_begin_time=$(date +%s)
####echo " Running: kite for Table5-2012.xml"
####/opt/palantir/services/staging_main/dispatchServer/bin/unix/kite.sh -c /home/palantir/kite-uds/Table5-2012.xml -i 
####local_end_time=$(date +%s)
####diffsec="$(expr $local_end_time - $local_begin_time)"
####echo | awk -v D=$diffsec '{printf " Elapsed time: %02d:%02d:%02d\n",D/(60*60),D%(60*60)/60,D%60}'


##local_begin_time=$(date +%s)
##echo " Running: kite for Table6A.xml"
##/opt/palantir/services/staging_main/dispatchServer/bin/unix/kite.sh -c /home/palantir/kite-uds/Table6A.xml -i  
##local_end_time=$(date +%s)
##diffsec="$(expr $local_end_time - $local_begin_time)"
##echo | awk -v D=$diffsec '{printf " Elapsed time: %02d:%02d:%02d\n",D/(60*60),D%(60*60)/60,D%60}'

##local_begin_time=$(date +%s)
##echo " Running: kite for Table6A-2011.xml"
##/opt/palantir/services/staging_main/dispatchServer/bin/unix/kite.sh -c /home/palantir/kite-uds/Table6A-2011.xml -i  
##local_end_time=$(date +%s)
##diffsec="$(expr $local_end_time - $local_begin_time)"
##echo | awk -v D=$diffsec '{printf " Elapsed time: %02d:%02d:%02d\n",D/(60*60),D%(60*60)/60,D%60}'

####local_begin_time=$(date +%s)
####echo " Running: kite for Table6A-2012.xml"
####/opt/palantir/services/staging_main/dispatchServer/bin/unix/kite.sh -c /home/palantir/kite-uds/Table6A-2012.xml -i  
####local_end_time=$(date +%s)
####diffsec="$(expr $local_end_time - $local_begin_time)"
####echo | awk -v D=$diffsec '{printf " Elapsed time: %02d:%02d:%02d\n",D/(60*60),D%(60*60)/60,D%60}'

##local_begin_time=$(date +%s)
##echo " Running: kite for Table6B.xml"
##/opt/palantir/services/staging_main/dispatchServer/bin/unix/kite.sh -c /home/palantir/kite-uds/Table6B.xml -i
##local_end_time=$(date +%s)
##diffsec="$(expr $local_end_time - $local_begin_time)"
##echo | awk -v D=$diffsec '{printf " Elapsed time: %02d:%02d:%02d\n",D/(60*60),D%(60*60)/60,D%60}'

##local_begin_time=$(date +%s)
##echo " Running: kite for Table6B-2011.xml"
##/opt/palantir/services/staging_main/dispatchServer/bin/unix/kite.sh -c /home/palantir/kite-uds/Table6B-2011.xml -i
##local_end_time=$(date +%s)
##diffsec="$(expr $local_end_time - $local_begin_time)"
##echo | awk -v D=$diffsec '{printf " Elapsed time: %02d:%02d:%02d\n",D/(60*60),D%(60*60)/60,D%60}'

####local_begin_time=$(date +%s)
####echo " Running: kite for Table6B-2012.xml"
####/opt/palantir/services/staging_main/dispatchServer/bin/unix/kite.sh -c /home/palantir/kite-uds/Table6B-2012.xml -i
####local_end_time=$(date +%s)
####diffsec="$(expr $local_end_time - $local_begin_time)"
####echo | awk -v D=$diffsec '{printf " Elapsed time: %02d:%02d:%02d\n",D/(60*60),D%(60*60)/60,D%60}'

##local_begin_time=$(date +%s)
##echo " Running: kite for Table7.xml"
##/opt/palantir/services/staging_main/dispatchServer/bin/unix/kite.sh -c /home/palantir/kite-uds/Table7.xml -i   
##local_end_time=$(date +%s)
##diffsec="$(expr $local_end_time - $local_begin_time)"
##echo | awk -v D=$diffsec '{printf " Elapsed time: %02d:%02d:%02d\n",D/(60*60),D%(60*60)/60,D%60}'

####local_begin_time=$(date +%s)
####echo " Running: kite for Table7-2012.xml"
####/opt/palantir/services/staging_main/dispatchServer/bin/unix/kite.sh -c /home/palantir/kite-uds/Table7-2012.xml -i   
####local_end_time=$(date +%s)
####diffsec="$(expr $local_end_time - $local_begin_time)"
####echo | awk -v D=$diffsec '{printf " Elapsed time: %02d:%02d:%02d\n",D/(60*60),D%(60*60)/60,D%60}'

global_end_time=$(date +%s)
diffsec="$(expr $global_end_time - $global_begin_time)"
echo | awk -v D=$diffsec '{printf "Global Elapsed time for UDS: %02d:%02d:%02d\n",D/(60*60),D%(60*60)/60,D%60}'

