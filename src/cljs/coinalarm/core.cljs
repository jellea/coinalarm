(ns coinalarm.core
  (:require [coinalarm.phone :as phone]
            [coinalarm.splash :as splash]
            [coinalarm.markets :as markets]
            [coinalarm.alarms :as alarms]
            [sablono.core :as html :refer-macros [html]]
            [om.core :as om :include-macros true]))

(enable-console-print!)

(def app-state (atom {:page "alarms"
                      :number ""}))

(defn main-component [cursor owner]
  (om/component
    (html
        [:div.container
             [:div.login
              [:p "login"]]

            [:div {:className (str "box " (:page cursor))}
              (cond
                (= (:page cursor) "splashscreen") (om/build splash/splashscreen cursor)
                (= (:page cursor) "phoneform") (om/build phone/phone-box cursor)
                (= (:page cursor) "marketselector") (om/build markets/market-selector cursor)
                (= (:page cursor) "alarms") (om/build alarms/alarms-selector cursor)
              )]
            [:div.footer
              [:p "about - open source"]]])))

(om/root main-component app-state {:target (. js/document (getElementById "app"))})

