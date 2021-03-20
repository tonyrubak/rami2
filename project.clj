(defproject rami2 "0.2.1-SNAPSHOT"
    :description "FIXME: write description"
    :url "http://example.com/FIXME"
    :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
              :url "https://www.eclipse.org/legal/epl-2.0/" }
    :dependencies [[org.clojure/clojure "1.10.1"]
                    [org.suskalo/discljord "1.2.0"]
                    [org.apache.commons/commons-lang3 "3.10"]  
                    [com.cognitect.aws/api "0.8.456"]
                    [com.cognitect.aws/endpoints "1.1.11.789"]
                    [com.cognitect.aws/dynamodb "799.2.679.0"]
                    [com.cognitect.aws/lambda "796.2.667.0"]
                    [clj-http "3.10.1"]]
    :main ^:skip-aot rami2.core
    :target-path "target/%s"
    :profiles {:uberjar {:aot :all}})