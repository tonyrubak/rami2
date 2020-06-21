(ns rami2.core
  (:gen-class)
  (:require [discljord.connections :as c]
            [discljord.messaging :as m]
            [discljord.events :as e]
            [clojure.core.async :as a]
            [clojure.data.json :as json]
            [clojure.string :as str]
            [rami2.storage :as storage]
            [rami2.weather :as wx]
            [rami2.markov :as markov]
            [rami2.logging :as logging]
            [rami2.search :as search]))

(def state (atom nil))

(defmulti handle-event
  (fn [event-type event-data]
    event-type))

(defmethod handle-event :default
  [event-type event-data])

(defmethod handle-event :message-create
  [event-type {{bot :bot} :author :keys [channel-id content]}]
  (if (= content "!disconnect")
    (a/put! (:connection @state) [:disconnect])
    (when-not bot
      (if (.contains (.toLowerCase content) "eddie")
        (m/create-message!
         (:messaging @state) channel-id
         :embed {:image {:url "https://cdn.discordapp.com/attachments/173094635391025152/691489861739216906/691114417013915740.png"}}))
      (if (.contains (.toLowerCase content) "bullshit")
        (m/create-message!
         (:messaging @state) channel-id
         :embed {:image {:url "https://cdn.discordapp.com/attachments/610695135738593282/710590989437501450/blazing.gif"}}))
      (if (.startsWith content ".")
        (let [sp (.split (.substring content 1) " ")
              command (first sp)
              args (rest sp)]
          (case command
            "aka" (storage/set-aka args state)
            "w" (m/create-message!
                 (:messaging @state) channel-id
                 :embed (wx/get-weather
                         (java.lang.String/join " " args) state))
            "markov" (m/create-message!
                      (:messaging @state) channel-id
                      :content (markov/markov (first args) state))
            "bing" (m/create-message!
                    (:messaging @state) channel-id
                    :embed (let [resp (search/get-search-response args state)
                                q (str/join " " args)]
                             {:title (format "Bing results for %s" q)
                             :type "link"
                             :description (get resp "snippet")
                             :url (get resp "url")
                             :fields [{:name "URL" :value (get resp "url")}]
                             :thumbnail {:url "https://1000logos.net/wp-content/uploads/2017/12/bing-emblem.jpg"}})))
            (let [response (storage/get-aka command state)]
              (when-not (nil? response)
                (m/create-message!
                 (:messaging @state) channel-id
                 :content response)))))
        (logging/log-raw (:logger @state) content))))

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
                    :logger (logging/create-rotating-logger (:logfile config))}]
    (reset! state init-state)
    (e/message-pump! event-ch handle-event)
    (m/stop-connection! messaging-ch)
    (c/disconnect-bot! connection-ch)))