(ns coinalarm.markets
  (:require [sablono.core :as html :refer-macros [html]]
            [om.core :as om :include-macros true]))

(enable-console-print!)

(def coins [{:name "Bitcoin/US Dollar"  :market "USD-BTC" }
            {:name "Dogecoin/US Dollar" :market "USD-DOGE" }
            {:name "Litecoin/US Dollar" :market "USD-LTC" }
            {:name "Bitcoin/Yen"        :market "YEN-BTC" }
            {:name "Dogecoin/Yen"       :market "YEN-DOGE"}])

(def markets [{:name "Trader Joe" :code "TRJ" :capabilities #{:USD-BTC :USD-DOGE :USD-LTC} }
              {:name "Another Trader" :code "ATR" :capabilities #{:USD-BTC :YEN-DOGE :YEN-BTC} }
              {:name "Third Trader" :code "TT" :capabilities #{:USD-BTC :USD-DOGE :USD-LTC :YEN-DOGE :YEN-BTC} }])


(defn find-markets
  "Seeks through the markets to find the ones that supports the relevant coins"
  [coin-market markets]
  (let [coin-market (keyword coin-market)]
    (filter (fn [market]
              (contains? (:capabilities market) coin-market)) markets)))

(defn handle-coin [e state owner]
  (let [coin (.. e -target -value)
        valid-markets (find-markets coin markets)]
    (println "setting coin" coin)
    (om/set-state! owner :markets valid-markets)
    (om/set-state! owner :coin coin)))

(defn handle-market [e state owner]
  (let [code (.. e -target -value)]
    (om/set-state! owner :market code)))

(defn send-markets [e state app owner]
  (let [market (:market state)
        coin   (:coin state)]
    (println "sending" market coin)
    (om/transact! app #(assoc % :page "alarms"))))

(defn market-selector [app owner]
  (reify
    om/IInitState
    (init-state [this]
      (let [coin (-> coins first :market)
            current-markets (find-markets coin markets)]
        {:coin coin
         :coins coins
         ;; the markets list defined on the namespace lists all available mkts
         ;; the state should show a subset of those
         :markets current-markets
         :market (-> current-markets first :code)}))
    om/IRenderState
    (render-state [this state]
      (println (:markets state))
      (html
        [:div
         [:h1 "Great!"]
         [:p "A confirmation text has been sent."]
         [:p "Let's set you up meanwhile:"]
         [:div
           [:p "What do you trade?"]
           [:select {:ref "coin-field"
                            :onChange #(handle-coin % state owner)
                            :value (:coin state)} ""
                    (map #(html [:option {:value (:market %)} (:name %)]) (:coins state))
            ]]
         [:div
            [:p "Where do you trade?"]
            [:select {:ref "market-field"
                             :onChange #(handle-market % state owner)
                             :value (:market state)}
                    (map #(html [:option {:value (:code %)} (:name %)]) (:markets state))]]
                     ;;(map #(render-alarm % cursor owner state) (:alarms state))]]))))
         [:div.box-footer
           [:a.button {:href "#"
                       :onClick #(send-markets % state app owner)} "done"]]]))))
