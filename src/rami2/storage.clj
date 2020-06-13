(ns rami2.storage)

(defprotocol Storage
    (open-storage   [file] "Returns a Storage service from the given file")
    (write-storage  [file] "Writes the in-memory Storage service to the file")
    (get-aka        [aka] "Returns the text associated with an AKA")
    (set-aka        [aka] "Creates or updates an AKA")
    (delete-aka     [aka] "Removes an AKA"))

(open-storage [file]
    (with-open [r (java.io.PushbackReader. (clojure.java.io/reader file))]
        (clojure.edn/read r))))
(write-storage [file storage]
    (spit file (with-out-str (pr storage))))