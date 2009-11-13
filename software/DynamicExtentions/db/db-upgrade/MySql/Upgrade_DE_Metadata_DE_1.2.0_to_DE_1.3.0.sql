create table DYEXTN_SKIP_LOGIC_ATTRIBUTE (IDENTIFIER bigint not null, SOURCE_SKIP_LOGIC_ID bigint, TARGET_SKIP_LOGIC_ID bigint, CAT_ATTR_ID bigint, PERM_VALUE_ID bigint, primary key (IDENTIFIER));

alter table DYEXTN_SKIP_LOGIC_ATTRIBUTE add index FK722F03989F433752 (SOURCE_SKIP_LOGIC_ID), add constraint FK722F03989F433752 foreign key (SOURCE_SKIP_LOGIC_ID) references DYEXTN_CATEGORY_ATTRIBUTE (IDENTIFIER);
alter table DYEXTN_SKIP_LOGIC_ATTRIBUTE add index FK722F039889907A48 (TARGET_SKIP_LOGIC_ID), add constraint FK722F039889907A48 foreign key (TARGET_SKIP_LOGIC_ID) references DYEXTN_CATEGORY_ATTRIBUTE (IDENTIFIER);
alter table DYEXTN_SKIP_LOGIC_ATTRIBUTE add index FK722F03985CC8694E (IDENTIFIER), add constraint FK722F03985CC8694E foreign key (IDENTIFIER) references DYEXTN_BASE_ABSTRACT_ATTRIBUTE (IDENTIFIER);
alter table DYEXTN_SKIP_LOGIC_ATTRIBUTE add index FK722F0398DB856DA (CAT_ATTR_ID), add constraint FK722F0398DB856DA foreign key (CAT_ATTR_ID) references DYEXTN_CATEGORY_ATTRIBUTE (IDENTIFIER);
alter table DYEXTN_SKIP_LOGIC_ATTRIBUTE add index FK722F0398B96D382 (PERM_VALUE_ID), add constraint FK722F0398B96D382 foreign key (PERM_VALUE_ID) references DYEXTN_PERMISSIBLE_VALUE (IDENTIFIER);
ALTER TABLE  DYEXTN_CONTROL ADD column SKIP_LOGIC bit default 0;
ALTER TABLE  DYEXTN_CATEGORY_ATTRIBUTE ADD column IS_SKIP_LOGIC bit default 0;
ALTER TABLE  DYEXTN_DATA_ELEMENT ADD column SKIP_LOGIC_ATTRIBUTE_ID bigint;
ALTER TABLE  DYEXTN_PERMISSIBLE_VALUE ADD column SKIP_LOGIC_ATTRIBUTE_ID bigint;
ALTER TABLE  DYEXTN_PERMISSIBLE_VALUE ADD column SKIP_LOGIC_CAT_ATTR_ID bigint;
ALTER TABLE  DYEXTN_CONTROL ADD column SKIP_LOGIC_TARGET_CONTROL bit default 0;
ALTER TABLE  DYEXTN_CONTROL ADD column SHOW_HIDE bit default 0;
ALTER TABLE  DYEXTN_CONTROL ADD column SELECTIVE_READ_ONLY bit default 0;
alter table DYEXTN_DATA_ELEMENT add index FKB1153E4791A6280 (SKIP_LOGIC_ATTRIBUTE_ID), add constraint FKB1153E4791A6280 foreign key (SKIP_LOGIC_ATTRIBUTE_ID) references DYEXTN_SKIP_LOGIC_ATTRIBUTE (IDENTIFIER);

alter table DYEXTN_PERMISSIBLE_VALUE add index FK136264E0668894B9 (SKIP_LOGIC_CAT_ATTR_ID), add constraint FK136264E0668894B9 foreign key (SKIP_LOGIC_CAT_ATTR_ID) references DYEXTN_CATEGORY_ATTRIBUTE (IDENTIFIER);
alter table DYEXTN_PERMISSIBLE_VALUE add index FK136264E0791A6280 (SKIP_LOGIC_ATTRIBUTE_ID), add constraint FK136264E0791A6280 foreign key (SKIP_LOGIC_ATTRIBUTE_ID) references DYEXTN_SKIP_LOGIC_ATTRIBUTE (IDENTIFIER);

ALTER TABLE DYEXTN_DATEPICKER ADD SHOWCALENDAR bit default 1;

create table DYEXTN_MULTISELECT_CHECK_BOX (IDENTIFIER bigint not null, MULTISELECT bit, primary key (IDENTIFIER));
alter table DYEXTN_MULTISELECT_CHECK_BOX add index FK4312896DBF67AB26 (IDENTIFIER), add constraint FK4312896DBF67AB26 foreign key (IDENTIFIER) references DYEXTN_SELECT_CONTROL (IDENTIFIER);
ALTER TABLE DYEXTN_CONTROL ADD column SOURCE_CONTROL_ID bigint;
alter table DYEXTN_CONTROL add index FK70FB5E80FEA07DFA (SOURCE_CONTROL_ID), add constraint FK70FB5E80FEA07DFA foreign key (SOURCE_CONTROL_ID) references DYEXTN_CONTROL (IDENTIFIER);

UPDATE DYEXTN_ABSTRACT_METADATA SET NAME='visitNumber' WHERE NAME='entryNumber';

update dyextn_date_type_info set format = 'DateOnly' where format like 'MM-dd-yyyy';
update dyextn_date_type_info set format = 'DateAndTime' where format like 'MM-dd-yyyy HH:mm';
update dyextn_date_type_info set format = 'DateOnly' where format is null;
update dyextn_date_type_info set format = 'DateAndTime' where format like 'yyyy-MM-dd-HH24.mm.ss.SSS';
update dyextn_date_type_info set format = 'MonthAndYear' where format like 'MM-yyyy';
update dyextn_date_type_info set format = 'YearOnly' where format like 'yyyy';

commit;
