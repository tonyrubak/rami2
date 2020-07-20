(ns rami2.markov
  (:require [cognitect.aws.client.api :as aws]
            [clojure.data.json :as json]
            [rami2.command :as command]))

(defn format-request [prefix]
  {:op :Invoke
   :request {:FunctionName "markov"
             :Payload (json/write-str {:prefix prefix})}})

(defn transform-response [resp]
  (-> resp
      :Payload
      slurp
      json/read-str
      (get "body")
      org.apache.commons.lang3.StringEscapeUtils/unescapeJava
      (.replaceAll "\"" "")))

(defn query-lambda [prefix]
  (aws/invoke
    (aws/client {:api :lambda})
    (format-request prefix)))

(defmethod command/invoke-command "markov" [cmd state]
  (let [prefix (first (:args cmd))
        response (if (= prefix "communism")
                   "communism begins"
                   (transform-response
                    (query-lambda prefix)))]
    {:type :content
     :value response}))