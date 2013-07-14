(ns mikera.streamz.server
  (:require [org.httpkit.server :as hts]))

(defn fresh-handler []
  (fn [request]
    {:status  200
     :headers {"Content-Type" "text/html"}
     :body    "Hello World!"}))

;; store the current server in a map of port -> close function
(def SERVER_MAP (agent {}))

(defn launch []
  (send-off SERVER_MAP 
    (fn [m]
      (let [port 8080
            _ (when (m port) ((m port))) ;; close the existing server, if any
            close-fn (hts/run-server (fresh-handler) {:port port})]
        (assoc m port close-fn)))))
