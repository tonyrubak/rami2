(ns rami2.awstr
  (:require [cognitect.aws.client.api :as aws]
            [clojure.data.json :as json]
            [clojure.string :as str]
            [rami2.command :as command]))

(defn format-request [text source target]
  {:op :TranslateText
  :request {:Text (json/write-str text)
            :SourceLanguageCode source
            :TargetLanguageCode target}})

(defn transform-response [resp]
  (-> resp
      :TranslatedText
      org.apache.commons.lang3.StringEscapeUtils/unescapeJava
      (.replaceAll "\"" "")))

(defn query-translate [text source target]
  (aws/invoke
    (aws/client {:api :translate})
    (format-request text source target)))

(defmethod command/invoke-command "awstr" [cmd message state]
  (let [source (first (:args cmd))
        target (first (rest (:args cmd)))
        query (str/join " " (rest (rest (:args cmd))))
        translation (transform-response
                      (query-translate query source target))]
    {:type :content
    :value translation}))