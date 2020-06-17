(ns rami2.logging)

(defn create-rotating-logger
    [path]
    (let [logfile (new java.io.FileOutputStream path true)]
        { :logger-kind :rotating
        :path path
        :writer (atom logfile)}))

(defn log-write
    [logger text]
    (if-not (.sync (.getFD @(:writer logger)))
        (reset! (:writer logger) (new java.io.FileOutputStream (:path logger) true))
        (let 
            [attributes (java.nio.file.Files/readAttributes (-> (:path logger) clojure.java.io/file .toPath) java.nio.file.attribute.BasicFileAttributes (into-array java.nio.file.LinkOption []))
            creation-time (.creationTime attributes)]
            (when
                (> (.compareTo (java.time.Duration/between (.toInstant creation-time) (java.time.Instant/now)) (java.time.Duration/ofDays 1)) 1)
                ; Rotate the log here pls
            )))
    (.write @(:writer logger) (.getBytes text (java.nio.charset.Charset/forName "UTF-8"))))