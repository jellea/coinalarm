(ns coinalarm.communication
  (:require [org.httpkit.client :as client]
            [clojure.data.json :as json]))

(def auth-keys {:AUTH_ID (or (System/getenv "PLIVO_AUTH_ID") "woop")
                :AUTH_TOKEN (or (System/getenv "PLIVO_AUTH_TOKEN") "voop")})

(def url (str "https://api.plivo.com/v1/Account/" (:AUTH_ID auth-keys) "/Message/"))

(defn send-sms [{:keys [phone text callback]}]
  (client/post url {:query-params {:dst phone :text text :src 0000 :type "sms"}} callback))


