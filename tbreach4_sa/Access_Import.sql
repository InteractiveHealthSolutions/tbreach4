-- 1. Open the Xlsx file and replace all commas with period
-- 2. Replace all non-ASCII characters with ASCII (e.g. in record 3790)
-- 3. Execute the queries below
-- Create new table
drop table if exists _minetb;
create table _minetb (
uid int not null, form_date varchar(255), district varchar(255), facility varchar(255), screener varchar(255), username varchar(255), nhls_id varchar(255),
surname varchar(255), given_name varchar(255), dob_day varchar(255), dob_month varchar(255), dob_year varchar(255), age varchar(255), gender varchar(255),
address varchar(255), phone1 varchar(255), phone2 varchar(255), contact varchar(255), last_hiv varchar(255), diabetes varchar(255), 
sputum_collect_day varchar(255), sputum_collect_month varchar(255), sputum_collect_year varchar(255), sputum_results_day varchar(255), sputum_results_month varchar(255), sputum_results_year varchar(255), sputum_result varchar(255), 
mdr varchar(255), tx_start_day varchar(255), tx_start_month varchar(255), tx_start_year varchar(255), died_before_tx varchar(255), lost_fup varchar(255), transferred varchar(255), transfer_to varchar(255), 
primary key (uid));
-- Load data from CSV
load data infile 'E:\\MINETB_Import.csv' into table _minetb
fields terminated by '\,' enclosed by '' escaped by ''
lines terminated by '\n'  ignore 1 lines;
-- Get age from year of birth (if available)
update _minetb set age = year(current_date()) - dob_year - (case when dob_year > 15 then 1900 else 2000 end) where dob_year <> '';
-- Select transformed data into a new table
set @id := 1013201;
drop table if exists minetb;
create table minetb 
select concat('0', @id:=@id+1) as patient_id, m.uid, convert(date_format(m.form_date, '%d-%m-%y'), date) as form_date, convert(date_format(m.form_date, '%d-%m-%y'), date) as time_stamp, 
(case m.district when 1 then '01000 Ugu' when 2 then '02000 eThekwini' else m.district end) as district_name, 
(case m.facility 
when 1 then '01001 Port Shepstone Hospital' when 2 then '01005 St. Andrews Hospital' 
when 3 then '01006 GJ Crookes Hospital' when 4 then '01003 Murchison Hospital' 
when 5 then '01007 Gamalakhe CHC' when 6 then '01008 Turton CHC' 
when 7 then '01002 Port Shepstone Clinic' when 8 then '01004 Murchison Gateway' 
when 9 then '01013 Ntabeni Clinic' when 10 then '01012 Ezingolweni Clinic' 
when 11 then '01009 Umzinto Clinic' when 12 then '01010 Marburg Clinic' 
when 13 then '01011 Margate Clinic' when 14 then '01014 Dududu Clinic' 
when 15 then '01015 TLC Clinic' when 16 then '01018 Assisi Clinic'
when -99 then 'IHS' else '' end) as facility_name, 
(case m.screener when 1 then '01001' when 2 then '01002' when 3 then '01003' 
when 4 then '' -- 04;"Nokubonga Mkhize" the ID needs to be filled
when 5 then '01004' when 6 then '01005' when 7 then '01006' when 8 then '01007' 
when 9 then '01008' when 10 then '01009' when 11 then '01010' when 12 then '01011' 
when 13 then '01021' when 14 then '01012' when 15 then '01013' when 16 then '01014' 
when 17 then '01015' when 18 then '01016' when 19 then '01017' else 'Rachel' end) as screener_id, 
(case m.username when 1 then 'bongani' when 2 then 'Saba' when 3 then 'Amal' when 4 then 'Rachel' when 5 then 'Lauren' when 6 then 'akhona' else 'Rachel' end) as user_id, 
nhls_id, given_name as first_name, surname, concat(dob_day, '-', dob_month, '-', dob_year) as dob, age, gender, 
address as address, 'Port Shepstone' as city, 'South Africa' as country, phone1, 
(case contact when 1 then 'Yes' when 2 then 'No' else 'Unknown' end) as tb_contact, 
(case last_hiv when 1 then 'Yes' when 2 then 'No' else 'Unknown' end) as hiv_positive, 
(case diabetes when 1 then 'Yes' when 2 then 'No' else 'Unknown' end) as diabetes, 
concat(sputum_collect_day, '-', sputum_collect_month, '-', sputum_collect_year) as sputum_collection_date, 
concat(sputum_results_day, '-', sputum_results_month, '-', sputum_results_year) as sputum_result_date, 
(case sputum_result when 1 then 'MTB Positive' when 2 then 'MTB Negative' when 3 then 'Leaked' when 4 then 'Insufficient Amount' when 5 then 'Unsuccessful' when 6 then 'Lab Rejected' when 7 then 'Empty' else 'Results Not Available' end) as sputum_result, 
(case mdr when 1 then 'Detected' when 2 then 'Not Detected' else 'Unknown' end) as rif, 
concat(tx_start_day, '-', tx_start_month, '-', tx_start_year) as treatment_start_date, 
(case died_before_tx when 'TRUE' then 'Yes' when 'FALSE' then 'No' else 'Unknown' end) as died, 
(case lost_fup when 'TRUE' then 'Yes' when 'FALSE' then 'No' else 'Unknown' end) as lost_followup, 
(case transferred when 'TRUE' then 'Yes' when 'FALSE' then 'No' else 'Unknown' end) as transferred, transfer_to, 'Suspect' as suspect
from _minetb as m 
limit 10000;
-- Turn all incorrect DOBs to null
update minetb set dob = '' where dob like '-%' or dob like '%-' or dob like '%--%';
-- Fix missing genders
update minetb set gender = 'male' where first_name = 'Madoda' and gender = '';
update minetb set gender = 'female' where first_name in ('Celeni', 'Mandisa', 'Harietta', 'Nomalizo', 'Nomangesi', 'Lina', 'Zinhle') and gender = '';
-- Turn all incorrect dates to empty
update minetb set sputum_collection_date = '' where sputum_collection_date like '-%' or sputum_collection_date like '%-' or sputum_collection_date like '%--%';
update minetb set sputum_result_date = '' where sputum_result_date like '-%' or sputum_result_date like '%-' or sputum_result_date like '%--%';
update minetb set treatment_start_date = '' where treatment_start_date like '-%' or treatment_start_date like '%-' or treatment_start_date like '%--%';
-- Set form date is sputum collection date when available
update ignore minetb set form_date = ifnull(convert(date_format(sputum_collection_date, '%d-%m-%y'), date), form_date) where sputum_collection_date <> '';
-- Delete all records with missing given_name, family_name, gender or birth
select * from minetb where gender = '' or first_name = '' or surname = '' or age = '';
delete from minetb where gender = '' or first_name = '' or surname = '' or age = '';
-- Export data to CSV
select 'patient_id', 'uid', 'form_date', 'time_stamp', 'district_name', 'facility_name', 'screener_id', 'user_id', 'nhls_id', 'first_name', 'surname', 'dob', 'age', 'gender', 'address', 'city', 'country', 'phone1', 'tb_contact', 'hiv_positive', 'diabetes', 'sputum_collection_date', 'sputum_result_date', 'sputum_result', 'rif', 'treatment_start_date', 'died', 'lost_followup', 'transferred', 'transfer_to', 'suspect'
union
select * from minetb into outfile 'E:\\MINETB_Clean.csv'
fields terminated by ',' enclosed by '' lines terminated by '\n';

