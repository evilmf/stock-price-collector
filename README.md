# stock-price-collector

Purpose:
The purpose of this project is to collect stock price by the minute from Google finance API. It should be scheduled to run once a day after market close.

Requirement:
Postgresql 9.6
Java 1.8

Instruction:
1. Modify the maven profile in parent pom to set the right database IP, user and password.
2. In the project root folder, run "mvn clean install -Pdev" to build.
3. After it finishes building, go to webapp folder and run "mvn spring-boot:run -Pdev".
4. Schedule cron expression is in the properties file.
