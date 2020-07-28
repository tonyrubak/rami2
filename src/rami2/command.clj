(ns rami2.command
  (:require [rami2.storage :as storage]
            [clojure.string :as str]))

(defmulti invoke-command :command)

(defmethod invoke-command "echo" [command state]
  {:type :content
  :value (str/join " " (:args command))})