(ns coinalarm.tests
  (:require [coinalarm.persistence :as pers]))


(defn generate-users [n]
  (map #(hash-map :phone % :subscriptions [{:price 1.3 :symbol "bcEUR" :upper true}]) (range n)))

;; make sure you switched to a testing database

(defn setup []
  (do (pers/reset-users!)))

(defn test-users []
  (let [users (generate-users 10)]
    (do (setup)
        (doseq [user users] (pers/create-user! user)))
        (let [retrieved-users (pers/get-all-user)]
          (assert (= (count users) (count retrieved-users))))))

(setup)
(test-users)
