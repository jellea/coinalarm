(ns coinalarm.core
  (:require [coinalarm.phone :as phone]
            [coinalarm.splash :as splash]
            [sablono.core :as html :refer-macros [html]]
            [coinalarm.markets :as markets]
            [om.core :as om :include-macros true]))

(enable-console-print!)

(def app-state (atom {:page "splashscreen"
                      :number ""}))

(defn main-component [cursor owner]
  (om/component
    (html [:div.container
            [:div {:className (str "box " (:page cursor))}
              (cond
                (= (:page cursor) "splashscreen") (om/build splash/splashscreen cursor)
                (= (:page cursor) "phoneform") (om/build phone/phone-box cursor)
                )]])))

(om/root main-component app-state {:target (. js/document (getElementById "app"))})
