# how to
 1. create spring boot project with spring initializr https://start.spring.io/
 2. generate basic model for client account using copilot
 3. use lombok instead of annoying getter and setter, refresh intellij project
4. build project by calling maven clean install
5. java: package org.junit.jupiter.api does not exist -> femove for now tests - wrong folder structure was generated
6. start application by running main method in ClientAccountApplication.java or by maven spring-boot:run
7. test by curl command - e.g. in gitbash - be carefull for parsing of curl command (e.g. in windows cmd)
````
   curl -X POST http://localhost:8080/api/accounts/transaction -H "Content-Type: application/json" -d "{\"accountNumber\":\"123456789\",\"amount\":500.00,\"currency\":\"CZK\",\"operationDate\":\"2026-03-24\"}"
 ````
8. 
