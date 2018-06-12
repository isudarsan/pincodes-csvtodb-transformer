package org.asnworks.apis.pincodescsvtodbtransformer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PincodesCsvtodbTransformerApplication implements CommandLineRunner {

    private static Logger LOG = LoggerFactory.getLogger(PincodesCsvtodbTransformerApplication.class);

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    Job job;

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(PincodesCsvtodbTransformerApplication.class);
        springApplication.setBannerMode(Banner.Mode.OFF);
        springApplication.run(args);
    }

    @Override
    public void run(String... arg0) throws Exception {
        try {
            JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
                .toJobParameters();
            LOG.info("Job Started");
            jobLauncher.run(job, jobParameters);
            LOG.info("Job Ended");
        } catch (Exception e) {
            LOG.info(e.getMessage());
        }
    }

}
