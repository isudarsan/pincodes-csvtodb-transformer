DROP TABLE IF EXISTS pincode;


CREATE TABLE pincode(id BIGINT(20) NOT NULL AUTO_INCREMENT, 
                     village_name VARCHAR(255), 
                     office_name VARCHAR(255), 
                     pin_code VARCHAR(255), 
                     sub_district_name VARCHAR(255), 
                     district_name VARCHAR(255), 
                     state_name VARCHAR(255), PRIMARY KEY(id));