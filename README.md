# progra3-tp

## Descripción

Este proyecto implementa un sistema para la gestión de **ciudades, rutas y agentes**, utilizando **Spring Boot** y **Neo4j** como base de datos orientada a grafos.

Se incluyen diversas operaciones como:

- Gestión de ciudades (crear, listar, buscar por nombre o ID)
- Gestión de rutas entre ciudades (agregar, actualizar, eliminar, listar)
- Gestión de agentes (crear, asignar ciudad, consultar energía y tareas)

El proyecto está pensado para aplicar los algoritmos vistos en la cursada de Programación 3, aprovechando las relaciones de Neo4j.

---

## Requisitos

- Java 17 o superior
- Maven 3.8+
- Docker (opcional, para levantar Neo4j localmente)
- Postman o cualquier cliente HTTP para probar la API

Al utilizar Neo4j de forma local (Docker), ejecutar en la terminal:

```bash
docker run \
  --name neo4j-local \
  -p 7474:7474 -p 7687:7687 \
  -d \
  -e NEO4J_AUTH=neo4j/<TuContrasenaSegura> \
  neo4j:latest
```

Luego, crear un archivo .env en la raiz del proyecto con las siguientes propiedades:

```bash
NEO4J_URI=bolt://localhost:7687
NEO4J_USERNAME=neo4j
NEO4J_PASSWORD=<TuContrasenaSegura>
```

Archivo application.properties:

```bash
spring.application.name=TP
spring.neo4j.uri=${NEO4J_URI}
spring.neo4j.authentication.username=${NEO4J_USERNAME}
spring.neo4j.authentication.password=${NEO4J_PASSWORD}
```

El backend quedará disponible en:

http://localhost:8080/api
