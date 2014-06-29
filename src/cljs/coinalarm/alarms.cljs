(ns coinalarm.alarms
  (:require [cljs.reader :as reader]
            [goog.events :as events]
            [goog.dom :as gdom]
            [sablono.core :as html :refer-macros [html]]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true])
  (:import [goog.net XhrIo]
           goog.net.EventType
           [goog.events EventType]))

(enable-console-print!)


(defn get-y-pos [value]
  (/ (- 1200 value) 2))


;; TODO: drag/drop of alarms
(defn render-alarm [alarm cursor owner state]
  (let [selected (= alarm (:selected state))]
    [:div {:class (str "alarm-cursor " (when selected "selected"))
           :data-selected selected
           :style {:-webkit-transform (str "translateY(" (get-y-pos (:value alarm)) "px)") }
           :key (:key alarm)
           :on-click (fn [e] (om/set-state! owner :selected alarm))
           :data-alarm (:value alarm)}
       [:div {:class "alarm-flag"
              :on-mousemove (fn [e]  )
              :on-mousedown (fn [e] )
              :on-mouseup   (fn [e] )
              } (:value alarm)]
       (when selected
         [:div {:class "alarm-message"}
            [:p "Message"]
            ;; TODO: get coin name
            [:textarea {:placeholder (str "OMG [coin] reaches " (:value alarm))}]
            [:a "Delete alarm"]])]))

(defn alarms-selector [cursor owner]
    (reify
      om/IInitState
      (init-state [this]
                  {:selected nil
                   :alarms [{:value 1200 :text "" :key 1}
                            {:value 400 :text "" :key 2}]})
      om/IRenderState
      (render-state [this state]
          (println (:selected state))
          (html [:div
                   [:h1 "Set your alarms"]
                   [:div {:class "alarm-box"}
                     (map #(render-alarm % cursor owner state) (:alarms state))]]))))

(def app-state (atom {:unused ""}))
(print "rendering alarms-selector")
(om/root alarms-selector app-state {:target (. js/document (getElementById "app"))})
