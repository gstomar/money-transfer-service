# Money transfer Rest API

A Java RESTful API for money transfers between users accounts

### Technologies
- JAX-RS API
- Log4j
- Jetty Container (for Test and Demo app)
- Apache HTTP Client


### How to run
```sh
mvn exec:java
```

Application starts a jetty server on localhost port 8090
- http://localhost:8090/command/api/transfer

### Available Services

| HTTP METHOD | PATH | USAGE |
| -----------| ------ | ------ |
| POST | /command/api/transfer | perform transfer between 2 user accounts |


### Sample Rest call for money transfer
{  
   "ccyCode":"GBP",
   "amount":100000.0000,
   "fromAccountId":1,
   "toAccountId":2
}

