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
	VALUES ('0001', '1');