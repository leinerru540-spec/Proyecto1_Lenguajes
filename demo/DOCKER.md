# Ejecucion con Docker

Docker permite levantar la aplicacion Spring Boot junto con la base de datos MySQL sin tener que instalar ni configurar MySQL manualmente en cada computadora.

## Requisito

Instalar Docker Desktop y asegurarse de que este abierto antes de ejecutar los comandos.

## Levantar el proyecto

Desde la carpeta `demo` ejecute:

```bash
docker compose up --build
```

Cuando el proceso termine de iniciar, la aplicacion estara disponible en:

```text
http://localhost:8080
```

## Servicios creados

- `api`: aplicacion Spring Boot.
- `db`: base de datos MySQL.
- `db_data`: volumen donde Docker guarda los datos de MySQL.

## Base de datos

Docker crea automaticamente la base de datos:

```text
proyecto1
```

Dentro de Docker, la aplicacion se conecta a MySQL usando el nombre del servicio:

```text
jdbc:mysql://db:3306/proyecto1
```

En la computadora, MySQL queda expuesto en el puerto `3307` para evitar conflictos con una instalacion local que use el puerto `3306`.

## Detener el proyecto

```bash
docker compose down
```

Si tambien desea eliminar los datos almacenados en MySQL:

```bash
docker compose down -v
```
