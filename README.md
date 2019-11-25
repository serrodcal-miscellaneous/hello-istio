# hello-isito

Small example of usage for Istio and Kubernetes.

## Prerequisites

Install the following technologies acording to your SO:

* Docker
* Kubernetes
* kubectl
* Kustomize

## Demo

Download the docker images:

```sh
$ docker pull serrodcal/istio-publisher:0.1.0
$ docker pull serrodcal/istio-formatter:0.1.0
$ docker pull serrodcal/istio-hello:0.1.0
```

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
$ istioctl manifest apply --set profile=demo
```

Deploy the hello architecture on the cluster with proxy sidecar:

```sh
$ kustomize build kustomize/publisher/overlays/local | istioctl kube-inject -f - | kubectl apply -f -
$ kustomize build kustomize/formatter/overlays/local | istioctl kube-inject -f - | kubectl apply -f -
$ kustomize build kustomize/hello/overlays/local | istioctl kube-inject -f - | kubectl apply -f -
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

Finally, send requests:

```sh
$ curl 'http://localhost:30000/hello?helloTo=demo&greeting=hi'
```

Or, keep sending request:

```sh
$ i=0 ; while true ; do curl -o /dev/null -s http://localhost:30000/hello?helloTo=demo&greeting=hi ; if [ $? -ne 0 ] ; then echo $i ; break ; fi ; i=$(($i+1)) ; echo -en "$i        \r" ; sleep 1 ; done
```
