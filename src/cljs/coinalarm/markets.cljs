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

(def coins [{:name "Bitcoin/US Dollar" :market :USD-BTC }
            {:name "Dogecoin/US Dollar" :market :USD-DOGE }
            {:name "Litecoin/US Dollar" :market :USD-LTC }
            {:name "Bitcoin/Yen" :maker :YEN-BTC }
            {:name "Dogecoin/Yen" :maker :YEN-DOGE}])

(def markets [{:name "Trader Joe" :capabilities #{:USD-BTC :USD-DOGE :USD-LTC} }
              {:name "Another Trader" :capabilities #{:USD-BTC :YEN-DOGE :YEN-BTC} }
              {:name "Third Trader" :capabilities #{:USD-BTC :USD-DOGE :USD-LTC :YEN-DOGE :YEN-BTC} }])

(def app-state (atom {:coins coins
                      :markets markets}))

(defn find-markets
  "Seeks through the markets to find the ones that supports the relevant coins"
  [coin-market markets]
  (filter (fn [market]
              (contains? (:capabilities market) coin-market)) markets))


(defn handle-coin [e state owner]
  (let [coin (.. e -target -value)
        valid-markets (find-markets coin markets)]
    (println "setting coin" coin)
    (om/set-state! owner :markets valid-markets)
    (om/set-state! owner :coin coin)))

(defn market-selector [app owner]
  (reify
    om/IInitState
    (init-state [this]
      {:coin ""
       :coins (:coins app)
       ;; the markets list defined on the namespace lists all available mkts
       ;; the state should show a subset of those
       :markets (find-markets (first (:coins app)) markets)
       :market ""})
    om/IRenderState
    (render-state [this state]
      (dom/div nil
         (dom/h1 nil "Great!")
         (dom/p nil "A confirmation text has been sent")
         (apply dom/select #js {:ref "coin-field"
                         :onChange #(handle-coin % state owner)
                         :value (:coin state)}
              (map #(dom/option #js {:value (:market %)} (:name %)) (:coins state)))       )

                  )))


(println "Mounting market box")
(om/root market-selector app-state {:target (. js/document (getElementById "markets"))})
