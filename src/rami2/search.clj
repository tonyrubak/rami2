(ns rami2.search
  (:require [clojure.data.json :as json]
            [clojure.string :as str]
            [clj-http.client :as client]
            [rami2.command :as command]))

(defn get-search-response [query state]
  (let [url "https://api.cognitive.microsoft.com/bing/v7.0/search"
        formatted-query (str/join "+" query)
        api-key (:azure (:apikeys @state))
        headers {"Ocp-Apim-Subscription-Key" api-key}
        params {"q" formatted-query
                "textDecorations" false
                "textFormat" "raw"}]
    (-> (client/get url {:headers headers :query-params params})
        :body
        json/read-str
        (#(get % "webPages"))
        (#(get % "value"))
        first
        (#(select-keys % ["url" "snippet"])))))

(defmethod command/invoke-command "bing" [cmd state]
  {:type :embed
    :value (let [resp (get-search-response (:args cmd) state)]
            {:title (format "Bing results for %s" (str/join " " (:args cmd)))
              :type "link"
              :description (get resp "snippet")
              :url (get resp "url")
              :fields [{:name "URL" :value (get resp "url")}]
              :thumbnail {:url "https://1000logos.net/wp-content/uploads/2017/12/bing-emblem.jpg"}})})