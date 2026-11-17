# Casos de prueba — SimOS

## Ejecución

```bash
mvn -q compile
mvn -q exec:java -Dexec.mainClass=simos.Main
```

También se puede empaquetar y ejecutar el jar:

```bash
mvn -q package
java -jar target/simos.jar
```

## Usuarios iniciales

| Usuario | Clave | Rol |
|---------|-------|-----|
| `admin` | `admin123` | Administrador |
| `usuario` | `usuario123` | Usuario |

Las claves se almacenan como hash SHA-256 en `src/main/resources/usuarios.txt`, con el
formato `usuario;hashSHA256;rol`. Solo el Administrador tiene el permiso de Seguridad.

## Casos de la fase actual (seguridad)

### S1 — Login de administrador

- Entrada: `admin` / `admin123`.
- Esperado: mensaje de bienvenida con rol Administrador y menú principal.
- Bitácora: `LOGIN;PERMITIDO` para `admin`.

### S2 — Login de usuario estándar

- Entrada: `usuario` / `usuario123`.
- Esperado: mensaje de bienvenida con rol Usuario y menú principal.
- Bitácora: `LOGIN;PERMITIDO` para `usuario`.

### S3 — Clave incorrecta

- Entrada: `admin` / `clave-mala`.
- Esperado: "Credenciales inválidas" y descuento de un intento. Tras 3 fallos el
  programa termina sin abrir el menú principal.
- Bitácora: un registro `LOGIN;DENEGADO` por cada intento fallido.

### S4 — Usuario intenta abrir Seguridad (permiso denegado)

- Entrada: sesión con `usuario`, opción `5`.
- Esperado: "Acceso denegado..." y regreso al menú principal sin abrir el módulo.
- Bitácora: `ABRIR MÓDULO 5 - SEGURIDAD;DENEGADO`.
- El módulo no debe ejecutarse ni mostrar su menú interno.

### S5 — Administrador consulta Seguridad (permitido)

- Entrada: sesión con `admin`, opción `5`.
- Esperado: menú del módulo con las opciones de listar usuarios y consultar bitácora.
- Bitácora: `ABRIR MÓDULO 5 - SEGURIDAD;PERMITIDO`.
- La opción 1 debe listar `admin` y `usuario` sin mostrar hashes ni claves.
- La opción 2 debe mostrar los últimos 20 registros con fecha/hora, usuario, acción y
  resultado.

## Formato de la bitácora

Se genera `bitacora.txt` en la raíz del proyecto (no se versiona):

```
fecha/hora;usuario;acción;resultado
2026-11-13 16:45:02;admin;LOGIN;PERMITIDO
```

## Casos de la fase actual (CPU)

### C1 — Comparación FCFS vs Round Robin

- Entrada: sesión con `admin`, opción `1`, opción `2` (leer `procesos.txt`), quantum `3`.
- Datos: P1(0,8), P2(1,4), P3(2,9), P4(3,5), P5(6,2).
- Esperas FCFS: P1=0, P2=7, P3=10, P4=18, P5=20 → promedio **11.00**.
- Esperas RR: P1=17, P2=13, P3=17, P4=15, P5=9 → promedio **14.20**.
- Gantt RR: `| P1 | P2 | P3 | P4 | P1 | P5 | P2 | P3 | P4 | P1 | P3 |`.
- Conclusión esperada: FCFS tiene el menor promedio de espera.
- También se puede ingresar la misma lista por teclado (opción `1` del módulo).

## Casos previstos para fases posteriores

Estos datos quedan registrados aquí para cuando se implementen los módulos; todavía no
tienen resultados calculados.

- **Memoria:** cadena de 20 referencias con 3 marcos; comparar FIFO y LRU.
- **Disco:** cabezal inicial 70, cola `{130, 25, 160, 45, 90, 10, 185, 75}`, rango 0–199;
  comparar FCFS, SSTF y SCAN.
- **Concurrencia:** contador sin sincronización (debe dar menos de 10000) frente a
  contador con mutex, y productor-consumidor con buffer fijo.
