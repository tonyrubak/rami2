(ns rami2.weather
    (:require [clojure.data.json :as json]))

(defn get-weather
    [location state]
    (let [response (json/read-str
                        (slurp
                            (format
                                "https://api.openweathermap.org/data/2.5/weather?q=%s&units=imperial&appid=%s"
                                location
                                (:axios (:apikeys @state)))))
                embed { :color 0x0099ff
                    :title (format "It is currently %.0f\u00b0 F in %s" (get (get response "main") "temp") (get response "name"))}]
        embed))