# api-softnet-challenge
Technical Challenge for Softnet

* El proyecto ha sido desarrollado con los siguientes frameworks
    - SpringBoot 3.2.0
    - Spring Data Reactive MongoDB
    - Spring Security
    - WebFlux
    - Spring Boot Actuator
    - Hibernate Validator
    - JSON Web Token
    - Lombok

---

# Arquitectura del software
![arquitectura](./recursos/Arquitectura_software.png)

---

# Data Base Set up
- Se necesitar tener instalado el MongoCLI en el ordenador y crear una base de datos que tenga de nombre 'challenge'.
- En caso se utilice otro puerto para la base de datos modificar el `application.properties`.

    spring.data.mongodb.uri= mongodb://localhost:27017/challenge
        


# Pasos para correr la aplicacion

Clonar el repositorio:

    $ git clone https://github.com/sebastian1907/api-softnet-challenge

* Es posible asignar valores al properties del entorno en tu archivo `application.properties`.

```bash
jwt.secret=${APP_SECURITY_JWT_SECRET_KEY}
jwt.expiration=${APP_SECURITY_JWT_EXPIRATION_TIME} 
```

- Donde:
    * __APP_SECURITY_JWT_SECRET_KEY__ -> secret key
    * __APP_SECURITY_JWT_EXPIRATION_TIME__ -> expiration time en segundos


* Y por ultimo correr la aplicacion de la siguiente manera.

```bash
APP_SECURITY_JWT_SECRET_KEY=AMDXGbO7gGwf3hoFPpm6GwQvFrqoCsn2
APP_SECURITY_JWT_EXPIRATION_TIME=28800
```
---
# Pruebas

### Crear Usuario:

    ```bash
    curl --location 'http://localhost:9000/public/api/user' \
    --header 'Content-Type: application/json' \
    --data '{
        "username": "admin",
        "password": "admin",
        "firstName": "Sebas",
        "lastName": "Prueba"
    }'
    ```




### Autenticación y creación de JWT valido:

    ```bash
    curl --location 'http://localhost:9000/public/api/login' \
    --header 'Content-Type: application/json' \
    --data '{
        "username": "admin",
        "password": "admin"
    }'
    ```



### Crear tipo de cambio:

    ```bash
    curl --location 'http://localhost:9000/api/exchange' \
    --header 'Content-Type: application/json' \
    --header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjpbIlJPTEVfVVNFUiJdLCJpc3MiOiJhZG1pbiIsInN1YiI6IjY1N2ExNmZmNWUyOTA4MDYzMzk3YTA4NSIsImlhdCI6MTcwMjUwMDU2NSwianRpIjoiZTFmM2YxNGUtYmFlZC00MTVhLTk5MmEtODM2YzNhOWNkMDc1IiwiZXhwIjoxNzAyNTI5MzY1fQ.Ieps3eTIcWN8b0VqU6oCJolJFCUmyxy0hZPmlNElQSQ' \
    --data '{
        "exchangeRate": 3.73,
        "origenCurrency": "PEN",
        "destinyCurrency": "USD"
    }'
    ```




### Listar tipo de cambio:

    ```bash
    curl --location 'http://localhost:9000/api/exchange' \
    --header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjpbIlJPTEVfVVNFUiJdLCJpc3MiOiJhZG1pbiIsInN1YiI6IjY1N2ExNmZmNWUyOTA4MDYzMzk3YTA4NSIsImlhdCI6MTcwMjUwMDU2NSwianRpIjoiZTFmM2YxNGUtYmFlZC00MTVhLTk5MmEtODM2YzNhOWNkMDc1IiwiZXhwIjoxNzAyNTI5MzY1fQ.Ieps3eTIcWN8b0VqU6oCJolJFCUmyxy0hZPmlNElQSQ'
    ```




### Buscar tipo de cambio por divisas:

    ```bash
    curl --location 'http://localhost:9000/api/exchange/PEN/EUR' \
    --header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjpbIlJPTEVfVVNFUiJdLCJpc3MiOiJhZG1pbiIsInN1YiI6IjY1N2ExNmZmNWUyOTA4MDYzMzk3YTA4NSIsImlhdCI6MTcwMjUwMDU2NSwianRpIjoiZTFmM2YxNGUtYmFlZC00MTVhLTk5MmEtODM2YzNhOWNkMDc1IiwiZXhwIjoxNzAyNTI5MzY1fQ.Ieps3eTIcWN8b0VqU6oCJolJFCUmyxy0hZPmlNElQSQ'
    ```




### Cambiar dinero a otra divisa

    ```bash
    curl --location --request GET 'http://localhost:9000/api/exchange/amount' \
    --header 'Content-Type: application/json' \
    --header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjpbIlJPTEVfVVNFUiJdLCJpc3MiOiJhZG1pbiIsInN1YiI6IjY1N2ExNmZmNWUyOTA4MDYzMzk3YTA4NSIsImlhdCI6MTcwMjUwMDU2NSwianRpIjoiZTFmM2YxNGUtYmFlZC00MTVhLTk5MmEtODM2YzNhOWNkMDc1IiwiZXhwIjoxNzAyNTI5MzY1fQ.Ieps3eTIcWN8b0VqU6oCJolJFCUmyxy0hZPmlNElQSQ' \
    --data '{
        "amount": 5000,
        "origenCurrency": "PEN",
        "destinyCurrency": "USD"
    }'
    ```




---
## Check health app 
```bash
http://localhost:9000/actuator/health
```
![health](./recursos/health_check.png)