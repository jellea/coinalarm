(ns coinalarm.communication
  (:require [org.httpkit.client :as client]
            [clojure.data.json :as json]))

(def auth-keys {:AUTH_ID (or (System/getenv "PLIVO_AUTH_ID") "")
                :AUTH_TOKEN (or (System/getenv "PLIVO_AUTH_TOKEN") "")
                :NUMBER (or (System/getenv "PLIVO_NUMBER") "")})

(def url (str "https://api.plivo.com/v1/Account/" (:AUTH_ID auth-keys) "/Message/"))

(defn send-sms [phone text]
  (client/post url {:basic-auth [(:AUTH_ID auth-keys) (:AUTH_TOKEN auth-keys)]
                    :headers {"Content-Type" "application/json"}
                    :body (json/write-str {"dst" phone "text" text "src" (:NUMBER auth-keys) "type" "sms"})}))
