(ns rami2.core
  (:gen-class)
  (:require [discljord.connections :as c]
            [discljord.messaging :as m]
            [discljord.events :as e]
            [clojure.core.async :as a]
            [clojure.data.json :as json]
            [clojure.string :as str]
            [rami2.logging :as logging]
            [rami2.command :as command]
            [rami2.markov :as markov]
            [rami2.search :as search]
            [rami2.storage :as storage]
            [rami2.weather :as wx]))

(def state (atom nil))

(defmulti handle-event
  (fn [event-type event-data]
    event-type))

(defmethod handle-event :default
  [event-type event-data])

(defmethod handle-event :message-create
  [event-type {{bot :bot} :author
              {author :username} :author
              :keys [channel-id content]}]
  (if (and (= content "!disconnect") (contains? (:admin @state) author))
    (a/put! (:connection @state) [:disconnect])
    (when-not bot
      ;;; REACTS NEED TO GO INTO THEIR OWN MODULE
      (if (.contains (.toLowerCase content) "eddie")
        (m/create-message!
         (:messaging @state) channel-id
         :embed {:image {:url "https://cdn.discordapp.com/attachments/173094635391025152/691489861739216906/691114417013915740.png"}}))
      (if (.contains (.toLowerCase content) "bullshit")
        (m/create-message!
         (:messaging @state) channel-id
         :embed {:image {:url "https://cdn.discordapp.com/attachments/610695135738593282/710590989437501450/blazing.gif"}}))
      (if (some? (-> content .toLowerCase (#(re-find #"twitch\.tv/|smash\.gg/" %))))
        (let [channel-name (:name @(m/get-channel! (:messaging @state) channel-id))
              mesg (format "%s linked to a twitch.tv stream in the message - %s - in %s"
                           author content channel-name)]
          (println mesg)
          (m/create-message!
            (:messaging @state) 406853584202760192 ; this should *really* go in a config file
            :content mesg)))
      ;;;
      (if (.startsWith content ".")
        (let [sp (.split (.substring content 1) " ")
              comm (first sp)
              args (rest sp)]
          (if-let [resp (command/invoke-command
                      {:command comm
                      :args args}
                      state)]
            (m/create-message!
             (:messaging @state) channel-id
             (:type resp) (:value resp))))
        (logging/log-raw (:logger @state) content)))))

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
                    :apikeys (:apikeys config)
                    :logger (logging/create-rotating-logger (:logfile config))
                    :admin (:admin config)}]
    (reset! state init-state)
    (e/message-pump! event-ch handle-event)
    (m/stop-connection! messaging-ch)
    (c/disconnect-bot! connection-ch)))