(ns coinalarm.alarm
  (:require [coinalarm.btc :as btc]
            [coinalarm.persistence :as pers]
            [korma.core :as k]
            [coinalarm.communication :as comm]
            [clojure.core.async :as async :refer [<! timeout chan go]]))

(def milisecs-to-wait 1000)
(def running (atom true))

(defn running? []
  @running)

(defn stop []
  (reset! running false))

(defn create-sms-text [{:keys [alarm user]}]
  (str "Hi user with number "))

(defn alarm-valid? [{:keys [currency market_name amount up]} latest-btc]
  (let [current-market (-> market_name currency latest-btc)
        ask (:ask current-market)]
    (or (and (> ask amount) (= up 1)) (and (< ask amount) (= up 0)))))

;; (defn update [latest-btc]
;;   (let [
;;         users (k/select pers/users (k/with pers/alarms (k/with pers/markets)))
;;         filtered-alarms (map (fn [{:keys [alarms] :as user}]
;;                                (assoc user :alarms
;;                                  (filter #(alarm-valid? % latest-btc) alarms))) users)
;;         send-sms-to-users (map #(map (fn [alarm] (create-sms-text alarm %))
;;                                      (:alarms %))
;;                                filtered-alarms)
;;         ]
;;     send-sms-to-users))

(defn update [latest-btc]
  ;; filter alarms
  ;; send appropriate sms
  ;; delete alarms for which we just send a sms (or just mark them as already send)
  ;; include latest fatch into the historic-data-table
  )

(go
 (while (running?)
   (<! (timeout milisecs-to-wait))
   (btc/get-latest-symbols-mockup update)))
