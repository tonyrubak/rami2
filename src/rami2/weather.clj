(ns rami2.weather
  (:require [clojure.data.json :as json]))

(defn get-weather
  [location state]
  (let [response (json/read-str
                  (slurp
                   (format
                    "https://api.openweathermap.org/data/2.5/weather?q=%s&units=imperial&appid=%s"
                    location
                    (:axios (:apikeys @state)))))]
    {:color 0x0099ff
     :title (format "It is currently %.0f\u00b0 F in %s" (get (get response "main") "temp") (get response "name"))
     :thumbnail {:url (format "http://openweathermap.org/img/w/%s.png" (get (nth (get response "weather") 0) "icon"))}
     :footer {:text " "}
     :fields [{:name "Daily High" :value (format "%.0f\u00b0 F" (double (get (get response "main") "temp_max"))) :inline true}
              {:name "Daily Low" :value (format "%.0f\u00b0 F" (double (get (get response "main") "temp_min"))) :inline true}
              {:name "Humidity" :value (format "%.0f %%" (double (get (get response "main") "humidity"))) :inline true}
              {:name "Wind Speed" :value (format "%.0f miles per hour" (double (get (get response "wind") "speed"))) :inline true}
              {:name "Pressure" :value (format "%.2f inHg" (* 0.02953 (double (get (get response "main") "pressure")))) :inline true}
              {:name "Cloudiness" :value (format "%s" (get (nth (get response "weather") 0) "description")) :inline true}]}))