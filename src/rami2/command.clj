(ns rami2.command
  (:require [rami2.storage :as storage]
            [discljord.messaging :as m]
            [clojure.string :as str]))

(defmulti invoke-command :command)

(defmethod invoke-command "echo" [command state]
  {:type :content
  :value (str/join " " (:args command))})

(defmethod invoke-command "delete" [command state]
  (let [message (:message command)
        author (:username (:author message))]
    (if (contains? (:admin @state) author)
      (if-let [target (:message-reference message)]
        (m/delete-message!
         (:messaging @state)
         (:channel-id target)
         (:message-id target))))
    nil))