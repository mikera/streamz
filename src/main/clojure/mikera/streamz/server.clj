(ns mikera.streamz.server
  (:use [mikera.cljutils.error])
  (:use [mikera.streamz.core])
  (:use [clojure.core.async])
  (:require [liberator.core :refer [resource defresource]])
  (:require [org.httpkit.server :as hts])
  (:require [compojure.core :as c]))

(defn text-resource [& content]
  (resource
    :available-media-types ["text/plain"]
    :handle-ok (fn [_] (apply str content))))

(defn make-stream-resource [server id]
  (text-resource "stream ID=" id))

(defn make-routes [server]
  (c/defroutes all-routes
    (c/context "/stream/:id" [id] 
      (make-stream-resource server id))))

(defn fresh-handler 
  "Creates a Ring handler backed by a new streamz server."
  ([]
    (make-routes (new-server))))

;; store the current server in a map of port -> close function
(defonce SERVER-MAP (agent {}))

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

(start)