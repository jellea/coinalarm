(ns coinalarm.alarms
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.reader :as reader]
            [goog.events :as events]
            [goog.dom :as gdom]
            [cljs.core.async :as async]
            [sablono.core :as html :refer-macros [html]]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true])
  (:import [goog.net XhrIo]
           goog.net.EventType
           [goog.events EventType]))

(defn get-y-pos [value]
  (/ (- 1200 value) 2))


(defn get-val-diff-from-y-diff [ypos]
  (* 2 ypos))


(defn start-listen [cb]
  (let [unlisten-key (events/listen js/window "mousemove" (fn [e] (cb e)))]
    (events/listenOnce js/window "mouseup"
       (fn [e] (events/unlistenByKey unlisten-key)))))

(defn set-starting-point [e owner]
  (om/set-state! owner :startX (.-offsetX e))
  (om/set-state! owner :startY (.-offsetY e)))

(defn get-starting-diff [e owner]
  (let [origX (om/get-state owner :startX)
        origY (om/get-state owner :startY)
        diffX (- origX (.-offsetX e))
        diffY (- origY (.-offsetY e))]
       ;; starting points haven't been set yet
       ;; use this event as starting point and return 0
       (if (.isNaN js/window diffX)
         (do (set-starting-point e owner)
             [0 0])
         [diffX diffY])))

;; TODO: set bounds
(defn update-alarm-value [e owner alarm channel]
  (let [[_ diff-y] (get-starting-diff e owner)
        val-diff (get-val-diff-from-y-diff diff-y)
        new-val (+ (:value alarm) val-diff)]
    (async/put! channel [new-val alarm])))
    ;;(println "new value" new-val val-diff diff-y)))


(defn render-alarm [alarm cursor owner state]
  (let [selected (= alarm (:selected state))
        channel (:channel state)]
    [:div {:class (str "alarm-cursor " (when selected "selected"))
           :data-selected selected
           :style {:-webkit-transform (str "translateY(" (get-y-pos (:value alarm)) "px)") }
           :key (:key alarm)
           :on-click (fn [e] (om/set-state! owner :selected alarm))
           :data-alarm (:value alarm)}

       [:div.alarm-flag {
              :on-mouse-down (fn [e]
                               ;; for some reason the click event does not get offsetY, etc
                               ;; so we can't use that to get the starting point
                               ;;(set-starting-point e owner)
                               ;; therefore i set it as state from the first event instead
                               (start-listen #(update-alarm-value % owner alarm channel)))
              } (str "$" (:value alarm))]

         (when selected
           [:div.alarm-message
              ;; TODO: get coin name
              [:img {:src "img/del.svg"}]
              [:input {:type "text" :placeholder (str "OMG [coin] reaches " (:value alarm))}]])]))

(defn render-popular [alarm cursor owner state]
  [:div.alarm-cursor.popular {:style {:-webkit-transform (str "translateY(" (get-y-pos (:value alarm)) "px)") }}
    [:div.alarm-flag (str "$" (:value alarm) " average buy/sell of CoinAlarm users")]])

(defn render-chart [cursor owner]
  (om/component
    (html [:svg#chart
            [:path {:d "M0.5,108.5 L107.5,31.5 L239.242187,138.828125 L362.554688,61.8867188 L401.175781,133.253906 L518.119141,0.15234375" :stroke "#FF0000" :fill "none"}]
            [:path.alarm-line {:d "M517,39 L0,39" :stroke "#FFFFFF" :strokeDasharray "3"}]
            [:path.alarm-line {:d "M517,239 L0,239" :stroke "#FFFFFF" :strokeDasharray "3"}]
            [:path.alarm-line {:d "M517,187 L0,187" :stroke "#C7C7C7" :strokeDasharray "3"}]
            [:path.alarm-line {:d "M517,140 L0,140" :stroke "#C7C7C7" :strokeDasharray "3"}]
           ])))

(defn alarms-selector [cursor owner]
    (reify
      om/IInitState
      (init-state [this]
                  {:channel (async/chan)
                   :selected nil
                   :alarms [{:value 929 :text "" :key 1}
                            {:value 530 :text "" :key 2}]
                   :popular [{:value 729 :text "" :key 1}
                            {:value 630 :text "" :key 2}]})
      om/IWillMount
      (will-mount [this]
           (go (while true
                 (let [[value current] (async/<! (om/get-state owner :channel))
                       alarms (->> (om/get-state owner :alarms)
                                   (remove #(= (:key current) (:key %))))
                       new-alarms (conj alarms (assoc current :value value))]
                                ;;(println "update" new-alarms)
                   (om/set-state! owner :alarms new-alarms)
                    ))))
      om/IRenderState
      (render-state [this state]
          (html [:div {:class (when (:selected state) "selected")}
                   [:h2 "When do you want to buy and sell?"]
                   (om/build render-chart cursor)
                   [:div {:class "alarm-box"}
                     (map #(render-alarm % cursor owner state) (:alarms state))
                     (map #(render-popular % cursor owner state) (:popular state))]
                   [:div.box-footer
                     [:a.button {:href "#"
                                 :onClick (fn [] (om/transact! cursor #(assoc % :page "phoneconfirm")))} "done"]]]))))

(def app-state (atom {:unused ""}))
