# PrimeNet Report
[![Java CI](https://github.com/querwurzel/mersenne-primenet-report/actions/workflows/main.yml/badge.svg)](https://github.com/querwurzel/mersenne-primenet-report/actions/workflows/main.yml)

GIMPS, the Great Internet Mersenne Prime Search, was founded in 1996 by George Woltman.
As the project grew, Scott Kurowski responded with the introduction of PrimeNet
and its ability to manage thousands of volunteers and millions of work assignments.
There are currently 51 known Mersenne primes, 17 discovered by GIMPS (_state 2019-02-23_).

As a GIMPS volunteer I wanted to observe and share my own results
while growing a database to perform statistics, time series analysis and means of data science.
  **This software collects the daily results of PrimeNet while offering an endpoint to publish your results.**

More information:
* [https://www.mersenne.org/various/history](https://www.mersenne.org/various/history.php)
* https://www.mersenne.org/primes
* https://en.wikipedia.org/wiki/Mersenne_prime

### Requirements

* JDK 21+
* MySQL 8+

### How to configure

Have a look into the [application-sample.properties](https://github.com/querwurzelt/mersenne-primenet-report/blob/master/src/main/resources/application-sample.properties)

*Copy and save* the file to `application-prod.properties` and modify the properties as required.

* port and ip address (default: localhost:8191)
    * `server.address`
    * `server.port`
* starting date of imports (YYYY-MM-DD; defaults to yesterday, 1997-11-11 is the very beginning)
    * `imports.start`
* own username for export endpoint (default: _ANONYMOUS_)
    * `imports.identity`
* MySQL connectivity, database, user, password (default: localhost:3306)
    * `spring.datasource.url`

### How to build

For productive use run:
```console
./mvnw -P prod package
```

### How to run

For productive use run:
```console
java -Dspring.profiles.active=prod -jar primenetreport.jar
```

### How to use

http://localhost:8191

* `/results`
    * publishes your results
* `/results/meta`
    * some meta data like number of imports, results, etc.

### License

Apache License 2.0
