(ns coinalarm.core
  (:require [coinalarm.phone :as phone]
            [coinalarm.splash :as splash]
            [coinalarm.markets :as markets]
            [coinalarm.alarms :as alarms]
            [coinalarm.about :as about]
            [sablono.core :as html :refer-macros [html]]
            [om.core :as om :include-macros true]))

(enable-console-print!)

(def app-state (atom {:page "alarms"
                      :number ""
                      :modal nil}))

(defn main-component [cursor owner]
  (om/component
    (prn (:modal cursor))

    (html
        [:div.container
          [:div.login
            [:p "login"]]

          (if (:modal cursor)
            (om/build (:modal cursor) cursor))

          [:div {:className (str "box " (:page cursor))}
            (cond
              (= (:page cursor) "splashscreen") (om/build splash/splashscreen cursor)
              (= (:page cursor) "phoneform") (om/build phone/phone-box cursor)
              (= (:page cursor) "marketselector") (om/build markets/market-selector cursor)
              (= (:page cursor) "alarms") (om/build alarms/alarms-selector cursor)
            )]

          [:div.footer
            [:p
              [:a {:onClick (fn [] (om/transact! cursor #(assoc % :modal about/about-modal)))} "about"]
              " - "
              [:a {:onClick (fn [] (om/transact! cursor #(assoc % :modal about/about-modal)))} "open source"]]]])))

(om/root main-component app-state {:target (. js/document (getElementById "app"))})
