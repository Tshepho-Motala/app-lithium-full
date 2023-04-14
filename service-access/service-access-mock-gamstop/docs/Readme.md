#Gamstop Mock Server

This a mock server for gamstop.

Swagger documentation of the API

http://localhost:10000/swagger-ui.html

By default, the endpoint has a default single record which returns a blocked response

{"firstName": "Daniel",
 "lastName": "Pasquier", 
 "dateOfBirth": "1945-05-29", 
 "email": "umaury@blanc.org", 
 "postcode": "PE166RY", 
 "mobile": "07700900000"}
 
###Batch Upload
http://localhost:10000/v2/upload

Upload records should get a blocked result.

####Request

[
    {
        "firstName": "John",
         "lastName": "Doe", 
         "dateOfBirth": "1945-05-29", 
         "email": "umaury@blanc.org", 
         "postcode": "PE166RY", 
         "mobile": "07700900000"
     },
    {
        "firstName": "Harry",
        "lastName": "Potter",
        "dateOfBirth": "1970-01-01", 
        "email": "harry.potter@example.com",
        "postcode": "HP11AA", 
        "mobile": "07700900004"
     }
   ]

####Response

[
  {
    "correlationId": null,
    "firstName": "Daniel",
    "lastName": "Pasquier",
    "dateOfBirth": "1945-05-29",
    "email": "umaury@blanc.org",
    "postcode": "PE166RY",
    "mobile": "07700900000"
  },
  {
    "correlationId": null,
    "firstName": "John",
    "lastName": "Doe",
    "dateOfBirth": "1945-05-29",
    "email": "umaury@blanc.org",
    "postcode": "PE166RY",
    "mobile": "07700900000"
  },
  {
    "correlationId": null,
    "firstName": "Harry",
    "lastName": "Potter",
    "dateOfBirth": "1970-01-01",
    "email": "harry.potter@example.com",
    "postcode": "HP11AA",
    "mobile": "07700900004"
  }
]

###Reset
http://localhost:10000/v2/reset

Reset url removed all the added record, only the default will be retained
