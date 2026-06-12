# Comandos de uso de scripts en un entorno Linux

```
chmod +x scripts/setup.sh
```

```
chmod +x scripts/teardown.sh
```

```
chmod +x load-testing/stress.sh
```

```
./scripts/setup.sh
```

```
./load-testing/stress.sh URL
```

```
./scripts/teardown.sh
```

```
./scripts/teardown.sh --delete-cluster
```
En caso de que la ejecución se pause, debido al siguiente comando:

```

```

Se recomienda abrir en otras 3 terminales (bash, ej: gitbash) y ejecutar la exposición de los demás servicios a utilizar mediante un navegador web:

```
echo "Loan Service:"
minikube service \
  loan-service \
  -n library-system \
  --url
```
```
echo "Grafana:"
minikube service \
  monitoring-grafana \
  -n monitoring \
  --url
```

```
echo "Prometheus:"
minikube service \
  monitoring-kube-prometheus-prometheus \
  -n monitoring \
  --url
```

Y luego acceder a los endpoints que brinde cada uno. 

### Ejemplo:

```
http://127.0.0.1:57547
```
Adicionalmente, no olvidar las rutas para los servicios:

### Catálogo de Libros
```
http://127.0.0.1:57547/books
```

### Préstamos
```
http://127.0.0.1:57553/loans
```