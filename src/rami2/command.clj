(ns rami2.command
  (:require [rami2.search :as search]
            [rami2.markov :as markov]
            [rami2.storage :as storage]
            [rami2.weather :as wx]
            [clojure.string :as str]))

(defmulti invoke-command :command)

(defmethod invoke-command "aka" [command state]
  {:type :content
   :value (if (storage/set-aka (:args command) state)
            "AKA added successfully."
            "Failed to add AKA. Maybe it already exists?")})

(defmethod invoke-command "delaka" [command state]
  {:type :content
   :value (if (contains? (:admin @state) (:author command))
            (if (storage/delete-aka (:args command) state)
              "AKA deleted successfully."
              "Failed to remove AKA. Maybe it doesn't exist?")
            "Not authorized.")})

(defmethod invoke-command "list" [command state]
  {:type :content
   :value (str/join ", "
                    (map #(-> % :tag :S) (:Items (storage/list-aka state))))})

(defmethod invoke-command "bing" [command state]
  {:type :embed
  :value (let [args (:args command)
               resp (search/get-search-response args state)
               q (str/join " " args)]
           {:title (format "Bing results for %s" q)
           :type "link"
           :description (get resp "snippet")
           :url (get resp "url")
           :fields [{:name "URL" :value (get resp "url")}]
           :thumbnail {:url "https://1000logos.net/wp-content/uploads/2017/12/bing-emblem.jpg"}})})

(defmethod invoke-command "w" [command state]
  (let [q (str/join " " (:args command))
        resp (wx/get-weather q state)]
    {:type :embed :value resp}))

(defmethod invoke-command "markov" [command state]
    {:type :content :value (markov/markov (-> command
                                              :args
                                              first)
                                          state)})

(defmethod invoke-command :default [command state]
  {:type :content
  :value (let [aka (:command command)]
           (if-let [response (storage/get-aka aka state)]
             response
             nil))})