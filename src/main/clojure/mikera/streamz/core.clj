(ns mikera.streamz.core)

(defn new-server 
  "Create a new server, with the specified option map"
  ([] (new-server {}))
  ([opts]
    (merge {}
           opts
           {:data (atom {})})))
