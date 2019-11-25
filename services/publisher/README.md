# Publisher

### Pulling

```sh
$ docker pull serrodcal/istio-publisher:0.1.0
```

### Building

```sh
$ docker build -t serrodcal/istio-publisher:0.1.0 .
```

### Running

```sh
$ docker run -d -p 8080:8080 --name publisher serrodcal/istio-publisher:0.1.0
```