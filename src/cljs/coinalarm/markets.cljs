(ns coinalarm.markets
  (:require [cljs.reader :as reader]
            [goog.events :as events]
            [goog.dom :as gdom]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true])
  (:import [goog.net XhrIo]
           goog.net.EventType
           [goog.events EventType]))

(enable-console-print!)

(def coins [{:name "Bitcoin/US Dollar"  :market :USD-BTC }
            {:name "Dogecoin/US Dollar" :market :USD-DOGE }
            {:name "Litecoin/US Dollar" :market :USD-LTC }
            {:name "Bitcoin/Yen"        :market :YEN-BTC }
            {:name "Dogecoin/Yen"       :market :YEN-DOGE}])

(def markets [{:name "Trader Joe" :code "TRJ" :capabilities #{:USD-BTC :USD-DOGE :USD-LTC} }
              {:name "Another Trader" :code "ATR" :capabilities #{:USD-BTC :YEN-DOGE :YEN-BTC} }
              {:name "Third Trader" :code "TT" :capabilities #{:USD-BTC :USD-DOGE :USD-LTC :YEN-DOGE :YEN-BTC} }])

(def app-state (atom {:coins coins
                      :markets markets}))

(defn find-markets
  "Seeks through the markets to find the ones that supports the relevant coins"
  [coin-market markets]
  (filter (fn [market]
              (contains? (:capabilities market) coin-market)) markets))



(defn handle-coin [e state owner]
  (let [coin (.. e -target -value)
        valid-markets (find-markets (:market coin) markets)]
    (println "setting coin" coin)
    (om/set-state! owner :markets valid-markets)
    (om/set-state! owner :coin coin)))

(defn handle-market [e state owner]
  (let [code (.. e -target -value)]
    (om/set-state! owner :market code)))

(defn market-selector [app owner]
  (reify
    om/IInitState
    (init-state [this]
      {:coin ""
       :coins (:coins app)
       ;; the markets list defined on the namespace lists all available mkts
       ;; the state should show a subset of those
       :markets (find-markets (-> app :coins first :market) markets)
       :market ""})
    om/IRenderState
    (render-state [this state]
      (println (:markets state))
      (dom/div nil
         (dom/h1 nil "Great!")
         (dom/p nil "A confirmation text has been sent")
         (dom/div nil
           (apply dom/select #js {:ref "coin-field"
                         :onChange #(handle-coin % state owner)
                         :value (:coin state)}
              (map #(dom/option #js {:value (:market %)} (:name %)) (:coins state))))
         (dom/div nil
            (apply dom/select #js {:ref "market-field"
                         :onChange #(handle-market % state owner)
                         :value (:market state)}
              (map #(dom/option #js {:value (:code %)} (:name %)) (:markets state))))))))


(println "Mounting market box")
(om/root market-selector app-state {:target (. js/document (getElementById "markets"))})
