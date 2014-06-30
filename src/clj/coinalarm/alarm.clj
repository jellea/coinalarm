(ns coinalarm.alarm
  (:require [coinalarm.btc :as btc]
            [coinalarm.persistence :as pers]
            [coinalarm.communication :as comm]
            [clojure.core.async :as async :refer [<! timeout chan go]]))

(def milisecs-to-wait 1000)
(def running (atom true))

(defn running? []
  @running)

(defn stop []
  (reset! running false))

(defn create-sms-text [{:keys [subscription user]}]
  (str "Hi user with number "
       (:phone user)
       ". The price on the market " (:symbol (:symbol subscription))
       " is now with "
       (-> subscription :symbol :price)
       (-> subscription :symbol :currency)
       " "
       (if (:upper subscription) "higher" "lower")
       " than " (:price subscription) (:currency subscription)
       "."))

(defn subscription-valid? [{:keys [price currency upper] :as subscription} latest-btc]
  (let [symbol-price (-> latest-btc (get currency) (get (:symbol subscription)) :price)]
    (and symbol-price ;; shouldn't happen -> symbol not in the latest fetch
         (or
          (and upper (< price symbol-price))
          (and (not upper) (> price symbol-price))))))

(defn whom-to-send-sms [users latest-btc]
  (let [users-with-filtered-subscriptions (map #(assoc %
                                                  :subscriptions
                                                  (for [sub (:subscriptions %)
                                                        :when (subscription-valid? sub latest-btc)]
                                                        (assoc ;; not sure if this is the right place to assoc the symbol
                                                          sub :symbol
                                                          (-> latest-btc (get (:currency sub)) (get (:symbol sub))))))
                                               users)
        filtered-users (filter (comp not-empty :subscriptions) users-with-filtered-subscriptions)]
    filtered-users))

(defn update [latest-btc]
  (let [users (pers/get-all-user)
        delete-subscriptions (fn [updated-users]
                               (let [user-hash (apply merge (map #(hash-map (:phone %) %) users))]
                                 (doseq [user updated-users]
                                   (pers/update-user! (assoc user
                                                        :subscriptions
                                                        (filter #(not (subscription-valid? % latest-btc))
                                                                (:subscriptions (user-hash (:phone user)))))))))
        send-sms-to-users (fn [users]
                            (doseq [user users]
                              (doseq [sub (:subscriptions user)]
                                (print {:phone (:phone user)
                                        :text (create-sms-text {:user user
                                                                :subscription sub})}))))]
    ((comp (juxt delete-subscriptions send-sms-to-users)
           (partial whom-to-send-sms users)) latest-btc)))

(go
 (while (running?)
   (<! (timeout milisecs-to-wait))
   (btc/get-latest-symbols-mockup update)))
