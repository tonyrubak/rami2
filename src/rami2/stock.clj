(ns rami2.stock
  (:require [clojure.data.json :as json]
            [clojure.string :as str]
            [clj-http.client :as client]
            [rami2.command :as command]))

(defn get-api-response [query state]
  (let [url "https://www.alphavantage.co/query"
        api-key (:alpha (:apikeys (:config @state)))
        params {"symbol" query
                "function" "GLOBAL_QUOTE"
                "apikey" api-key}]
    (-> (client/get url {:query-params params})
        :body
        json/read-str
        (#(get % "Global Quote"))
        (#(get % "05. price")))))

(defmethod command/invoke-command "quote" [cmd message state]
  {:type :content
  :value (get-api-response (:args cmd) state)})