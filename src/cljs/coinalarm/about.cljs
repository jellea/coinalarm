(ns coinalarm.about
  (:require [sablono.core :as html :refer-macros [html]]
            [om.core :as om :include-macros true]))

;(def modal-props (atom {:hidden false})

(defn about-modal [cursor owner]
  (om/component
    (html [:div.modal
            [:div.greyout {:onClick (fn [] (om/transact! cursor #(assoc % :modal nil)))} nil]
            [:div.container
              [:p "Coinalarm is"]
              [:p "Made by .."]]])))

(defn opensource-modal [cursor owner]
  (om/component
    (html [:div.modal
            [:div.greyout {:onClick (fn [] (om/transact! cursor #(assoc % :modal nil)))} nil]
            [:div.container
              [:p "You can find Coinalarm's source on Github"]]])))
