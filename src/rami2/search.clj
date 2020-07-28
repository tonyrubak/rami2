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

(defn format-query [query]
  (client/url-encode-illegal-characters
   (str/join "+" query)))

(defn format-request [query api-key]
  {:headers {"Ocp-Apim-Subscription-Key" api-key}
   :query-params {"q" (format-query query)
                  "safeSearch" "moderate"}})

(defn query-azure-image [request]
  (let [url "https://api.cognitive.microsoft.com/bing/v7.0/images/search"]
    (client/get url request)))

(defn get-images-from-response [response]
  (-> response
      :body
      json/read-str
      (get "value")))

(defn transform-response [response]
  (let [images (get-images-from-response response)]
    (-> images
        (nth (rand-int (count images)))
        (get "contentUrl"))))

(defmethod command/invoke-command "image" [cmd state]
  (let [api-key (:azure (:apikeys @state))
        query (:args cmd)
        img-url (transform-response
                      (query-azure-image
                       (format-request query api-key)))]
    {:type :embed
     :value {:image {:url img-url}}}))

(defmethod command/invoke-command "bing" [cmd state]
  {:type :embed
    :value (let [resp (get-search-response (:args cmd) state)]
            {:title (format "Bing results for %s" (str/join " " (:args cmd)))
              :type "link"
              :description (get resp "snippet")
              :url (get resp "url")
              :fields [{:name "URL" :value (get resp "url")}]
              :thumbnail {:url "https://1000logos.net/wp-content/uploads/2017/12/bing-emblem.jpg"}})})