(ns coinalarm.btcpay
  (:require [sablono.core :as html :refer-macros [html]]
            [om.core :as om :include-macros true]))

(defn btcpay [app owner]
  (om/component
    (html
      [:div
        [:h2 "All your alarms are set"]
        [:p "Put some credits on your account to start receiving text messages."]])))
