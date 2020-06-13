(ns rami2.storage)

(defn open-storage
    [file]
    (with-open [r (java.io.PushbackReader. (clojure.java.io/reader file))]
        {:data (clojure.edn/read r) :filename file}))
(defn write-storage
    [storage]
    (spit (:filename storage) (with-out-str (pr (:data storage)))))
(defn set-aka
    [storage aka]
    (let [storage {:filename (:filename storage) :data (assoc (:data storage) (keyword (first aka)) (first (rest aka)))}]
        (write-storage storage)
        storage))
(defn is-aka
    [storage aka]
    (contains? (:data storage) (keyword aka)))
(defn get-aka
    [storage aka]
    (get (:data storage) (keyword aka)))