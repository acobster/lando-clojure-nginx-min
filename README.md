# Lando/Clojure/Nginx issue minimal repro

Running `lando start` in this repo should result in a warning from Lando that "the service 'nginx' is not running!"

`lando logs -s nginx` reveals that the `proxy_pass` directive in `lando/nginx.conf` is to blame:

```
nginx_1      | INFO  ==> ** Starting NGINX setup **
nginx_1      | INFO  ==> Validating settings in NGINX_* env vars...
nginx_1      | INFO  ==> Initializing NGINX...
nginx_1      | DEBUG ==> Custom configuration detected. Using it...
nginx_1      | 
nginx_1      | INFO  ==> ** NGINX setup finished! **
nginx_1      | INFO  ==> ** Starting NGINX **
nginx_1      | 2020/05/29 21:29:28 [emerg] 176#0: host not found in upstream "appserver.clojureapp.internal" in /opt/bitnami/nginx/conf/nginx.conf:17
nginx_1      | nginx: [emerg] host not found in upstream "appserver.clojureapp.internal" in /opt/bitnami/nginx/conf/nginx.conf:17
```

This seems to be an issue with the way the different service hosts detect (or rather fail to detect) each other.

For example, if I remove dbhost and appserver's dependency on it from the Landofile, the reverse proxy works:

```
$ git checkout remove-dbhost
$ lando rebuild -y
$ curl http://clojure-app.lndo.site
Hello, Lando!
```

 Alternatively, if I remove the proxy_pass directive so that Nginx starts up correctly (`git checkout remove-proxy`), Nginx even sees the internal host as expected:

```
$ git checkout remove-proxy
$ lando rebuild -y
$ curl http://clojure-app.lndo.site
Nginx is working!
$ lando ssh -s nginx -c 'curl appserver.clojureapp.internal:8080'
Hello, Lando!
```

For these reasons, I don't think this is an issue with my application/config but rather with the underlying Docker network as created by Lando.

