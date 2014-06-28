(ns coinalarm.core
  (:require [cljs.reader :as reader]
            [coinalarm.phone :as phone]
            [coinalarm.splash :as splash]
            [sablono.core :as html :refer-macros [html]]
            [coinalarm.markets :as markets]
            [om.core :as om :include-macros true]))

(enable-console-print!)

(def app-state (atom {:page :splash
                      :number ""}))

(defn main-component [cursor owner]
  (om/component
    (html [:div.container
            (cond
              (= (:page cursor) :splash) (om/build splash/splashscreen cursor)
              (= (:page cursor) :phone) (om/build phone/phone-box cursor)
              )])))

(om/root main-component app-state {:target (. js/document (getElementById "app"))})
