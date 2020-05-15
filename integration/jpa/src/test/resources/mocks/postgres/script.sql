CREATE TABLE bpd_mcc_category
(
    mcc_category_id_s character varying NOT NULL,
    mcc_category_description_s character varying,
    multiplier_score_d numeric NOT NULL,
    CONSTRAINT bpd_mcc_category_pkey PRIMARY KEY (mcc_category_id_s)
);

CREATE TABLE bpd_mcc_category_rel
(
    mcc_s character varying NOT NULL,
    mcc_category_id_s character varying NOT NULL,
    CONSTRAINT bdp_mcc_category_rel_pk PRIMARY KEY (mcc_s)
);

INSERT INTO bpd_mcc_category(
	mcc_category_id_s, mcc_category_description_s, multiplier_score_d)
	VALUES ('0', 'test', 0.10);

INSERT INTO bpd_mcc_category(
	mcc_category_id_s, mcc_category_description_s, multiplier_score_d)
	VALUES ('1', 'test', 0.10);

INSERT INTO bpd_mcc_category_rel(
	mcc_s, mcc_category_id_s)
	VALUES ('0000', '0');

INSERT INTO bpd_mcc_category_rel(
	mcc_s, mcc_category_id_s)
	VALUES ('0001', '2');