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
  (when (:valid state)
    (let [phone-number (-> (om/get-node owner "phone-field")
                           .-value)]
      (println "sendint phone number" phone-number))))

(defn validate-number [phone-number]
  ;; TODO: validate phone no
  true)

(defn handle-number [e state owner]
  (let [phone-number (.. e -target -value)
        valid (validate-number phone-number)
        ;; show error msg when number is filled in and invalid
        message (and (not valid)
                     (not (empty? phone-number)))]
    (om/set-state! owner :number phone-number)
    (om/set-state! owner :valid valid)
    (om/set-state! owner :message message)))

(defn phone-box [app owner]
  (reify
    om/IInitState
    (init-state [this]
      {:number ""
       :valid false
       :message false})
    om/IRenderState
    (render-state [this state]
      (dom/div nil
         (dom/p nil "Fill in your phone number to receive text messages")
         ;; error message
         (when (:message state)
            (dom/div nil "Yo that's not a real phone number"))
         (dom/input #js {:placeholder "+47 999 999 000"
                         :ref "phone-field"
                         :onChange #(handle-number % state owner)
                         :value (:number state)})
         (dom/div nil
           (dom/button #js {:onClick #(send-number % state owner)
                            :disabled (not (:valid state))} "Done"))))))


(println "Mounting phone box")
;(om/root phone-box app-state {:target (. js/document (getElementById "phone"))})
