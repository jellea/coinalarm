(ns coinalarm.splash
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]))

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
              [:div.front
                [:img {:src "img/logo.svg"}]
                [:h1 "CoinAlarm"]
                [:h3 "Get a SMS message when its time to sell or buy Bitcoins*!"]
                [:p "*and other alt coins"]
                [:div.box-footer
                  [:a.button {:href "#"
                       :onClick (fn [] (om/transact! cursor #(assoc % :page "phoneform")))} "start!"]]
                [:div.backplate nil]]
              [:div#chart nil]
             ]))))
