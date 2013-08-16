(ns mikera.streamz.server
  (:use [mikera.cljutils.error])
  (:require [org.httpkit.server :as hts]))

(defn fresh-handler []
  (fn [request]
    {:status  200
     :headers {"Content-Type" "text/html"}
     :body    "Hello World!"}))

;; store the current server in a map of port -> close function
(def SERVER-MAP (agent {}))

(defn start 
  "Launches a streamz server on the given port with default configuration"
  ([] (start 8080))
  ([port]
    (send-off SERVER-MAP 
              (fn [m]
      (let [port (int port)
            _ (when (m port) ((m port))) ;; close the existing server, if any
            close-fn (hts/run-server (fresh-handler) {:port port})]
        (assoc m port close-fn))))))


(defn stop-all
  "Stops the streamz server on all ports"
  ([] (send-off SERVER-MAP
                (fn [m]
                  (doseq [[port cf] m] (cf))
                  {}))))

(defn stop
  "Stops the streamz server on all ports or a specific port"
  ([] (stop-all))
  ([port] (send-off SERVER-MAP
                (fn [m]
                  (if-let [cf (m port)]
                    (do (cf) (dissoc m port))
                    (error "Streamz port " port " not running"))))))
