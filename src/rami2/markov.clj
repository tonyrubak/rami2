(ns rami2.markov
  (:require [cognitect.aws.client.api :as aws]
            [clojure.data.json :as json]
            [rami2.command :as command]))

(defn markov
  [prefix state]
  (if (= prefix "communism")
    "communism begins"
    (let [resp (aws/invoke (aws/client {:api :lambda})
                           {:op :Invoke
                            :request {:FunctionName "markov"
                                      :Payload (format "{ \"prefix\": \"%s\" }"
                                                       prefix)}})]
      (-> resp
          :Payload
          slurp
          json/read-str
          (get "body")
          (org.apache.commons.lang3.StringEscapeUtils/unescapeJava)
          (.replaceAll "\"" "")))))

(defmethod command/invoke-command "markov" [cmd state]
  {:type :content
    :value (markov (-> cmd
                       :args
                       first)
                   state)})


; (def resp (aws/invoke (aws/client {:api :lambda})
;                       {:op :Invoke
;                        :request {:FunctionName "markov"
;                        :Payload "{ \"prefix\": \"🙂\" }"}}))
; (get (json/read-str
;       "{\"statusCode\": 200, \"body\": \"\\\"\\\\ud83d\\\\ude42\\\"\"}")
;      "body")

; (load-string (.replaceAll "\"\\ud83d\\ude42\"" "\"" ""))

; (java.net.URLDecoder/decode "\\ud83d\\ude42" "UTF-8")

; (org.apache.commons.lang3.StringEscapeUtils/unescapeJava "\\ud83d\\ude42")

; (clojure.string/replace 
;  "\\ud83d\\ude42"
;  #"\\u([a-z0-9].)" "\\u$1")