(ns rami2.command
  (:require [rami2.storage :as storage]
            [discljord.messaging :as m]
            [clojure.string :as str]))

(defmulti invoke-command (fn [command _ _] (:command command)))

(defmethod invoke-command "echo" [command message state]
  {:type :content
   :value (str/join " " (:args command))})

(defmethod invoke-command "snowflake" [command message state]
  {:type :content
   :value (:guild-id message)})

(defmethod invoke-command "listemoji" [command message state]
  (println @(m/list-guild-emojis!
              (:messaging @state)
              "222454667773345792")))