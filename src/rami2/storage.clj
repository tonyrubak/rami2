(ns rami2.storage
    (:require [cognitect.aws.client.api :as aws]
        [clojure.data.json :as json]))

(defn get-aka
    [aka state]
    (let [dynamo (aws/client {:api :dynamodb})
         resp (aws/invoke dynamo
                          {:op :GetItem
                          :request {:TableName "rami2" 
                                   :Key       {"tag" {:S aka}}}})]
      (if-not (empty? resp)
        (-> resp :Item :value :S)
        nil)))

(defn set-aka
    [aka state]
    (let [dynamo (aws/client {:api :dynamodb})
        command (first aka)
        value (clojure.string/join " " (rest aka))]
        (aws/invoke
            dynamo
            {:op :PutItem
             :request
                {:ConditionExpression "attribute_not_exists(tag)"
                 :TableName "rami2" 
                 :Item {"tag"   {:S command}
                        "value" {:S value}}}})))

;;;
; (let [dynamo (aws/client {:api :dynamodb})]
;     (aws/invoke dynamo
;                 {:op :GetItem :request {:TableName "rami2"
;                                        :Key        {"tag" {:S "noexist"}}}}))

; (let [dynamo (aws/client {:api :dynamodb})]
;     (aws/invoke dynamo
;                 {:op :GetItem :request {:TableName "rami2"
;                                        :Key        {"tag" {:S "dave"}}}}))