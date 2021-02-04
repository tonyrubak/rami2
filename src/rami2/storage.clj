(ns rami2.storage
  (:require
   [cognitect.aws.client.api :as aws]
   [clojure.data.json :as json]))

(defn get-aka
  [aka state]
    (let [dynamo (aws/client {:api :dynamodb})
          resp (aws/invoke dynamo
                          {:op :GetItem
                           :request {:TableName (:table (:storage (:config @state)))
                                     :Key {"tag" {:S aka}}}})]
      (if-not (empty? resp)
        (-> resp :Item :value :S)
        nil)))

(defn set-aka
  [aka state]
  (let [dynamo (aws/client {:api :dynamodb})
        command (first aka)
        value (clojure.string/join " " (rest aka))
        resp (aws/invoke
              dynamo
              {:op :PutItem
               :request
                {:ConditionExpression "attribute_not_exists(tag)"
                 :TableName (:table (:storage (:config @state)))
                 :Item {"tag"   {:S command}
                        "value" {:S value}}}})]
    (not= (:cognitect.anomalies/category resp) :cognitect.anomalies/incorrect)))

(defn list-aka
  [state]
  (aws/invoke (aws/client {:api :dynamodb})
              {:op :Scan
              :request
               {:TableName (:table (:storage (:config @state)))
               :ProjectionExpression "tag"}}))

(defn delete-aka
  [aka state]
    (let [dynamo (aws/client {:api :dynamodb})
          command (first aka)
          resp (aws/invoke dynamo
                          {:op :DeleteItem
                            :request {:TableName (:table (:storage (:config @state)))
                                      :Key {"tag" {:S command}}}})]
      (not= (:cognitect.anomalies/category resp)
            :cognitect.anomalies/incorrect)))
;;;
; (let [dynamo (aws/client {:api :dynamodb})]
;     (aws/invoke dynamo
;                 {:op :GetItem :request {:TableName "rami2"
;                                        :Key        {"tag" {:S "noexist"}}}}))

; (let [dynamo (aws/client {:api :dynamodb})]
;     (aws/invoke dynamo
;                 {:op :GetItem :request {:TableName "rami2"
;                                        :Key        {"tag" {:S "dave"}}}}))

; (def badresp (let [dynamo (aws/client {:api :dynamodb})]
;     (aws/invoke dynamo
;                 {:op :PutItem :request {:TableName "rami2"
;                                        :ConditionExpression "attribute_not_exists(tag)"
;                                        :Item       {"tag" {:S "dave"}
;                                                    "value" {:S "value"}}}})))

; (let [dynamo (aws/client {:api :dynamodb})]
;     (aws/invoke dynamo
;                 {:op :DeleteItem :request {:TableName "rami2"
;                                            :Key {"tag" {:S "test"}}}}))

; (aws/invoke (aws/client {:api :dynamodb})
;   {:op :Scan
;   :request
;    {:TableName "rami2"
;    :ProjectionExpression "tag"}})