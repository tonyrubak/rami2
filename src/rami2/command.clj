(ns rami2.command
  (:require [rami2.storage :as storage]
            [discljord.messaging :as m]
            [clojure.string :as str]))

(defmulti invoke-command :command)

(defmethod invoke-command "echo" [command state]
  {:type :content
   :value (str/join " " (:args command))})

; (defmethod invoke-command "delete" [command state]
;   (let [message (:message command)]
;     (if-let [target (:message-reference message)]
;       (let [target-message @(m/get-channel-message!
;                              (:messaging @state)
;                              (:channel-id target)
;                              (:message-id target))]
;         (if (= "Ramiel" (:username (:author target-message)))
;           (m/delete-message!
;            (:messaging @state)
;            (:channel-id target)
;            (:message-id target)))))
;   nil))

(defmethod invoke-command "snowflake" [command state]
  {:type :content
   :value (:guild-id (:message command))})

(defmethod invoke-command "listemoji" [command state]
  (println @(m/list-guild-emojis!
              (:messaging @state)
              "222454667773345792")))