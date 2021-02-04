(ns rami2.reacts
  (:require [discljord.messaging :as m]
            [clojure.string :as str]))

(defmulti message-react (fn [reaction _ _] (:kind reaction)))

(defmethod message-react :emoji [reaction message state]
  (if (some? (-> (:content message) .toLowerCase (#(re-find (re-pattern (:trigger reaction)) %))))
    (m/create-reaction!
     (:messaging @state)
     (:channel-id message)
     (:id message)
     (:emoji-id reaction))))

(defmethod message-react :repost [reaction message state]
  (if (some? (-> (:content message) .toLowerCase (#(re-find (re-pattern (:trigger reaction)) %))))
    (let [mesg (format "%s: %s" (:username (:author message)) (:content message))]
      (m/create-message!
       (:messaging @state)
       (:target-channel reaction)
       :content mesg))))