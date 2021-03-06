create table DYEXTN_CALCULATED_ATTRIBUTE (
	IDENTIFIER number(19,0) not null,
	SOURCE_CAT_ATTR_ID number(19,0),
	TARGET_CAL_ATTR_ID number(19,0),
        CAL_CATEGORY_ATTR_ID number(19,0),
	primary key (IDENTIFIER)
);
alter table DYEXTN_CALCULATED_ATTRIBUTE add constraint FKEF3B77585697A453 foreign key (CAL_CATEGORY_ATTR_ID) references DYEXTN_CATEGORY_ATTRIBUTE;