# Informe Técnico – Monitoreo de Microservicios Java en Kubernetes

**Curso:** Kubernetes con Prometheus & Grafana
**Alumno:** [TU NOMBRE]
**Proyecto:** Biblioteca Digital Monitoreada con Kubernetes
**Fecha:** Junio 2026

---

# Parte A — Conceptos

## 1. ¿Qué es Micrometer y por qué se usa en lugar de la librería directa de Prometheus?

Micrometer es una fachada de observabilidad para aplicaciones Java. Proporciona una API común para registrar métricas independientemente del sistema de monitoreo utilizado.

Se utiliza en lugar de la librería nativa de Prometheus porque desacopla la aplicación de una tecnología específica. Gracias a Micrometer, las métricas pueden enviarse a Prometheus, Datadog, New Relic u otros sistemas sin modificar el código de negocio.

En este proyecto se utilizó Micrometer junto con Spring Boot Actuator para exponer métricas en el endpoint:

```text
/metrics
```

que posteriormente fueron recolectadas por Prometheus mediante ServiceMonitors.

---

## 2. ¿Cuál es la diferencia entre Counter, Gauge y Timer?

### Counter

Un Counter representa un valor que únicamente aumenta.

Ejemplo utilizado:

```java
catalog_requests_total
```

Esta métrica registra la cantidad total de consultas realizadas al catálogo.

---

### Gauge

Un Gauge representa un valor que puede aumentar o disminuir.

Ejemplos utilizados:

```java
books_available
catalog_response_delay_ms
```

books_available indica cuántos libros permanecen disponibles.

catalog_response_delay_ms indica el retraso artificial configurado para las pruebas de rendimiento.

---

### Timer

Un Timer mide la duración de una operación.

Ejemplo utilizado:

```java
catalog_lookup_duration_seconds
```

Esta métrica registra el tiempo necesario para localizar un libro dentro del catálogo.

---

## 3. ¿Qué es un ServiceMonitor y cómo sabe el Prometheus Operator qué monitorear?

Un ServiceMonitor es un recurso personalizado (CRD) proporcionado por Prometheus Operator.

Define:

* Qué servicio monitorear.
* Qué endpoint consultar.
* Cada cuánto tiempo realizar scraping.

Prometheus Operator descubre automáticamente los ServiceMonitor que coinciden con los selectores configurados en la instalación de kube-prometheus-stack.

En este proyecto se configuró:

```yaml
serviceMonitorNamespaceSelector: {}
serviceMonitorSelector: {}
```

permitiendo descubrir ServiceMonitors en cualquier namespace.

---

## 4. ¿Cuál es la diferencia entre liveness probe y readiness probe?

### Liveness Probe

Verifica si el contenedor sigue funcionando correctamente.

Si falla repetidamente:

```text
Kubernetes reinicia el contenedor.
```

---

### Readiness Probe

Verifica si el contenedor está listo para recibir tráfico.

Si falla:

```text
El pod continúa ejecutándose,
pero deja de recibir solicitudes.
```

---

## 5. ¿Por qué es necesario apuntar Docker al daemon de Minikube antes de construir las imágenes?

Minikube ejecuta su propio entorno Docker interno.

Si las imágenes se construyen en Docker Desktop y no dentro del daemon de Minikube, Kubernetes no puede encontrarlas localmente.

Por ello se ejecutó:

```bash
minikube docker-env --shell powershell | Invoke-Expression
```

permitiendo que los builds se almacenaran directamente dentro del entorno Docker utilizado por Minikube.

---

## 6. ¿Qué ocurre si no configuras el selector de ServiceMonitors en el values.yaml del chart?

Prometheus Operator únicamente monitorea ServiceMonitors que coincidan con sus selectores.

Si estos selectores no incluyen el namespace del proyecto:

```text
Prometheus ignora los ServiceMonitors.
```

Como consecuencia:

* Los targets no aparecen.
* Las métricas no se recolectan.
* Los dashboards permanecen vacíos.
* Las alertas nunca se activan.

---

# Parte B — PromQL

## 1. Tasa de requests por minuto del Loan Service

```promql
rate(http_server_requests_seconds_count[5m]) * 60
```

---

## 2. Latencia p95 del endpoint más crítico

