(ns rami2.markov
  (:require [cognitect.aws.client.api :as aws]
            [clojure.data.json :as json]))

(defn markov
  [prefix state]
  (if (= prefix "communism")
    "communism begins"
    (let [lambda (aws/client {:api :lambda})]
      (.replaceAll
        (get
          (json/read-str
            (slurp
              (:Payload
                (aws/invoke
                  lambda
                  {:op :Invoke
                  :request {:FunctionName "markov"
                           :LogType "None"
                           :Payload (format "{ \"prefix\": \"%s\" }"
                                            prefix)}}))))
          "body") "\"" ""))))