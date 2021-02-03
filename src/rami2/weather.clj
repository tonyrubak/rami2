(ns rami2.weather
  (:require [clojure.data.json :as json]
            [clojure.string :as str]
            [clj-http.client :as client]
            [rami2.command :as command]))

(defn format-request [api-key query]
  {:query-params {:units "imperial"
                  :appid api-key
                  :q query}})

(defn format-response [response]
  {:color 0x0099ff
   :title (format "It is currently %.0f\u00b0 F in %s" (get (get response "main") "temp") (get response "name"))
   :thumbnail {:url (format "http://openweathermap.org/img/w/%s.png" (get (nth (get response "weather") 0) "icon"))}
   :footer {:text " "}
   :fields [{:name "Daily High" :value (format "%.0f\u00b0 F" (double (get (get response "main") "temp_max"))) :inline true}
           {:name "Daily Low" :value (format "%.0f\u00b0 F" (double (get (get response "main") "temp_min"))) :inline true}
           {:name "Humidity" :value (format "%.0f %%" (double (get (get response "main") "humidity"))) :inline true}
           {:name "Wind Speed" :value (format "%.0f miles per hour" (double (get (get response "wind") "speed"))) :inline true}
           {:name "Pressure" :value (format "%.2f inHg" (* 0.02953 (double (get (get response "main") "pressure")))) :inline true}
           {:name "Cloudiness" :value (format "%s" (get (nth (get response "weather") 0) "description")) :inline true}]})

(defn query-openweather [location state]
  (json/read-str
    (:body
     (client/get
      "https://api.openweathermap.org/data/2.5/weather"
      (format-request (:axios (:apikeys @state)) location)))))

(defmethod command/invoke-command "w" [cmd message state]
  (let [query (str/join " " (:args cmd))]
    {:type :embed
     :value (format-response (query-openweather query state))}))