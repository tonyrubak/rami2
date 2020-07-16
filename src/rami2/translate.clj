(ns rami2.translate
  (:require [clojure.data.json :as json]
            [clojure.string :as str]
            [clj-http.client :as client]
            [rami2.command :as command]))

(defn get-translate-response [target query state]
  (let [url "https://api.cognitive.microsofttranslator.com/translate?api-version=3.0"
        api-key (:translate (:apikeys @state))]
    (-> (client/post url {:headers {"Ocp-Apim-Subscription-Key" api-key}
                      :content-type :json
                      :query-params {:to target}
                      :body (format "[{\"Text\": \"%s\"}]" query)})
        :body
        json/read-str
        (nth 0)
        (get "translations")
        (nth 0)
        (get "text"))))

(defmethod command/invoke-command "translate" [cmd state]
  {:type :content
    :value (get-translate-response
            (first (:args cmd))
            (str/join " " (rest (:args cmd)))
            state)})