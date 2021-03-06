(ns rami2.aka
  (:require [rami2.command :as command]
            [rami2.storage :as storage]
            [clojure.string :as str]))

(defmethod command/invoke-command "aka" [command message state]
  {:type :content
    :value (if (storage/set-aka (:args command) state)
            (format "AKA `%s` added successfully." (first (:args command)))
            "Failed to add AKA. Maybe it already exists?")})

(defmethod command/invoke-command "delaka" [command message state]
  (let [author (:id (:author message))]
    {:type :content
      :value (if (contains? (:admin (:config @state)) author)
              (if (storage/delete-aka (:args command) state)
                "AKA deleted successfully."
                "Failed to remove AKA. Maybe it doesn't exist?")
              "Not authorized.")}))

(defmethod command/invoke-command "list" [command message state]
  (let [keys (map #(-> % :tag :S)
                  (:Items (storage/list-aka state)))]
    {:type :content
     :value (str/join ", "
                      (sort (if-let [search (first (:args command))]
                              (filter #(.contains % search) keys)
                              keys)))}))

(defmethod command/invoke-command :default [command message state]
  {:type :content
   :value (if-let [response (storage/get-aka (:command command) state)]
            response
            nil)})