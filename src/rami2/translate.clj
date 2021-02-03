(ns rami2.translate
  (:require [clojure.data.json :as json]
            [clojure.string :as str]
            [clj-http.client :as client]
            [rami2.command :as command]))

(defn transform-response [response]
  (-> response
      :body
      json/read-str
      (nth 0)
      (get "translations")
      (nth 0)
      (get "text")))

(defn format-request [api-key target query]
  {:headers {"Ocp-Apim-Subscription-Key" api-key}
   :content-type :json
   :query-params {:to target
                  :api-version 3.0}
   :body (json/write-str [{:Text query}])})

(defn query-azure [query]
  (client/post
    "https://api.cognitive.microsofttranslator.com/translate"
    query))

(defmethod command/invoke-command "translate" [cmd message state]
  (let [api-key (:translate (:apikeys @state))
        target (first (:args cmd))
        query (str/join " " (rest (:args cmd)))
        translation (transform-response
                     (query-azure
                      (format-request api-key target query)))]
    {:type :content
     :value translation}))