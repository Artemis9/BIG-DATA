The kite scripts should be run the following order:

Grantees.xml					-- HRSA Grantees since 2000 until 2011 (geo-coded, about 1,232 records, 312 UDS only, 920 to be merged) 
GranteeSite.xml					-- HRSA Grantee Sites since 2000 until 2011 (geo-coded, 17,739 - 1,232= 16,507)

Contacts.xml					-- Grantee contacts since 2000 until 2010 new naming 3,358
ContactsClinicDir.xml			-- Grantee Clinic Directors since 2000 until 2010	 7,001-3,358 = 3,643
ContactsExecDir.xml				-- Grantee Executive Directors since 2000 until 2010 8,748 - 7,001 = 1,747
ContactsProjectDir2011.xml		-- Grantee Project (HRSA UDS) Directors in 2011 (non-existent in prior years) 8877-8748= 129

Contact Totals : 10,498 down to --> 8,877

Reports.xml						-- UDS Report headers information since 2000 until 2010 (10,481) 
Reports-2011.xml				-- UDS Report headers and summary information for 2011 (11,609 - 10,481 = 1,128) 

/********************** The report add ons, zip codes, and zip code patients are properties; they do not create new objects                **********************/

Reports-AddOn-2000-2010.xml		-- UDS Report summary information since 2008 until 2010
ZipCodePatients.xml				-- Grantee Zip codes and patients from these Zip codes (geo-coded, per report, may change every year)
ZipCodes.xml					-- Grantee Zip codes  (per report, not geo-coded, may change every year)

/********************** The rest create table objects (89,847 - 48,733 = 41,114 table objects w/ 2011 Table 7 which should add 1,128 more) **********************/
/********************** Derives are added as properties so they do not increase the number of objects                                      **********************/
Table3A.xml						-- Table 3A contents since 2000 until 2010.
Table3A-2011.xml				-- Table 3A contents in 2011.
Table3ADerives.xml				-- Table 3A derives since 2000 until 2010. (OMITTED FROM FIRST LOAD)

Table3B.xml						-- Table 3B contents since 2000 until 2010.
Table3B-2011.xml				-- Table 3B contents in 2011.

Table3BDerives.xml				-- Table 3B derives since 2000 until 2010. (OMITTED FROM FIRST LOAD)

Table4.xml						-- Table 4 contents until 2010.
Table4-2011.xml					-- Table 4 contents in 2011.
Table4Derives.xml				-- Table 4 derives since 2000 until 2010. (OMITTED FROM FIRST LOAD)

Table5.xml						-- Table 5 contents until 2010.
Table5-2011.xml					-- Table 5 contents in 2011.

Table6A.xml						-- Table 6A contents until 2010.
Table6A-2011.xml				-- Table 6A contents in 2011.
Table6ADerives.xml				-- Table 6A derives since 2000 until 2010. (OMITTED FROM FIRST LOAD)

Table6B.xml						-- Table 6B contents until 2010.
Table6B-2011.xml				-- Table 6B contents in 2011.

Table7.xml						-- Table 7 contents  until 2010
Table7-2011.xml					-- Table 7 contents in 2011. (NOT COMPLETE)					