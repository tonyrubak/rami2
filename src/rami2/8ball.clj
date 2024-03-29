(ns rami2.8ball
  (:require [rami2.command :as command]
            [rami2.storage :as storage]
            [clojure.string :as str]))

(def responses '("It is Certain.", "It is decidedly so.", "Without a doubt.",
                "Yes definitely.", "You may rely on it.", "As I see it, yes.",
                "Most likely.", "Outlook good.", "Yes.", "Signs point to yes.",
                "Reply hazy, try again.", "Ask again later.",
                "Better not tell you now.", "Cannot predict now.",
                "Concentrate and ask again.", "Don't count on it.",
                "My reply is no.", "My sources say no.",
                "Outlook not so good.", "Very doubtful."))

(defmethod command/invoke-command "8ball" [command message state]
    {:type :content
    :value (rand-nth responses)})