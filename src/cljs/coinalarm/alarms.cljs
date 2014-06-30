(ns coinalarm.alarms
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.reader :as reader]
            [goog.dom :as gdom]
            [cljs.core.async :as async]
            [sablono.core :as html :refer-macros [html]]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(defn get-y-pos [value]
  (/ (- 1200 value) 2))

(defn get-val-diff-from-y-diff [ypos]
  (* 2 ypos))

(defn start-listen [cb]
  ;; using .addEventListener instead of google events,
  ;; since GE removes the almost universially (IE8) supported pageX

  ;; listen for updated position
  (let [cancel (fn mouseup[e]
                 (.removeEventListener js/window "mousemove" cb)
                 (.removeEventListener js/window "mouseup" mouseup))]
    (.addEventListener js/window "mousemove" cb false)
    ;; listen for mouseup to cancel updated position
    (.addEventListener js/window "mouseup" cancel false)))

(defn get-starting-diff [e origin]
  (let [diffX (- (:pageX origin) (.-pageX e))
        diffY (- (:pageY origin) (.-pageY e))]
        [diffX diffY]))

;; TODO: set bounds
(defn update-alarm-value [e origin alarm channel]
  (let [[_ diff-y] (get-starting-diff e origin)
        val-diff (get-val-diff-from-y-diff diff-y)
        new-val (+ (:value alarm) val-diff)]
    (async/put! channel [new-val alarm])))

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
                               ;; note that this event is a react synthetic event
                               ;; those are not exactly the same as the ones
                               ;; from google events or native events
                               ;; react events documented here:
                               ;; http://facebook.github.io/react/docs/events.html#mouse-events
                               ;; doc for google mouse event:
                               ;; http://docs.closure-library.googlecode.com/git/class_goog_events_BrowserEvent.html
                               (let [origin {:pageX (.-pageX e) :pageY (.-pageY e)}]
                                 (start-listen #(update-alarm-value % origin alarm channel))))
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
