-- -- ## FACT MODELING SCRIPT FOR MINE-TB DATA WAREHOUSE ## -- --
-- Drop existing fact tables
drop table if exists fact_screening;
drop table if exists fact_sputumresults;
drop table if exists fact_treatment;
-- Create fact tables
create table fact_screening (fact_id int(11) not null auto_increment, year int(11) default null, month int(11) default null, week int(11) default null, quarter int(11) default null, strategy varchar(45) default null, province varchar(45) default null, district varchar(45) default null, facility varchar(45) default null, screener int(11) default null, screened varchar(45) default null, suspects int(11) default null, non_suspects int(11) default null, last_updated datetime default null, primary key (fact_id));
create table fact_sputumresults (fact_id int(11) not null auto_increment, time_id int(11) null default null, strategy varchar(45) null default null, province varchar(45) null default null, district varchar(45) null default null, facility varchar(45) null default null, screener varchar(45) null default null, submissions int null, results int null, pending int null, mtb_positives int null, rif_resistants int null, negatives int null, errors int null, rejected int null, no_results int null, last_updated datetime default null,primary key (fact_id));
create table fact_treatment (fact_id int(11) not null auto_increment, time_id int(11) null default null, strategy varchar(45) null default null, province varchar(45) null default null, district varchar(45) null default null, facility varchar(45) null default null, screener varchar(45) null default null, tx_initiations int null, drug_sensitives int null, drug_resistant int null, tx_pending int null, followups int null, tx_neg_outcomes int null, tx_other_outcomes int null, last_updated datetime null, primary key (fact_id));

-- Prepare community strategy screening data
drop table if exists community_data;
create table community_data 
select s.username as screener, t.year, t.month, t.week_of_year as week, t.quarter, pa.location as location_id, l.location_name as facility, l.city_village as district, l.state_province as province, 
	s.no_of_screening as screened, s.no_of_suspects as suspects, s.no_of_sputum_submitted as submissions, s.no_of_sputum_result as results, s.no_of_mtb_pos as mtb_positives, s.no_of_treatment_initiated as treatments_initiated from minetb_rpt.weekly_screener_summary as s
inner join minetb_rpt.working_weeks as w using (week_no)
inner join dim_user as u on u.username = s.username
inner join person_attribute_merged as pa on pa.person_id = u.person_id
inner join dim_location as l on l.location_id = pa.location
inner join dim_time as t on t.week_of_year = s.week_no;
-- TODO: Prepare facility strategy screening data

-- Insert Screening facts
-- Fill screening facts
insert into fact_screening
select 0, year, month, week, quarter, 'Community' as strategy, province, district, facility, screener, sum(screened) as screened, sum(suspects) as suspects, sum(screened - suspects) as non_suspects, now() as last_updated from community_data as d
group by year, month, week, quarter, province, district, facility, screener;

-- Fill sputum facts
insert into fact_sputumresults
select 0, year, month, week, quarter, 'Community' as strategy, province, district, facility, screener, sum(submissions) as submissions, sum(results) as results, 0 as pending, sum(mtb_positives) as mtb_positives, 0 as rif_resistants, 0 as negatives, 0 as errors, 0 as rejected, 0 as no_results, now() as last_updated from community_data as d
group by year, month, week, quarter, province, district, facility, screener;

-- Fill treatment facts
insert into fact_treatment
select 0, year, month, week, quarter, 'Community' as strategy, province, district, facility, screener, sum(treatments_initiated) as tx_initiations, 0 as drug_sensitives, 0 as drug_resistant, 0 as tx_pending, 0 as followups, 0 as tx_neg_outcomes, now() as last_updated from community_data as d
group by year, month, week, quarter, province, district, facility, screener;
