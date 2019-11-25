# Bookstore

Small example of usage for Istio and Kubernetes.

## Prerequisites

Install the following technologies acording to your SO:

* Docker
* Kubernetes
* kubectl
* Kustomize

## Demo

Download Istio:

```sh
$ curl -L https://istio.io/downloadIstio | sh -
```

Set `istioctl` in your path:

```sh
$ cd istio-1.4.0
$ export PATH=$PWD/bin:$PATH
```

Install Istio into the cluster:

```sh
$ istioctl manifest generate --set profile=demo --set values.tracing.enabled=true --set values.tracing.provider=zipkin | kubectl apply -f -
```

Enable autoinjection:

```sh
$ kubectl label namespace default istio-injection=enabled
```

Deploy Book Info:

```sh
$ kubectl apply -f bookinfo/bookinfo.yaml
```

Expose Zipkin'z dashboard:

```sh
$ istioctl dashboard zipkin
```

Expose Graphana's Dashboard:

```sh
$ kubectl -n istio-system port-forward $(kubectl -n istio-system get pod -l app=grafana -o jsonpath='{.items[0].metadata.name}') 3000:3000 &
```

Open [http://localhost:3000/dashboard/db/istio-mesh-dashboard](http://localhost:3000/dashboard/db/istio-mesh-dashboard) in your Browser.

Expose Kiali's Dashboard (`admin;admin`):

```sh
$ istioctl dashboard kiali
```

Open in a browser [http://localhost:30000/productpage](http://localhost:30000/productpage).

Finally, send requests:

```sh
$ i=0 ; while true ; do curl -o /dev/null -s http://localhost:30000/productpage ; if [ $? -ne 0 ] ; then echo $i ; break ; fi ; i=$(($i+1)) ; echo -en "$i        \r" ; sleep 1 ; done
```

## Clean up

Delete label:

```sh
$ kubectl label namespace default istio-injection-
```

Delete Book Info:

```sh
$ kubectl delete -f bookinfo/bookinfo.yaml
```

Delete all Istio components:

```sh
$ istioctl manifest generate --set profile=demo | kubectl delete -f -
```
