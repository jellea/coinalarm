(ns coinalarm.persistence
  (:require [korma.db :as kdb]
            [korma.core :as k]
            [clojure.java.jdbc.deprecated  :as j]))

(def sqll (kdb/sqlite3 {:db "coinalarm-database"}))

(kdb/defdb kpg sqll)

(declare users alarms markets historical-data)

(k/defentity users
  (k/has-many alarms {:fk :user_id}))

(k/defentity alarms
  (k/belongs-to users {:fk :user_id})
  (k/belongs-to markets {:fk :market_name}))

(k/defentity markets
  (k/pk :name)
  (k/has-many alarms)
  (k/has-many historical-data))

(k/defentity historical-data
  (k/belongs-to markets))

(defn create-users []
  (j/create-table
   :users
   [:id :integer "PRIMARY KEY" "AUTOINCREMENT"]
   [:phone :integer "NOT NULL" "UNIQUE"])) ;; maybe this could also be the primary key

(defn create-alarms []
  (j/create-table
   :alarms
   [:id :integer "PRIMARY KEY" "AUTOINCREMENT"]
   [:user_id :integer "REFERENCES USERS (phone)"]
   [:market_name :text "REFERENCES MARKETS (name)"]
   [:amount :real "NOT NULL"]
   [:up :integer]))

(defn create-markets []
  (j/create-table
   :markets
   [:name :text "PRIMARY KEY"]
   [:currency :text "NOT NULL"]))

(defn create-historical-data []
  (j/create-table
   :historical_data
   [:id :integer "PRIMARY KEY" "AUTOINCREMENT"]
   [:trading_date :date "NOT NULL" "DEFAULT CURRENT_DATE"]
   [:market_name :text "REFERENCES MARKETS (name)"]
   [:high :real "NOT NULL"]
   [:low :real "NOT NULL"]
   [:close :real "NOT NULL"]
   [:volume :integer "NOT NULL"]
   [:avg :integer "NOT NULL"]
   [:ask :real]
   [:bid :real]))

(defn invoke-with-connection [f]
  (j/with-connection
     sqll
     (j/transaction
       (f))))

(defn create-tables []
  (do (create-users)
      (create-markets)
      (create-alarms)
      (create-historical-data)))

(defn set-up! []
  (invoke-with-connection create-tables))

(set-up!)

(k/insert users (k/values {:phone 1212312}))
(k/select users)

(k/insert markets (k/values {:name "test" :currency "EUR"}))
(k/insert alarms (k/values {:user_id 1, :market_name "test", :amount 4.2, :up 0}))

(k/select users (k/with alarms (k/with markets)))
