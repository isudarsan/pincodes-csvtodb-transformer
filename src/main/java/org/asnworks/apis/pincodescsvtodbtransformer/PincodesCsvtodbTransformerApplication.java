package org.asnworks.apis.pincodescsvtodbtransformer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.asnworks.apis.pincodescsvtodbtransformer.model.PinCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import com.google.common.collect.Lists;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;

@SpringBootApplication
public class PincodesCsvtodbTransformerApplication implements CommandLineRunner {

    private static Logger LOG = LoggerFactory.getLogger(PincodesCsvtodbTransformerApplication.class);

    public static final String CSV_FILE = "Locality_village_pincode_final_mar-2017.csv";

    @Autowired
    private JdbcTemplate jdbcTemplate;

	public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(PincodesCsvtodbTransformerApplication.class);
        springApplication.setBannerMode(Banner.Mode.OFF);
        springApplication.run(args);
	}

    @Override
    public void run(String... args) throws Exception {
        long lStartTime = System.nanoTime();

        LOG.info("Start :: run :: Creating table Pincode");
        jdbcTemplate.execute(
            "CREATE TABLE pincode(id BIGINT(20) NOT NULL AUTO_INCREMENT, village_name VARCHAR(255), office_name VARCHAR(255), pin_code VARCHAR(255), sub_district_name VARCHAR(255), district_name VARCHAR(255), state_name VARCHAR(255), PRIMARY KEY(id))");
        LOG.info("End :: run :: Created table Pincode");

        String sql =
            "INSERT INTO pincode (village_name, office_name, pin_code, sub_district_name, district_name, state_name) VALUES(?,?,?,?,?,?)";

        List<PinCode> list = transformPinCodeCSV();

        LOG.info("End :: run :: Inserting rows into the table : Pincode");
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public int getBatchSize() {
                return list.size();
            }

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                PinCode pinCode = list.get(i);
                ps.setString(1, pinCode.getVillageName());
                ps.setString(2, pinCode.getOfficeName());
                ps.setString(3, pinCode.getCode());
                ps.setString(4, pinCode.getSubDistrictName());
                ps.setString(5, pinCode.getDistrictName());
                ps.setString(6, pinCode.getStateName());
            }
        });
        LOG.info("End :: run :: Completed inserting rows into the table : Pincode");
        long lEndTime = System.nanoTime();
        long output = lEndTime - lStartTime;
        LOG.info("Elapsed time in milliseconds: {}", (output / 1000000));
    }

    public static List<PinCode> transformPinCodeCSV() {
        List<PinCode> list = Lists.newArrayList();
        LOG.info("Start :: Loading CSV records");
        Reader reader = getTextFileReader(CSV_FILE);
        CSVReader csvReader = new CSVReader(reader, '|', CSVParser.DEFAULT_QUOTE_CHARACTER, '\0');
        String[] line;
        try {
            line = csvReader.readNext();
            while ((line = csvReader.readNext()) != null) {
                String parts[] = line[0].split(",");
                PinCode pinCode = new PinCode(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]);
                list.add(pinCode);
            }
            csvReader.close();
        } catch (IOException e) {
            LOG.warn("Exception :: {}", e);
        } finally {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOG.warn("Exception : {}", e);
                }

        }
        LOG.info("End :: Loading CSV records Completed");
        LOG.info("End :: Total Number of records : {}", list.size());
        return list;
    }

    public static Reader getTextFileReader(String fileName) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);
        return new InputStreamReader(inputStream, Charset.forName("utf8"));
    }

}
