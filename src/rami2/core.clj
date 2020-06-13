(ns rami2.core
  (:gen-class)
  (:require [discljord.connections :as c]
            [discljord.messaging :as m]
            [discljord.events :as e]
            [clojure.core.async :as a]
            [rami2.storage :as storage]))

(def state (atom nil))

(defmulti handle-event
  (fn [event-type event-data]
    event-type))

(defmethod handle-event :default
  [event-type event-data]))

(defmethod handle-event :message-create
  [event-type {{bot :bot} :author :keys [channel-id content]}]
  (if (= content "!disconnect")
    (a/put! (:connection @state) [:disconnect])
    (when-not bot
      (if (.contains (.toLowerCase content) "eddie")
        (m/create-message! (:messaging @state) channel-id :embed { :image {:url "https://cdn.discordapp.com/attachments/173094635391025152/691489861739216906/691114417013915740.png"}}))
      (if (.contains (.toLowerCase content) "bullshit")
        (m/create-message! (:messaging @state) channel-id :embed { :image {:url "https://cdn.discordapp.com/attachments/610695135738593282/710590989437501450/blazing.gif"}}))
      (if (.startsWith content ".")
        (let [sp (.split (.substring content 1) " ")
              command (first sp)
              args (rest sp)
              storage @(:storage @state)]
          (case command "aka" (reset! (:storage @state) (storage/set-aka storage args))
                        "print" (m/create-message! (:messaging @state) channel-id :content (format "Filename: %s\nContents: %s" (:filename storage) (:data storage)))
                        "exist" (m/create-message! (:messaging @state) channel-id :content (format "AKA %s: %s" (first args) (str (storage/is-aka storage (first args)))))
                        (if (storage/is-aka storage command)
                          (m/create-message! (:messaging @state) channel-id :content (storage/get-aka storage command)))))))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [config (with-open [r (java.io.PushbackReader. (clojure.java.io/reader "config.edn"))]
                  (clojure.edn/read r))
        event-ch (a/chan 100)
        connection-ch (c/connect-bot! (:token config) event-ch)
        messaging-ch (m/start-connection! (:token config))
        init-state {:connection connection-ch
                    :event event-ch
                    :messaging messaging-ch
                    :storage (atom (storage/open-storage (:storage config)))}]
      (reset! state init-state)
      (e/message-pump! event-ch handle-event)
      (m/stop-connection! messaging-ch)
      (c/disconnect-bot! connection-ch)))