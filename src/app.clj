(ns app
  (:require
   [org.httpkit.server :as http]))

(defn -main [& _args]
  (println "Running at http://localhost:8080")
  (http/run-server (fn [req]
                     {:status 200 :body "Hello, Lando!"})
                   {:port 8080}))