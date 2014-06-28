(ns coinalarm.alarm
  (:require [coinalarm.btc :as btc]
            [coinalarm.persistence :as pers]
            [coinalarm.communication :as comm]))


(defn update []
  (let [users (pers/get-all-user)
        filter-subscriptions (fn [data]
                               (map #(filter (fn [{:keys [symbol price upper lower]}]
                                               (let [symbol-data (symbol data)]
                                                 (or (and upper (> price (:price symbol-data)))
                                                     (and (not upper) (< price (:price symbol-data))))))
                                             (:subscritions %)) users))
        filter-users #(filter (comp not-empty :subscritions) %)
        send-sms-to-user (fn [data]
                           (doseq [user data] #(comm/send-sms (:phone %) "Woop woop")))]
    (btc/get-latest-symbols (comp send-sms-to-user filter-users filter-subscriptions))))
