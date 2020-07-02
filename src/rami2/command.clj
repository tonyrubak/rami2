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
                    (map #(-> % :tag :S)
                         (:Items (storage/list-aka state))))})

(defmethod invoke-command :default [command state]
  {:type :content
   :value (if-let [response (storage/get-aka (:command command) state)]
            response
            nil)})