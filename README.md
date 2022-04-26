# Introduction
This Spring boot application uses a H2 in memory database and uses maven to manage dependencies.

## How to run
Go to Application.java and run the main method or run 'mvn package' to create the .war file. 
The application has a swagger endpoint which can be found here http://localhost:8080/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config

## Tests
See BookRepoControllerTest.java for Unit/Integration tests.

## Scope of changes 
The rest API concentrates on delivering the requirement to create a system capable of 
"adding new books, removing books and allowing members to loan/return a book". Beyond that immediate requirement
the rest API is quite limited, for example the API only allows you to add an author but it does not allow you to edit/update
an author.
Validation has also been omitted. 
