-- -- ## FACT MODELING SCRIPT FOR MINE-TB DATA WAREHOUSE ## -- --
-- Drop existing fact tables
drop table if exists fact_screening;
drop table if exists fact_sputumresults;
drop table if exists fact_treatment;
-- Create fact tables
create table fact_screening		(fact_id int(11) not null auto_increment, year int(11) default null, month int(11) default null, week int(11) default null, quarter int(11) default null, strategy varchar(45) default null, province varchar(45) default null, district varchar(45) default null, facility varchar(45) default null, screener varchar(45) default null, screened varchar(45) default null, suspects int(11) default null, non_suspects int(11) default null, last_updated datetime default null, primary key (fact_id));
create table fact_sputumresults	(fact_id int(11) not null auto_increment, year int(11) default null, month int(11) default null, week int(11) default null, quarter int(11) default null, strategy varchar(45) default null, province varchar(45) default null, district varchar(45) default null, facility varchar(45) default null, screener varchar(45) default null, submissions int null, results int null, pending int null, mtb_positives int null, rif_resistants int null, negatives int null, errors int null, rejected int null, no_results int null, last_updated datetime default null,primary key (fact_id));
create table fact_treatment		(fact_id int(11) not null auto_increment, year int(11) default null, month int(11) default null, week int(11) default null, quarter int(11) default null, strategy varchar(45) default null, province varchar(45) default null, district varchar(45) default null, facility varchar(45) default null, screener varchar(45) default null, tx_initiations int null, drug_sensitive int null, drug_resistant int null, tx_pending int null, followups int null, tx_neg_outcomes int null, tx_other_outcomes int null, last_updated datetime null, primary key (fact_id));

-- Insert Screening facts
insert into fact_screening
select 0 as fact_id, c.full_name as strategy, d.year, d.month, d.week, d.quarter, d.province, d.district, d.facility, d.username as screener, count(patient_id) as screened, sum(if(d.suspect_status = 'Suspect', 1, 0)) as suspects, sum(if(d.suspect_status = 'Non-Suspect', 1, 0)) as non_suspects, now() as last_updated from screening_data as d
inner join dim_user as u on u.username = d.username 
inner join person_attribute_merged as pa on pa.person_id = u.person_id 
inner join dim_concept as c on c.concept_id = pa.screening_type 
group by pa.screening_type, year, month, week, quarter, province, district, facility, screener;

-- Fill sputum facts
-- insert into fact_sputumresults
-- select 0, year, month, week, quarter, 'Community' as strategy, province, district, facility, screener, sum(submissions) as submissions, sum(results) as results, 0 as pending, sum(mtb_positives) as mtb_positives, 0 as rif_resistants, 0 as negatives, 0 as errors, 0 as rejected, 0 as no_results, now() as last_updated from community_data as d
-- group by year, month, week, quarter, province, district, facility, screener;

-- Fill treatment facts
-- insert into fact_treatment
-- select 0, year, month, week, quarter, 'Community' as strategy, province, district, facility, screener, sum(treatments_initiated) as tx_initiations, 0 as drug_sensitive, 0 as drug_resistant, 0 as tx_pending, 0 as followups, 0 as tx_neg_outcomes, 0 as tx_other_outcomes, now() as last_updated from community_data as d
-- group by year, month, week, quarter, province, district, facility, screener;
