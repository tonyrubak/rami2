(ns rami2.image
  (:require [clojure.data.json :as json]
            [clojure.string :as str]
            [clj-http.client :as client]
            [rami2.command :as command]))

(defn format-query [query]
  (client/url-encode-illegal-characters
   (str/join "+"
             (str/split query #" "))))

(defn format-request [query api-key]
  {:headers {"Ocp-Apim-Subscription-Key" api-key}
   :query-params {"q" query
                  "safeSearch" "strict"}})

(defn query-azure [request]
  (let [url "https://api.cognitive.microsoft.com/bing/v7.0/images/search"]
    (client/get url request)))

(defn images [response]
  (-> response
      :body
      json/read-str
      (get "value")))

(defn transform-response [response]
  (let [img (images response)]
    (-> img
        (nth (rand-int (count img)))
        (get "contentUrl"))))

(defmethod command/invoke-command "image" [cmd state]
  (let [api-key (:azure (:apikeys @state))
        query (format-query (:args cmd))
        img-url (transform-response
                 (query-azure
                  (format-request query api-key)))]
    {:type :embed
     :value {:image {:url img-url}}}))