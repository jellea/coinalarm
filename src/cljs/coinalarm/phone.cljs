(ns coinalarm.phone
  (:require [cljs.reader :as reader]
            [goog.events :as events]
            [goog.dom :as gdom]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true])
  (:import [goog.net XhrIo]
           goog.net.EventType
           [goog.events EventType]))

(enable-console-print!)



(def app-state (atom {:number ""}))

(defn send-number [e state owner]
  (println (-> (om/get-node owner "phone-field")
               .-value)))

(defn validate-number [phone-number]
  true)

(defn handle-number [e state owner]
  (let [phone-number (e -target -value)
        valid (validate-number phone-number)]
    (println "valid" valid)
    (om/set-state! owner :number phone-number )
    (om/set-state! owner :valid valid)))

(defn phone-box [app owner]
  (reify
    om/IInitState
    (init-state [this]
      {:number ""
       :valid false})
    om/IRenderState
    (render-state [this state]
      (dom/div nil
         (dom/p nil "Fill in your phone number to receive text messages")
         (dom/input #js {:placeholder "+47 999 999 000"
                         :ref "phone-field"
                         :onChange #(handle-number % state owner)
                         :value (:number state)})
         (dom/div nil
           (dom/button #js {:onClick #(send-number % state owner)
                            :disabled (not (:valid state))} "Done"))))))


(println "Mounting phone box")
(om/root phone-box app-state {:target (. js/document (getElementById "phone"))})
