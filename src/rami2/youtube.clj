(ns rami2.youtube
  (:require [clojure.data.json :as json]
            [clojure.string :as str]
            [clj-http.client :as client]
            [rami2.command :as command]))

(defn format-query [query]
  (client/url-encode-illegal-characters
   (str/join "+" query)))

(defn format-request [query api-key]
  {:headers {"key" api-key}
   :query-params {"q" query
                  "part" "snippet"
                  "type" "video"
                  "videoEmbeddable" "true"
                  "key" api-key}})

(defn query-youtube [request]
  (let [url "https://www.googleapis.com/youtube/v3/search"]
    (client/get url request)))

(defn videos [response]
  (-> response
      :body
      json/read-str
      (get "items")))

(defn transform-response [response]
  (let [vid (videos response)]
    (-> vid
        ; (nth (rand-int (count vid)))
        first
        (get "id")
        (get "videoId")
        ((fn [id] (str "https://www.youtube.com/watch?v=" id))))))

(defmethod command/invoke-command "yt" [cmd message state]
  (let [api-key (:youtube (:apikeys (:config @state)))
        query (format-query (:args cmd))
        vid-url (transform-response
                 (query-youtube
                  (format-request query api-key)))]
    {:type :content
     :value vid-url}))