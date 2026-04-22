# Ejecucion con Docker

Docker permite levantar la aplicacion Spring Boot y la base de datos MySQL sin configurar MySQL manualmente en cada computadora.

## Requisito

Instalar Docker Desktop y abrirlo antes de ejecutar los comandos.

## Levantar el proyecto

Desde la carpeta `demo`:

```bash
docker compose up --build
```

Cuando termine de iniciar, la aplicacion queda disponible en:

```text
http://localhost:8080
```

## Servicios creados

- `proyecto1_app`: aplicacion Spring Boot.
- `proyecto1_mysql`: base de datos MySQL.
- `mysql_data`: volumen donde Docker guarda los datos de MySQL.

## Base de datos

Docker crea automaticamente la base:

```text
proyecto1
```

La aplicacion se conecta internamente a MySQL usando:

```text
jdbc:mysql://mysql:3306/proyecto1
```

En la computadora, MySQL queda expuesto en el puerto `3307` para evitar choque con un MySQL local que use `3306`.

## Detener el proyecto

```bash
docker compose down
```

Si se quieren borrar tambien los datos guardados en MySQL:

```bash
docker compose down -v
```
