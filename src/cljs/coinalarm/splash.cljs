(ns coinalarm.splash
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]))

(defn advance-page [cursor]
  (om/transact! cursor ()))

(defn splashscreen [cursor owner]
  (reify
    om/IDidMount
    (did-mount [_]
      (prn "did mount")
      (.drawChart js/window)
    )
    om/IRender
    (render [_]
      (html [:div
             ;[:div#chart nil]
              [:div.front
                [:h1 "CoinAlarm"]
                [:h3 "Get a SMS message when its time to sell or buy Bitcoins*!"]
                [:sub "*and alt coins"]
                [:div {:onClick (fn [] (om/transact! cursor #(assoc % :page "phoneform")))} "start"]]]))))
