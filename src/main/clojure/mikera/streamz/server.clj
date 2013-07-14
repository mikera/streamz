(ns mikera.streamz.server
  (:require [org.httpkit.server :as hts]))

(defn fresh-handler []
  (fn [request]
    {:status  200
     :headers {"Content-Type" "text/html"}
     :body    "Hello World!"}))

(defn launch []
  (let [close-fn (hts/run-server (fresh-handler) {:port 8080})]
    close-fn))
