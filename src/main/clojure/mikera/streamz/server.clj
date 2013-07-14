(ns mikera.streamz.server
  (:require [org.httpkit.server :as hts]))

(defn fresh-handler []
  (fn [request]
    "OK"))

(defn launch []
  (let [close-fn (hts/run-server (fresh-handler) {:port 8080})]
    close-fn))
