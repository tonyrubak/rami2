(ns rami2.markov
  (:require [cognitect.aws.client.api :as aws]
            [clojure.data.json :as json]
            [rami2.command :as command]))

(defn markov
  [prefix state]
  (if (= prefix "communism")
    "communism begins"
    (let [resp (aws/invoke (aws/client {:api :lambda})
                           {:op :Invoke
                            :request {:FunctionName "markov"
                                      :Payload (format "{ \"prefix\": \"%s\" }"
                                                       prefix)}})]
      (-> resp
          :Payload
          slurp
          json/read-str
          (get "body")
          (.replaceAll "\"" "")))))

(defmethod command/invoke-command "markov" [cmd state]
  {:type :content
    :value (markov (-> cmd
                       :args
                       first)
                   state)})