create sequence aa_sequence_aa_sequence_ii_seq;
create sequence analysis_analysis_ii_seq;
create sequence analysis_data_analysis_data_ii_seq;
create sequence analysis_type_analysis_type_ii_seq;
create sequence attribute_attribute_ii_seq;
create sequence attribute_group_attribute_group_ii_seq;
create sequence attribute_nominal_value_attribute_nominal_value_ii_seq;
create sequence combined_query_combined_query_ii_seq;
create sequence dataset_dataset_ii_seq;
create sequence drug_class_drug_class_ii_seq;
create sequence drug_commercial_drug_commercial_ii_seq;
create sequence drug_generic_drug_generic_ii_seq;
create sequence event_event_ii_seq;
create sequence event_nominal_value_event_nominal_value_ii_seq;
create sequence genome_genome_ii_seq;
create sequence nt_sequence_nt_sequence_ii_seq;
create sequence open_reading_frame_open_reading_frame_ii_seq;
create sequence patient_attribute_value_patient_attribute_value_ii_seq;
create sequence patient_event_value_patient_event_value_ii_seq;
create sequence patient_patient_ii_seq;
create sequence protein_protein_ii_seq;
create sequence query_definition_parameter_query_definition_parameter_ii_seq;
create sequence query_definition_parameter_type_query_definition_parameter_type_ii_seq;
create sequence query_definition_query_definition_ii_seq;
create sequence query_definition_run_parameter_query_definition_run_parameter_ii_seq;
create sequence query_definition_run_query_definition_run_ii_seq;
create sequence resistance_interpretation_template_resistance_interpretation_template_ii_seq;
create sequence splicing_position_splicing_position_ii_seq;
create sequence test_nominal_value_test_nominal_value_ii_seq;
create sequence test_object_test_object_ii_seq;
create sequence test_result_test_result_ii_seq;
create sequence test_test_ii_seq;
create sequence test_type_test_type_ii_seq;
create sequence therapy_motivation_therapy_motivation_ii_seq;
create sequence therapy_therapy_ii_seq;
create sequence user_attribute_user_attribute_ii_seq;
create sequence value_type_value_type_ii_seq;
create sequence viral_isolate_viral_isolate_ii_seq;
create table commercial_generic (generic_ii integer  not null, commercial_ii integer  not null, primary key (commercial_ii, generic_ii));
create table genome_drug_generic (genome_ii integer  not null, generic_ii integer  not null, primary key (generic_ii, genome_ii));
create table regadbschema.aa_insertion (insertion_position int2 not null, aa_sequence_ii integer  not null, insertion_order int2 not null, version integer  not null, aa_insertion varchar(30) not null, nt_insertion_codon varchar(3) not null, primary key (insertion_position, aa_sequence_ii, insertion_order));
create table regadbschema.aa_mutation (mutation_position int2 not null, aa_sequence_ii integer  not null, version integer  not null, aa_reference varchar(1) not null, aa_mutation varchar(30), nt_reference_codon varchar(3) not null, nt_mutation_codon varchar(3), primary key (mutation_position, aa_sequence_ii));
create table regadbschema.aa_sequence (aa_sequence_ii integer default nextval('aa_sequence_aa_sequence_ii_seq'), version integer  not null, nt_sequence_ii integer  not null, protein_ii integer  not null, first_aa_pos int2 not null, last_aa_pos int2 not null, primary key (aa_sequence_ii));
create table regadbschema.analysis (analysis_ii integer default nextval('analysis_analysis_ii_seq'), analysis_type_ii integer  not null, url varchar(100), account varchar(50), password varchar(100), baseinputfile varchar(50), baseoutputfile varchar(50), service_name varchar(100), dataoutputfile varchar(50), primary key (analysis_ii));
create table regadbschema.analysis_data (analysis_data_ii integer default nextval('analysis_data_analysis_data_ii_seq'), analysis_ii integer  not null, name varchar(50), data bytea, mimetype varchar(50) not null, primary key (analysis_data_ii));
create table regadbschema.analysis_type (analysis_type_ii integer default nextval('analysis_type_analysis_type_ii_seq'), type varchar(50), primary key (analysis_type_ii));
create table regadbschema.attribute (attribute_ii integer default nextval('attribute_attribute_ii_seq'), version integer  not null, value_type_ii integer , attribute_group_ii integer , name varchar(50) not null, primary key (attribute_ii));
create table regadbschema.attribute_group (attribute_group_ii integer default nextval('attribute_group_attribute_group_ii_seq'), version integer  not null, group_name varchar(50), primary key (attribute_group_ii));
create table regadbschema.attribute_nominal_value (nominal_value_ii integer default nextval('attribute_nominal_value_attribute_nominal_value_ii_seq'), version integer  not null, attribute_ii integer  not null, value varchar(100) not null, primary key (nominal_value_ii));
create table regadbschema.combined_query (combined_query_ii integer default nextval('combined_query_combined_query_ii_seq'), name varchar(50) not null, primary key (combined_query_ii));
create table regadbschema.combined_query_definition (combined_query_ii integer  not null, query_definition_ii integer  not null, number integer  not null, name varchar(50), primary key (combined_query_ii, query_definition_ii));
create table regadbschema.dataset (dataset_ii integer default nextval('dataset_dataset_ii_seq'), version integer  not null, uid varchar(50) not null, description varchar(50) not null, creation_date date not null, closed_date date, revision integer , primary key (dataset_ii));
create table regadbschema.dataset_access (uid varchar(50) not null, dataset_ii integer  not null, version integer  not null, permissions integer  not null, provider varchar(50) not null, primary key (uid, dataset_ii));
create table regadbschema.drug_class (drug_class_ii integer default nextval('drug_class_drug_class_ii_seq'), version integer  not null, class_id varchar(10) not null, class_name varchar(100) not null, resistance_table_order integer , primary key (drug_class_ii));
create table regadbschema.drug_commercial (commercial_ii integer default nextval('drug_commercial_drug_commercial_ii_seq'), version integer  not null, name varchar(100) not null, atc_code varchar(50), primary key (commercial_ii));
create table regadbschema.drug_generic (generic_ii integer default nextval('drug_generic_drug_generic_ii_seq'), version integer  not null, drug_class_ii integer  not null, generic_id varchar(10) not null, generic_name varchar(50) not null, resistance_table_order integer , atc_code varchar(50), primary key (generic_ii));
create table regadbschema.event (event_ii integer default nextval('event_event_ii_seq'), version integer  not null, value_type_ii integer , name varchar(50) not null, primary key (event_ii));
create table regadbschema.event_nominal_value (nominal_value_ii integer default nextval('event_nominal_value_event_nominal_value_ii_seq'), version integer  not null, event_ii integer  not null, value varchar(500) not null, primary key (nominal_value_ii));
create table regadbschema.genome (genome_ii integer default nextval('genome_genome_ii_seq'), version integer  not null, organism_name varchar(50) not null, organism_description varchar(500) not null, genbank_number varchar(50), primary key (genome_ii));
create table regadbschema.nt_sequence (nt_sequence_ii integer default nextval('nt_sequence_nt_sequence_ii_seq'), version integer  not null, viral_isolate_ii integer  not null, label varchar(50), sequence_date date, nucleotides text, primary key (nt_sequence_ii));
create table regadbschema.open_reading_frame (open_reading_frame_ii integer default nextval('open_reading_frame_open_reading_frame_ii_seq'), version integer  not null, genome_ii integer  not null, name varchar(50) not null, description varchar(500) not null, reference_sequence text not null, primary key (open_reading_frame_ii));
create table regadbschema.patient (patient_ii integer default nextval('patient_patient_ii_seq'), version integer  not null, patient_id varchar(50) not null, last_name varchar(50), first_name varchar(50), birth_date date, death_date date, primary key (patient_ii));
create table regadbschema.patient_attribute_value (patient_attribute_value_ii integer default nextval('patient_attribute_value_patient_attribute_value_ii_seq'), version integer  not null, attribute_ii integer  not null, patient_ii integer  not null, nominal_value_ii integer , value varchar(100), primary key (patient_attribute_value_ii));
create table regadbschema.patient_dataset (dataset_ii integer  not null, patient_ii integer  not null, primary key (dataset_ii, patient_ii));
create table regadbschema.patient_event_value (patient_event_value_ii integer default nextval('patient_event_value_patient_event_value_ii_seq'), version integer  not null, patient_ii integer  not null, nominal_value_ii integer , event_ii integer  not null, value varchar(100), start_date date, end_date date, primary key (patient_event_value_ii));
create table regadbschema.protein (protein_ii integer default nextval('protein_protein_ii_seq'), version integer  not null, open_reading_frame_ii integer  not null, abbreviation varchar(50) not null, full_name varchar(50), start_position integer  not null, stop_position integer  not null, primary key (protein_ii));
create table regadbschema.query_definition (query_definition_ii integer default nextval('query_definition_query_definition_ii_seq'), uid varchar(50), name varchar(50), description text, query text, query_type_ii integer  not null, primary key (query_definition_ii));
create table regadbschema.query_definition_parameter (query_definition_parameter_ii integer default nextval('query_definition_parameter_query_definition_parameter_ii_seq'), query_definition_parameter_type_ii integer , query_definition_ii integer , name varchar(50), primary key (query_definition_parameter_ii));
create table regadbschema.query_definition_parameter_type (query_definition_parameter_type_ii integer default nextval('query_definition_parameter_type_query_definition_parameter_type_ii_seq'), name varchar(100) not null unique, id integer  not null unique, primary key (query_definition_parameter_type_ii));
create table regadbschema.query_definition_run (query_definition_run_ii integer default nextval('query_definition_run_query_definition_run_ii_seq'), query_definition_ii integer , uid varchar(50), startdate date, enddate date, status integer , name varchar(100) not null, result varchar(100), primary key (query_definition_run_ii));
create table regadbschema.query_definition_run_parameter (query_definition_run_parameter_ii integer default nextval('query_definition_run_parameter_query_definition_run_parameter_ii_seq'), query_definition_parameter_ii integer , query_definition_run_ii integer , value varchar(50), primary key (query_definition_run_parameter_ii));
create table regadbschema.resistance_interpretation_template (template_ii integer default nextval('resistance_interpretation_template_resistance_interpretation_template_ii_seq'), name varchar(100), document bytea, filename varchar(100), primary key (template_ii));
create table regadbschema.settings_user (uid varchar(50) not null, version integer  not null, test_ii integer , dataset_ii integer , chart_width integer  not null, chart_height integer  not null, password varchar(50), email varchar(100), first_name varchar(50), last_name varchar(50), admin bool, enabled bool, primary key (uid));
create table regadbschema.splicing_position (splicing_position_ii integer default nextval('splicing_position_splicing_position_ii_seq'), version integer  not null, protein_ii integer  not null, nt_position integer  not null, primary key (splicing_position_ii));
create table regadbschema.test (test_ii integer default nextval('test_test_ii_seq'), version integer  not null, analysis_ii integer  unique, test_type_ii integer  not null, description varchar(50) not null, primary key (test_ii));
create table regadbschema.test_nominal_value (nominal_value_ii integer default nextval('test_nominal_value_test_nominal_value_ii_seq'), version integer  not null, test_type_ii integer  not null, value varchar(100) not null, primary key (nominal_value_ii));
create table regadbschema.test_object (test_object_ii integer default nextval('test_object_test_object_ii_seq'), version integer  not null, description varchar(50) not null, test_object_id integer , primary key (test_object_ii));
create table regadbschema.test_result (test_result_ii integer default nextval('test_result_test_result_ii_seq'), version integer  not null, test_ii integer  not null, generic_ii integer , viral_isolate_ii integer , nominal_value_ii integer , patient_ii integer , nt_sequence_ii integer , value varchar(50), test_date date, sample_id varchar(50), data bytea, primary key (test_result_ii));
create table regadbschema.test_type (test_type_ii integer default nextval('test_type_test_type_ii_seq'), version integer  not null, value_type_ii integer , genome_ii integer , test_object_ii integer  not null, description varchar(50) not null, primary key (test_type_ii));
create table regadbschema.therapy (therapy_ii integer default nextval('therapy_therapy_ii_seq'), version integer  not null, therapy_motivation_ii integer , patient_ii integer  not null, start_date date not null, stop_date date, comment varchar(50), primary key (therapy_ii));
create table regadbschema.therapy_commercial (therapy_ii integer  not null, commercial_ii integer  not null, version integer  not null, day_dosage_units float8, placebo bool not null, blind bool not null, frequency int8, primary key (therapy_ii, commercial_ii));
create table regadbschema.therapy_generic (therapy_ii integer  not null, generic_ii integer  not null, version integer  not null, day_dosage_mg float8, placebo bool not null, blind bool not null, frequency int8, primary key (therapy_ii, generic_ii));
create table regadbschema.therapy_motivation (therapy_motivation_ii integer default nextval('therapy_motivation_therapy_motivation_ii_seq'), version integer  not null, value varchar(50) not null, primary key (therapy_motivation_ii));
create table regadbschema.user_attribute (user_attribute_ii integer default nextval('user_attribute_user_attribute_ii_seq'), value_type_ii integer , uid varchar(50), name varchar(50), value varchar(100), data bytea, primary key (user_attribute_ii));
create table regadbschema.value_type (value_type_ii integer default nextval('value_type_value_type_ii_seq'), version integer  not null, description varchar(50) not null, minimum float8, maximum float8, multiple bool, primary key (value_type_ii));
create table regadbschema.viral_isolate (viral_isolate_ii integer default nextval('viral_isolate_viral_isolate_ii_seq'), version integer  not null, patient_ii integer  not null, sample_id varchar(50), sample_date date, primary key (viral_isolate_ii));
alter table commercial_generic add constraint "FK_commercial_generic_drug_commercial" foreign key (commercial_ii) references regadbschema.drug_commercial(commercial_ii) ON UPDATE CASCADE;
alter table commercial_generic add constraint "FK_commercial_generic_drug_generic" foreign key (generic_ii) references regadbschema.drug_generic(generic_ii) ON UPDATE CASCADE;
alter table genome_drug_generic add constraint "FK_genome_drug_generic_drug_generic" foreign key (generic_ii) references regadbschema.drug_generic(generic_ii) ON UPDATE CASCADE;
alter table genome_drug_generic add constraint "FK_genome_drug_generic_genome" foreign key (genome_ii) references regadbschema.genome(genome_ii) ON UPDATE CASCADE;
alter table regadbschema.aa_insertion add constraint "FK_regadbschema.aa_insertion_aa_sequence" foreign key (aa_sequence_ii) references regadbschema.aa_sequence(aa_sequence_ii) ON UPDATE CASCADE;
alter table regadbschema.aa_mutation add constraint "FK_regadbschema.aa_mutation_aa_sequence" foreign key (aa_sequence_ii) references regadbschema.aa_sequence(aa_sequence_ii) ON UPDATE CASCADE;
alter table regadbschema.aa_sequence add constraint "FK_regadbschema.aa_sequence_nt_sequence" foreign key (nt_sequence_ii) references regadbschema.nt_sequence(nt_sequence_ii) ON UPDATE CASCADE;
alter table regadbschema.aa_sequence add constraint "FK_regadbschema.aa_sequence_protein" foreign key (protein_ii) references regadbschema.protein(protein_ii) ON UPDATE CASCADE;
alter table regadbschema.analysis add constraint "FK_regadbschema.analysis_analysis_type" foreign key (analysis_type_ii) references regadbschema.analysis_type(analysis_type_ii) ON UPDATE CASCADE;
alter table regadbschema.analysis_data add constraint "FK_regadbschema.analysis_data_analysis" foreign key (analysis_ii) references regadbschema.analysis(analysis_ii) ON UPDATE CASCADE;
alter table regadbschema.attribute add constraint "FK_regadbschema.attribute_attribute_group" foreign key (attribute_group_ii) references regadbschema.attribute_group(attribute_group_ii) ON UPDATE CASCADE;
alter table regadbschema.attribute add constraint "FK_regadbschema.attribute_value_type" foreign key (value_type_ii) references regadbschema.value_type(value_type_ii) ON UPDATE CASCADE;
alter table regadbschema.attribute_nominal_value add constraint "FK_regadbschema.attribute_nominal_value_attribute" foreign key (attribute_ii) references regadbschema.attribute(attribute_ii) ON UPDATE CASCADE;
alter table regadbschema.combined_query_definition add constraint "FK_regadbschema.combined_query_definition_combined_query" foreign key (combined_query_ii) references regadbschema.combined_query(combined_query_ii) ON UPDATE CASCADE;
alter table regadbschema.combined_query_definition add constraint "FK_regadbschema.combined_query_definition_query_definition" foreign key (query_definition_ii) references regadbschema.query_definition(query_definition_ii) ON UPDATE CASCADE;
alter table regadbschema.dataset add constraint "FK_regadbschema.dataset_settings_user" foreign key (uid) references regadbschema.settings_user(uid) ON UPDATE CASCADE;
alter table regadbschema.dataset_access add constraint "FK_regadbschema.dataset_access_dataset" foreign key (dataset_ii) references regadbschema.dataset(dataset_ii) ON UPDATE CASCADE;
alter table regadbschema.dataset_access add constraint "FK_regadbschema.dataset_access_settings_user" foreign key (uid) references regadbschema.settings_user(uid) ON UPDATE CASCADE;
alter table regadbschema.drug_generic add constraint "FK_regadbschema.drug_generic_drug_class" foreign key (drug_class_ii) references regadbschema.drug_class(drug_class_ii) ON UPDATE CASCADE;
alter table regadbschema.event add constraint "FK_regadbschema.event_value_type" foreign key (value_type_ii) references regadbschema.value_type(value_type_ii) ON UPDATE CASCADE;
alter table regadbschema.event_nominal_value add constraint "FK_regadbschema.event_nominal_value_event" foreign key (event_ii) references regadbschema.event(event_ii) ON UPDATE CASCADE;
alter table regadbschema.nt_sequence add constraint "FK_regadbschema.nt_sequence_viral_isolate" foreign key (viral_isolate_ii) references regadbschema.viral_isolate(viral_isolate_ii) ON UPDATE CASCADE;
alter table regadbschema.open_reading_frame add constraint "FK_regadbschema.open_reading_frame_genome" foreign key (genome_ii) references regadbschema.genome(genome_ii) ON UPDATE CASCADE;
alter table regadbschema.patient_attribute_value add constraint "FK_regadbschema.patient_attribute_value_attribute" foreign key (attribute_ii) references regadbschema.attribute(attribute_ii) ON UPDATE CASCADE;
alter table regadbschema.patient_attribute_value add constraint "FK_regadbschema.patient_attribute_value_attribute_nominal_value" foreign key (nominal_value_ii) references regadbschema.attribute_nominal_value(nominal_value_ii) ON UPDATE CASCADE;
alter table regadbschema.patient_attribute_value add constraint "FK_regadbschema.patient_attribute_value_patient" foreign key (patient_ii) references regadbschema.patient(patient_ii) ON UPDATE CASCADE;
alter table regadbschema.patient_dataset add constraint "FK_regadbschema.patient_dataset_dataset" foreign key (dataset_ii) references regadbschema.dataset(dataset_ii) ON UPDATE CASCADE;
alter table regadbschema.patient_dataset add constraint "FK_regadbschema.patient_dataset_patient" foreign key (patient_ii) references regadbschema.patient(patient_ii) ON UPDATE CASCADE;
alter table regadbschema.patient_event_value add constraint "FK_regadbschema.patient_event_value_event" foreign key (event_ii) references regadbschema.event(event_ii) ON UPDATE CASCADE;
alter table regadbschema.patient_event_value add constraint "FK_regadbschema.patient_event_value_event_nominal_value" foreign key (nominal_value_ii) references regadbschema.event_nominal_value(nominal_value_ii) ON UPDATE CASCADE;
alter table regadbschema.patient_event_value add constraint "FK_regadbschema.patient_event_value_patient" foreign key (patient_ii) references regadbschema.patient(patient_ii) ON UPDATE CASCADE;
alter table regadbschema.protein add constraint "FK_regadbschema.protein_open_reading_frame" foreign key (open_reading_frame_ii) references regadbschema.open_reading_frame(open_reading_frame_ii) ON UPDATE CASCADE;
alter table regadbschema.query_definition add constraint "FK_regadbschema.query_definition_settings_user" foreign key (uid) references regadbschema.settings_user(uid) ON UPDATE CASCADE;
alter table regadbschema.query_definition_parameter add constraint "FK_query_definition_parameter_query_definition_parameter_type" foreign key (query_definition_parameter_type_ii) references regadbschema.query_definition_parameter_type(query_definition_parameter_type_ii) ON UPDATE CASCADE;
alter table regadbschema.query_definition_parameter add constraint "FK_regadbschema.query_definition_parameter_query_definition" foreign key (query_definition_ii) references regadbschema.query_definition(query_definition_ii) ON UPDATE CASCADE;
alter table regadbschema.query_definition_run add constraint "FK_regadbschema.query_definition_run_query_definition" foreign key (query_definition_ii) references regadbschema.query_definition(query_definition_ii) ON UPDATE CASCADE;
alter table regadbschema.query_definition_run add constraint "FK_regadbschema.query_definition_run_settings_user" foreign key (uid) references regadbschema.settings_user(uid) ON UPDATE CASCADE;
alter table regadbschema.query_definition_run_parameter add constraint "FK_query_definition_run_parameter_query_definition_parameter" foreign key (query_definition_parameter_ii) references regadbschema.query_definition_parameter(query_definition_parameter_ii) ON UPDATE CASCADE;
alter table regadbschema.query_definition_run_parameter add constraint "FK_query_definition_run_parameter_query_definition_run" foreign key (query_definition_run_ii) references regadbschema.query_definition_run(query_definition_run_ii) ON UPDATE CASCADE;
alter table regadbschema.settings_user add constraint "FK_regadbschema.settings_user_dataset" foreign key (dataset_ii) references regadbschema.dataset(dataset_ii) ON UPDATE CASCADE;
alter table regadbschema.settings_user add constraint "FK_regadbschema.settings_user_test" foreign key (test_ii) references regadbschema.test(test_ii) ON UPDATE CASCADE;
alter table regadbschema.splicing_position add constraint "FK_regadbschema.splicing_position_protein" foreign key (protein_ii) references regadbschema.protein(protein_ii) ON UPDATE CASCADE;
alter table regadbschema.test add constraint "FK_regadbschema.test_analysis" foreign key (analysis_ii) references regadbschema.analysis(analysis_ii) ON UPDATE CASCADE;
alter table regadbschema.test add constraint "FK_regadbschema.test_test_type" foreign key (test_type_ii) references regadbschema.test_type(test_type_ii) ON UPDATE CASCADE;
alter table regadbschema.test_nominal_value add constraint "FK_regadbschema.test_nominal_value_test_type" foreign key (test_type_ii) references regadbschema.test_type(test_type_ii) ON UPDATE CASCADE;
alter table regadbschema.test_result add constraint "FK_regadbschema.test_result_drug_generic" foreign key (generic_ii) references regadbschema.drug_generic(generic_ii) ON UPDATE CASCADE;
alter table regadbschema.test_result add constraint "FK_regadbschema.test_result_nt_sequence" foreign key (nt_sequence_ii) references regadbschema.nt_sequence(nt_sequence_ii) ON UPDATE CASCADE;
alter table regadbschema.test_result add constraint "FK_regadbschema.test_result_patient" foreign key (patient_ii) references regadbschema.patient(patient_ii) ON UPDATE CASCADE;
alter table regadbschema.test_result add constraint "FK_regadbschema.test_result_test" foreign key (test_ii) references regadbschema.test(test_ii) ON UPDATE CASCADE;
alter table regadbschema.test_result add constraint "FK_regadbschema.test_result_test_nominal_value" foreign key (nominal_value_ii) references regadbschema.test_nominal_value(nominal_value_ii) ON UPDATE CASCADE;
alter table regadbschema.test_result add constraint "FK_regadbschema.test_result_viral_isolate" foreign key (viral_isolate_ii) references regadbschema.viral_isolate(viral_isolate_ii) ON UPDATE CASCADE;
alter table regadbschema.test_type add constraint "FK_regadbschema.test_type_genome" foreign key (genome_ii) references regadbschema.genome(genome_ii) ON UPDATE CASCADE;
alter table regadbschema.test_type add constraint "FK_regadbschema.test_type_test_object" foreign key (test_object_ii) references regadbschema.test_object(test_object_ii) ON UPDATE CASCADE;
alter table regadbschema.test_type add constraint "FK_regadbschema.test_type_value_type" foreign key (value_type_ii) references regadbschema.value_type(value_type_ii) ON UPDATE CASCADE;
alter table regadbschema.therapy add constraint "FK_regadbschema.therapy_patient" foreign key (patient_ii) references regadbschema.patient(patient_ii) ON UPDATE CASCADE;
alter table regadbschema.therapy add constraint "FK_regadbschema.therapy_therapy_motivation" foreign key (therapy_motivation_ii) references regadbschema.therapy_motivation(therapy_motivation_ii) ON UPDATE CASCADE;
alter table regadbschema.therapy_commercial add constraint "FK_regadbschema.therapy_commercial_drug_commercial" foreign key (commercial_ii) references regadbschema.drug_commercial(commercial_ii) ON UPDATE CASCADE;
alter table regadbschema.therapy_commercial add constraint "FK_regadbschema.therapy_commercial_therapy" foreign key (therapy_ii) references regadbschema.therapy(therapy_ii) ON UPDATE CASCADE;
alter table regadbschema.therapy_generic add constraint "FK_regadbschema.therapy_generic_drug_generic" foreign key (generic_ii) references regadbschema.drug_generic(generic_ii) ON UPDATE CASCADE;
alter table regadbschema.therapy_generic add constraint "FK_regadbschema.therapy_generic_therapy" foreign key (therapy_ii) references regadbschema.therapy(therapy_ii) ON UPDATE CASCADE;
alter table regadbschema.user_attribute add constraint "FK_regadbschema.user_attribute_settings_user" foreign key (uid) references regadbschema.settings_user(uid) ON UPDATE CASCADE;
alter table regadbschema.user_attribute add constraint "FK_regadbschema.user_attribute_value_type" foreign key (value_type_ii) references regadbschema.value_type(value_type_ii) ON UPDATE CASCADE;
alter table regadbschema.viral_isolate add constraint "FK_regadbschema.viral_isolate_patient" foreign key (patient_ii) references regadbschema.patient(patient_ii) ON UPDATE CASCADE;
create index aa_sequence_nt_sequence_ii_idx on regadbschema.aa_sequence (nt_sequence_ii);
create index aa_sequence_protein_ii_idx on regadbschema.aa_sequence (protein_ii);
create index analysis_analysis_type_ii_idx on regadbschema.analysis (analysis_type_ii);
create index analysis_data_analysis_ii_idx on regadbschema.analysis_data (analysis_ii);
create index attribute_attribute_group_ii_idx on regadbschema.attribute (attribute_group_ii);
create index attribute_nominal_value_attribute_ii_idx on regadbschema.attribute_nominal_value (attribute_ii);
create index attribute_value_type_ii_idx on regadbschema.attribute (value_type_ii);
create index combined_query_definition_combined_query_ii_idx on regadbschema.combined_query_definition (combined_query_ii);
create index combined_query_definition_query_definition_ii_idx on regadbschema.combined_query_definition (query_definition_ii);
create index dataset_uid_idx on regadbschema.dataset (uid);
create index drug_generic_drug_class_ii_idx on regadbschema.drug_generic (drug_class_ii);
create index event_nominal_value_event_ii_idx on regadbschema.event_nominal_value (event_ii);
create index event_value_type_ii_idx on regadbschema.event (value_type_ii);
create index nt_sequence_viral_isolate_ii_idx on regadbschema.nt_sequence (viral_isolate_ii);
create index open_reading_frame_genome_ii_idx on regadbschema.open_reading_frame (genome_ii);
create index patient_attribute_value_attribute_ii_idx on regadbschema.patient_attribute_value (attribute_ii);
create index patient_attribute_value_nominal_value_ii_idx on regadbschema.patient_attribute_value (nominal_value_ii);
create index patient_attribute_value_patient_ii_idx on regadbschema.patient_attribute_value (patient_ii);
create index patient_event_value_event_ii_idx on regadbschema.patient_event_value (event_ii);
create index patient_event_value_nominal_value_ii_idx on regadbschema.patient_event_value (nominal_value_ii);
create index patient_event_value_patient_ii_idx on regadbschema.patient_event_value (patient_ii);
create index protein_open_reading_frame_ii_idx on regadbschema.protein (open_reading_frame_ii);
create index query_definition_parameter_query_definition_ii_idx on regadbschema.query_definition_parameter (query_definition_ii);
create index query_definition_parameter_query_definition_parameter_type_ii_idx on regadbschema.query_definition_parameter (query_definition_parameter_type_ii);
create index query_definition_run_parameter_query_definition_parameter_ii_idx on regadbschema.query_definition_run_parameter (query_definition_parameter_ii);
create index query_definition_run_parameter_query_definition_run_ii_idx on regadbschema.query_definition_run_parameter (query_definition_run_ii);
create index query_definition_run_query_definition_ii_idx on regadbschema.query_definition_run (query_definition_ii);
create index query_definition_run_uid_idx on regadbschema.query_definition_run (uid);
create index query_definition_uid_idx on regadbschema.query_definition (uid);
create index settings_user_dataset_ii_idx on regadbschema.settings_user (dataset_ii);
create index settings_user_test_ii_idx on regadbschema.settings_user (test_ii);
create index splicing_position_protein_ii_idx on regadbschema.splicing_position (protein_ii);
create index test_analysis_ii_idx on regadbschema.test (analysis_ii);
create index test_nominal_value_test_type_ii_idx on regadbschema.test_nominal_value (test_type_ii);
create index test_result_generic_ii_idx on regadbschema.test_result (generic_ii);
create index test_result_nominal_value_ii_idx on regadbschema.test_result (nominal_value_ii);
create index test_result_nt_sequence_ii_idx on regadbschema.test_result (nt_sequence_ii);
create index test_result_patient_ii_idx on regadbschema.test_result (patient_ii);
create index test_result_test_ii_idx on regadbschema.test_result (test_ii);
create index test_result_viral_isolate_ii_idx on regadbschema.test_result (viral_isolate_ii);
create index test_test_type_ii_idx on regadbschema.test (test_type_ii);
create index test_type_genome_ii_idx on regadbschema.test_type (genome_ii);
create index test_type_test_object_ii_idx on regadbschema.test_type (test_object_ii);
create index test_type_value_type_ii_idx on regadbschema.test_type (value_type_ii);
create index therapy_patient_ii_idx on regadbschema.therapy (patient_ii);
create index therapy_therapy_motivation_ii_idx on regadbschema.therapy (therapy_motivation_ii);
create index user_attribute_uid_idx on regadbschema.user_attribute (uid);
create index user_attribute_value_type_ii_idx on regadbschema.user_attribute (value_type_ii);
create index viral_isolate_patient_ii_idx on regadbschema.viral_isolate (patient_ii);
