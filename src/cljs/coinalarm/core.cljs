(ns coinalarm.core
  (:require [coinalarm.phone :as phone]
            [coinalarm.splash :as splash]
            [coinalarm.markets :as markets]
            [coinalarm.alarms :as alarms]
            [coinalarm.about :as about]
            [coinalarm.phoneconfirm :as phoneconfirm]
            [coinalarm.btcpay :as btcpay]
            [sablono.core :as html :refer-macros [html]]
            [om.core :as om :include-macros true]))

(enable-console-print!)

(def app-state (atom {:page "splashscreen"
                      :number ""
                      :alarms [{:value 929 :text "" :key 1}
                               {:value 530 :text "" :key 2}]
                      :modal nil}))

(defn main-component [cursor owner]
  (om/component
    ;;(prn (:modal cursor))

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
              (= (:page cursor) "phoneconfirm") (om/build phoneconfirm/phoneconfirm cursor)
              (= (:page cursor) "btcpay") (om/build btcpay/btcpay cursor)
            )]

          [:div.footer
            [:p
              [:a {:onClick (fn [] (om/transact! cursor #(assoc % :modal about/about-modal)))} "about"]
              " - "
              [:a {:onClick (fn [] (om/transact! cursor #(assoc % :modal about/opensource-modal)))} "open source"]]]])))

(om/root main-component app-state {:target (. js/document (getElementById "app"))})

