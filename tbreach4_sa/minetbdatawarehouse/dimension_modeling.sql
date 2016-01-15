-- -- ## DIMENSTION MODELING SCRIPT FOR MINE-TB DATA WAREHOUSE ## -- --
-- Create a table to record systems
drop table if exists dim_systems;
create table dim_systems select 1 as system_id, 'OpenMRS' as system_name;
-- Denormalize concept tables and create a dimension
drop table if exists dim_concept;
create table dim_concept ( surrogate_id bigint(21) auto_increment, system_id int(1) not null default '0', concept_id int(11) not null, full_name varchar(255) default '', concept varchar(255) default '', description text, retired tinyint(1) not null default '0', data_type varchar(255) not null default '', class varchar(255) not null default '', creator int(11) not null default '0', date_created datetime not null, version int(11) default null, changed_by int(11), date_changed datetime, uuid char(38) default null, primary key surrogate_id (surrogate_id), unique key concept_id (concept_id), unique key `uuid_unique` (`uuid`)) engine=InnoDB default charset=utf8;
insert into dim_concept select 0, 1, c.concept_id, n1.name as full_name, n2.name as concept, d.description, c.retired, dt.name as data_type, cl.name as class, c.creator, c.date_created, c.version, c.changed_by, c.date_changed, c.uuid from concept as c inner join concept_datatype as dt on dt.concept_datatype_id = c.datatype_id inner join concept_class as cl on cl.concept_class_id = c.class_id left outer join concept_name as n1 on n1.concept_id = c.concept_id and n1.locale = 'en' and n1.voided = 0 and n1.concept_name_type = 'FULLY_SPECIFIED' left outer join concept_name as n2 on n2.concept_id = c.concept_id and n2.locale = 'en' and n2.voided = 0 and n2.concept_name_type <> 'FULLY_SPECIFIED' left outer join concept_description as d on d.concept_id = c.concept_id;
-- Optionally change True/False to Yes/No
update dim_concept set full_name = 'Yes', concept = 'Yes' where concept_id = 1;
update dim_concept set full_name = 'No', concept = 'No' where concept_id = 2;
-- Denormalize location tables and create a dimension
drop table if exists dim_location;
create table dim_location ( surrogate_id bigint(21) auto_increment, system_id int(1) not null default '0', location_id int(11) not null, location_name varchar(255) not null default '', description varchar(255) default null, address1 varchar(255) default null, address2 varchar(255) default null, city_village varchar(255) default null, state_province varchar(255) default null, postal_code varchar(50) default null, country varchar(50) default null, latitude varchar(50) default null, longitude varchar(50) default null, creator int(11) not null default '0', date_created datetime not null, retired tinyint(1) not null default '0', parent_location int(11) default null, uuid char(38) default null, primary key surrogate_id (surrogate_id), unique key location_id (location_id), unique key uuid_unique (uuid)) engine=InnoDB default charset=utf8;
insert into dim_location select 0, 1, l.location_id, l.name as location_name, l.description, l.address1, l.address2, l.city_village, l.state_province, l.postal_code, l.country, l.latitude, l.longitude, l.creator, l.date_created, l.retired, l.parent_location, l.uuid from location as l;
-- Set preferred to 1 for people with single entries
update patient_identifier set preferred = 1 where patient_id not in (select t.patient_id from (select patient_id, count(*) as total from patient_identifier group by patient_id having total > 1) as t);
-- Pick only the latest records to avoid cases where there are multiple 
drop table if exists patient_latest_identifier;
create table patient_latest_identifier select * from patient_identifier as a having a.patient_id = (select max(patient_id) from patient_identifier where patient_id = a.patient_id and preferred = 1 and voided = 0);
alter table patient_latest_identifier add primary key patient_id (patient_id);
-- Set preferred to 1 for people with single entries
update person_name set preferred = 1 where person_id not in (select t.person_id from (select person_id, count(*) as total from person_name group by person_id having total > 1) as t);
-- Pick only the latest records to avoid cases where there are multiple preferred
drop table if exists person_latest_name;
create table person_latest_name select * from person_name as a having a.person_name_id = (select max(person_name_id) from person_name where person_id = a.person_id and preferred = 1);
alter table person_latest_name add primary key person_name_id (person_name_id);
alter table person_latest_name add unique key person_id (person_id);
-- Set preferred to 1 for people with single entries
update person_address set preferred = 1 where person_id not in (select t.person_id from (select person_id, count(*) as total from person_address group by person_id having total > 1) as t);
-- Pick only the latest records to avoid cases where there are multiple preferred
drop table if exists person_latest_address;
create table person_latest_address select * from person_address as a having a.person_address_id = (select max(person_address_id) from person_address where person_id = a.person_id and preferred = 1);
alter table person_latest_address add primary key person_address_id (person_address_id);
alter table person_latest_address add unique key person_id (person_id);
-- Transpose attributes
drop table if exists person_attribute_merged;
create table person_attribute_merged select a.person_id, group_concat(if(a.person_attribute_type_id = 1, a.value, null)) as race, group_concat(if(a.person_attribute_type_id = 2, a.value, null)) as birthplace, group_concat(if(a.person_attribute_type_id = 3, a.value, null)) as citizenship, group_concat(if(a.person_attribute_type_id = 4, a.value, null)) as mother_name, group_concat(if(a.person_attribute_type_id = 5, a.value, null)) as civil_status, group_concat(if(a.person_attribute_type_id = 6, a.value, null)) as health_district, group_concat(if(a.person_attribute_type_id = 7, a.value, null)) as health_center, group_concat(if(a.person_attribute_type_id = 8, a.value, null)) as primary_mobile, group_concat(if(a.person_attribute_type_id = 9, a.value, null)) as secondary_mobile, group_concat(if(a.person_attribute_type_id = 10, a.value, null)) as primary_phone, group_concat(if(a.person_attribute_type_id = 11, a.value, null)) as secondary_phone, group_concat(if(a.person_attribute_type_id = 12, a.value, null)) as primary_mobile_owner, group_concat(if(a.person_attribute_type_id = 13, a.value, null)) as secondary_mobile_owner, group_concat(if(a.person_attribute_type_id = 14, a.value, null)) as primary_phone_owner, group_concat(if(a.person_attribute_type_id = 15, a.value, null)) as secondary_phone_owner, group_concat(if(a.person_attribute_type_id = 12, a.value, null)) as suspect_status, group_concat(if(a.person_attribute_type_id = 13, a.value, null)) as location from person_attribute as a where a.voided = 0 group by a.person_id;
alter table person_attribute_merged add primary key person_id (person_id);
-- Denormalize patient tables and create dimension
drop table if exists dim_patient;
create table dim_patient ( surrogate_id bigint(20) not null auto_increment, system_id int(11) not null, patient_id int(11) not null, identifier varchar(50) not null default '', gender varchar(50) default '', birthdate date default null, birthdate_estimated tinyint(1) not null default '0', dead tinyint(1) not null default '0', first_name varchar(50) default null, middle_name varchar(50) default null, last_name varchar(50) default null, race text, birthplace text, citizenship text, mother_name text, civil_status text, health_district text, health_center text, primary_mobile text, secondary_mobile text, primary_phone text, secondary_phone text, primary_mobile_owner text, secondary_mobile_owner text, primary_phone_owner text, secondary_phone_owner text,address1 text, address2 text, city_village text, state_province text,postal_code text, country text, creator int(11) not null default '0', date_created datetime not null, changed_by int(11) default null, date_changed datetime default null, voided tinyint(1) not null default '0', uuid char(38) default null, primary key (surrogate_id), unique key patient_id_unique (patient_id), unique key uuid_unique (uuid), key identifier (identifier)) engine=InnoDB default charset=utf8;
insert into dim_patient select 0, 1, p.patient_id, i.identifier, pr.gender, pr.birthdate, pr.birthdate_estimated, pr.dead, n.given_name as first_name, n.middle_name, n.family_name as last_name, pa.race, pa.birthplace, pa.citizenship, pa.mother_name, pa.civil_status, pa.health_district, pa.health_center, pa.primary_mobile, pa.secondary_mobile, pa.primary_phone, pa.secondary_phone, pa.primary_mobile_owner, pa.secondary_mobile_owner, pa.primary_phone_owner, pa.secondary_phone_owner, a.address1, a.address2, a.city_village, a.state_province, a.postal_code, a.country, p.creator, p.date_created, p.changed_by, p.date_changed, p.voided, pr.uuid from patient as p inner join patient_identifier as i on i.patient_id = p.patient_id and i.identifier_type = 1 inner join person as pr on pr.person_id = p.patient_id left outer join person_latest_name as n on n.person_id = pr.person_id and n.preferred = 1 left outer join person_latest_address as a on a.person_id = p.patient_id left outer join person_attribute_merged as pa on pa.person_id = p.patient_id where p.voided = 0;
-- Denormalize user tables and create dimension
drop table if exists dim_user;
create table dim_user ( surrogate_id int(1) not null auto_increment, system_id int(1) not null, user_id int(11) not null, username varchar(50) not null, person_id int(11) not null, identifier varchar(255) default null, secret_question varchar(255) default null, secret_answer varchar(255) default null, creator int(11) not null default '0', date_created datetime not null, changed_by int(11) default null, date_changed datetime default null, retired tinyint(1) not null default '0', retire_reason varchar(255) default null, uuid char(38) not null, primary key (surrogate_id), unique key user_id_unique (user_id), unique key username_unique (username), unique key person_id_unique (person_id)) engine=InnoDB default charset=utf8;
insert ignore into dim_user select 0, 1, u.user_id, u.username, u.person_id, p.identifier, u.secret_question, u.secret_answer, u.creator, u.date_created, u.changed_by, u.date_changed, u.retired, u.retire_reason, u.uuid from users as u left outer join provider as p on p.person_id = u.person_id and p.retired = 0;
-- Denormalize encounter tables and create dimension
drop table if exists dim_encounter;
create table dim_encounter ( surrogate_id bigint(21) not null auto_increment, system_id int(1) not null default '0', encounter_id int(11) not null, encounter_type int(11) not null, encounter_name varchar(50) not null default '', description text, patient_id int(11) not null default '0', location_id int(11) default null, provider varchar(255) default null, date_entered datetime not null, changed_by int(11) default null, date_changed datetime default null, creator int(11) default null, date_start datetime, date_end datetime, uuid char(38) not null, primary key (surrogate_id), unique key encounter_id (encounter_id), unique key uuid_unique (uuid), key patient_id (patient_id), key location_id (location_id), key provider (provider)) engine=InnoDB default charset=utf8;
insert ignore into dim_encounter select 0, 1, e.encounter_id, e.encounter_type, et.name as encounter_name, et.description, e.patient_id, e.location_id, p.identifier as provider, e.encounter_datetime as date_entered, e.creator, e.date_created as date_start, e.changed_by, e.date_changed, e.date_created as date_end, e.uuid from encounter as e inner join encounter_type as et on et.encounter_type_id = e.encounter_type left outer join encounter_provider as ep on ep.encounter_id = e.encounter_id left outer join provider as p on p.person_id = ep.provider_id;
-- Denormalize observations and create dimension
drop table if exists dim_obs;
create table dim_obs ( surrogate_id int(11) not null auto_increment, system_id int(11) not null default '0', encounter_id int(11) default null, encounter_type int(11) default null, patient_id int(11) default null, identifier varchar(50) default null, provider varchar(50) default null, obs_id int(11) not null, concept_id int(11) not null default '0', question varchar(255) default '', obs_datetime datetime not null, location_id int(11) default null, answer longtext, value_boolean tinyint(1) default null, value_coded int(11) default null, value_datetime datetime default null, value_numeric double default null, value_text text, creator int(11) not null default '0', date_created datetime not null, voided tinyint(1) not null default '0', uuid char(38) default null, primary key (surrogate_id), unique key obs_id_unique (obs_id), key identifier (identifier), key encounter_type (encounter_type), key concept_id (concept_id), key question (question), key location_id (location_id)) engine=InnoDB default charset=utf8;
insert into dim_obs select 0, 1, e.encounter_id, e.encounter_type, e.patient_id, p.identifier, e.provider, o.obs_id, o.concept_id, c.full_name as question, obs_datetime, o.location_id, concat(ifnull(o.value_boolean, ''), ifnull(ifnull(c2.concept, c2.full_name), ''), ifnull(o.value_datetime, ''), ifnull(o.value_numeric, ''), ifnull(o.value_text, '')) as answer, o.value_boolean, o.value_coded, o.value_datetime, o.value_numeric, o.value_text, o.creator, o.date_created, o.voided, o.uuid from obs as o inner join dim_concept as c on c.concept_id = o.concept_id inner join dim_encounter as e on e.encounter_id = o.encounter_id inner join dim_patient as p on p.patient_id = e.patient_id left outer join dim_concept as c2 on c2.concept_id = o.value_coded;
-- create dimension for time
drop table if exists dim_time;
create table dim_time (time_id int(11) not null auto_increment, date_start datetime not null, date_end datetime not null, year int(11) not null, month int(11) not null, week_of_year int(11) not null, quarter int(11) not null, month_name varchar(50) not null, primary key (time_id)) engine=InnoDB default charset=utf8;
