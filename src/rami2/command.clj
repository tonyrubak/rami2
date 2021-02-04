(ns rami2.command
  (:require [rami2.storage :as storage]
            [discljord.messaging :as m]
            [clojure.string :as str]))

(defmulti invoke-command (fn [command _ _] (:command command)))

(defmethod invoke-command "echo" [command message state]
  {:type :content
   :value (str/join " " (:args command))})

(defmethod invoke-command "guild-snowflake" [command message state]
  {:type :content
   :value (:guild-id message)})

(defmethod invoke-command "user-snowflake" [command message state]
  {:type :content
   :value (if-let [target (:message-reference message)]
            (:id (:author @(m/get-channel-message!
                            (:messaging @state)
                            (:channel-id message)
                            (:message-id target))))
            (:id (:author message)))})

(defmethod invoke-command "listemoji" [command message state]
  (println @(m/list-guild-emojis!
              (:messaging @state)
              (first (:args command)))))