```promql
histogram_quantile(
  0.95,
  rate(http_server_requests_seconds_bucket[5m])
)
```

---

## 3. Estado UP/DOWN de ambos servicios

```promql
up
```

o individualmente:

```promql
up{job="book-catalog-service"}
```

```promql
up{job="loan-service"}
```

---

## 4. Query de negocio

Cantidad de libros disponibles:

```promql
books_available
```

Permite responder:

> ¿Cuántos libros permanecen disponibles para préstamo?

---

# Parte C — Evidencias de Casuísticas

## Casuística 1 — Servicio Caído

### Contexto

Se simuló un error de configuración en el Book Catalog Service provocando que el pod no pudiera iniciar correctamente.

---

### Síntoma

Los usuarios no podían consultar libros.

---

### Activación

```bash
kubectl edit deployment book-catalog-service -n library-system
```

Modificar una variable crítica o puerto incorrecto.

---

### Diagnóstico

Grafana mostró:

* Servicio DOWN.
* Disminución abrupta de requests.
* Alerta activada.

---

### Resolución

```bash
kubectl rollout restart deployment book-catalog-service -n library-system
```

---

### Lección Técnica

Los probes permiten detectar fallos automáticamente y recuperar servicios sin intervención manual.

---

### Evidencia

[CAPTURA]

---

## Casuística 2 — Servicio Lento

### Contexto

Se introdujo latencia artificial en el catálogo.

---

### Síntoma

Los préstamos empezaron a responder lentamente y se registraron timeouts.

---

### Activación

```yaml
CATALOG_DELAY_MS=5000
```

---

### Diagnóstico

Grafana mostró:

```promql
catalog_response_delay_ms
```

incrementándose hasta 5000 ms.

También aumentó la latencia observada en Loan Service.

---

### Resolución

```yaml
CATALOG_DELAY_MS=0
```

y redeploy.

---

### Lección Técnica

La latencia se propaga entre servicios y afecta toda la cadena de dependencias.

---

### Evidencia

[CAPTURA]

---

## Casuística 3 — Sin Libros Disponibles

### Contexto

Se simularon préstamos hasta agotar el inventario.

---

### Síntoma

Los usuarios ya no podían registrar nuevos préstamos.

---

### Activación

Realizar préstamos hasta que:

```promql
books_available
```

alcance:

```text
0
```

---

### Diagnóstico

Grafana mostró disponibilidad nula.

---

### Resolución

Agregar nuevos libros o devolver libros prestados.

---

### Lección Técnica

Las métricas de negocio permiten detectar problemas antes de que generen pérdidas operativas.

---

### Evidencia

[CAPTURA]

---

# Parte D — Evidencias del Sistema

## 1. Targets en Prometheus

Prometheus detectó correctamente:

* Book Catalog Service
* Loan Service

Estado:

```text
UP
```

### Evidencia

[CAPTURA]

---

## 2. Dashboard Grafana

Dashboard compuesto por:

* Estado del catálogo
* Estado del servicio de préstamos
* Requests por segundo
* Memoria JVM
* Libros disponibles
* Delay artificial

### Evidencia

[CAPTURA]

---

## 3. Pods Running

Comando:

```bash
kubectl get pods -A
```

Todos los pods se encontraron en estado:

```text
Running
```

### Evidencia

[CAPTURA]

---

## 4. Alerta FIRING

Durante la ejecución de las casuísticas se verificó la activación de alertas configuradas mediante PrometheusRule.

### Evidencia

[CAPTURA]

---

# Conclusiones

Durante el desarrollo de este proyecto se implementó una arquitectura de microservicios basada en Spring Boot, desplegada sobre Kubernetes y observada mediante Prometheus y Grafana.

Se comprobó la importancia de:

* Las métricas de aplicación.
* Los probes de Kubernetes.
* El monitoreo continuo.
* La detección temprana de incidentes.
* Las alertas automatizadas.

Además, se verificó cómo los problemas de disponibilidad y rendimiento pueden propagarse entre servicios y afectar directamente el comportamiento del sistema completo.

El uso conjunto de Kubernetes, Prometheus y Grafana permitió obtener visibilidad completa sobre la salud de la plataforma y facilitar el diagnóstico de incidentes.
