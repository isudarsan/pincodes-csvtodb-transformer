[![Codacy Badge](https://api.codacy.com/project/badge/Grade/785720ef21e840c6b11542c63ac517a3)](https://www.codacy.com/app/sudarsan.a/pincodes-csvtodb-transformer?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=isudarsan/pincodes-csvtodb-transformer&amp;utm_campaign=Badge_Grade)

# pincodes-csvtodb-transformer

Spring Batch is one of the Spring frameworks that provides functions for processing large volumes of data in batch jobs. That includes logging, transaction management, job restart, job skip, statistics, and many others.

In this application, the requeirement is to strore pincodes related data from CSV file into MySQL database. Inorder to achive this I have used spring batch.

### Deployment using Docker

As a first step, run MySQL in Docker container with the below command.

`docker run --name mysql-pincodes-containar -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=pincodes_db -e MYSQL_USER=zarvis -e MYSQL_PASSWORD=password -d mysql:5.6`

Now that we have defined the Dockerfile, build a docker image for our application. Type the following command from the root directory of the project to build the docker image

`docker build -t pincodes-csvtodb-transformer .`

Once we have a docker image, run and link with MySQL database container which we already create below command.

`docker run --name pincodes-csvtodb-transformer --link mysql-pincodes-containar -d pincodes-csvtodb-transformer`

The docker image is available at : https://hub.docker.com/r/isudarsan/zarvis-apps/tags/

Image can be pulled directly from Docker Hub using `docker pull isudarsan/zarvis-apps:pincodes-csvtodb-transformer-0.0.1-SNAPSHOT`

@mentions (https://data.gov.in/) (https://docs.spring.io/spring-batch/trunk/reference/html/spring-batch-intro.html)
