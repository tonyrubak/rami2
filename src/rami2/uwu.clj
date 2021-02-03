(ns rami2.uwu
  (:require [rami2.command :as command]
            [rami2.storage :as storage]
            [clojure.string :as str]))

(def faces '("(*^ω^)", "(◕‿◕✿)", "(◕ᴥ◕)", "ʕ•ᴥ•ʔ", "ʕ￫ᴥ￩ʔ", "(*^.^*)", "owo", "(｡♥‿♥｡)", "uwu", "(*￣з￣)", ">w<", "^w^", "(つ✧ω✧)つ", "(// =ω=)//"))

(defn uwuify [s]
  (-> s
      (str/replace #"[l|r]" "w")
      (str/replace #"[L|R]" "W")
      (str/replace #"n([aeiou])" "ny$1")
      (str/replace #"N([aeiouAEIOU])" "Ny$1")
      (str/replace #"ove" "uv")
      (str/replace #"!+" (rand-nth faces))))

(defmethod command/invoke-command "uwu" [command message state]
  (let [uwu (uwuify (str/join " " (:args command)))]
    {:type :content
     :value uwu}))