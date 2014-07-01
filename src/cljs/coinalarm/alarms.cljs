(ns coinalarm.alarms
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.reader :as reader]
            [goog.dom :as gdom]
            [cljs.core.async :as async]
            [sablono.core :as html :refer-macros [html]]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))


;; about calculating y-positions from dollar values:
;; try to fit the range 1200 to 300 on 300px
;; 1200 becomes the top (0)
;; and 300 becomes the bottom (300px)
;; since the coordinates are starting on top left
;; we need to invert it
(defn get-y-pos [value]
  (/ (- 1200 value) 3))

(defn get-val-diff-from-y-diff [ypos]
  (* 3 ypos))

(defn start-listen [cb & [stop-cb]]
  ;; using .addEventListener instead of google events,
  ;; since GE removes the almost universially (except IE8) supported pageX

  ;; listen for updated position
  (let [cancel (fn mouseup[e]
                 (.removeEventListener js/window "mousemove" cb)
                 (.removeEventListener js/window "mouseup" mouseup)
                 (when stop-cb (stop-cb e)))]
    (.addEventListener js/window "mousemove" cb false)
    ;; listen for mouseup to cancel updated position
    (.addEventListener js/window "mouseup" cancel false)))

(defn get-starting-diff [e origin]
  (let [diffX (- (:pageX origin) (.-pageX e))
        diffY (- (:pageY origin) (.-pageY e))]
        [diffX diffY]))

(defn new-alarm-value [e origin start-val]
  (let [[_ diff-y] (get-starting-diff e origin)
        val-diff (get-val-diff-from-y-diff diff-y)]
    (+ start-val val-diff)))

(defn alarm-pointer [cursor owner]
  (reify
    om/IInitState
    (init-state [_]
                {:selected nil})
    om/IRenderState
    (render-state [_ state]
                  (let [alarm cursor
                        ;; this is tricky: since we're manipulating the cursor on drag,
                        ;; i needed to pick out the start-value of the alarm,
                        ;; we need to compare the diff from what the value was when first clicked
                        ;; understanding cursors is hard :(
                        start-val (:value alarm)
                        selected (:selected state)]
                    (html [:div {:class (str "alarm-cursor " (when selected "alarm-cursor-selected"))
                                 :data-selected selected
                                 :style {:zIndex (if selected 1000 2) ;; z-index adds 'px' to the value WTF
                                         :-webkit-transform (str "translateY(" (get-y-pos start-val) "px)")
                                         :transform (str "translateY(" (get-y-pos start-val) "px)")}
                                 :key (:key alarm)
                                 ;; dragging and 'selected' needs to work together
                                 ;; if you're dragging, selected can not be unset, that causes flickering
                                 :on-mouse-enter (fn [e] (om/set-state! owner :selected true))
                                 :on-mouse-leave (fn [e]
                                                   (when-not (om/get-state owner :dragging)
                                                     (om/set-state! owner :selected false)))
                                 :data-alarm (:value alarm)
                                 :on-mouse-down (fn [e]
                                    ;; note that this event is a react synthetic event
                                    ;; those are not exactly the same as the ones
                                    ;; from google events or native events
                                    ;; react events documented here:
                                    ;; http://facebook.github.io/react/docs/events.html#mouse-events
                                    ;; doc for google mouse event:
                                    ;; http://docs.closure-library.googlecode.com/git/class_goog_events_BrowserEvent.html
                                    (om/set-state! owner :dragging true)
                                    (let [origin {:pageX (.-pageX e) :pageY (.-pageY e)}]
                                      (start-listen (fn [e]
                                                      (let [new-val (new-alarm-value e origin start-val)]
                                                        (om/transact! cursor :value (fn [x] new-val))))
                                                     #(om/set-state! owner :dragging false))))}
                           [:div.alarm-flag (str "$" start-val)]
                           (when selected
                             [:div.alarm-message
                              ;; TODO: get coin name
                              [:img {:src "img/del.svg"
                                     :onClick (fn [e]
                                                (async/put! (:delete-chan state) @cursor))
                                     }]
                              [:input {:type "text" :placeholder (str "OMG " (:coin state) " reaches " (:value alarm))}]])])))))

(defn render-popular [alarm cursor owner state]
  [:div.alarm-cursor.popular {:style {:-webkit-transform (str "translateY(" (get-y-pos (:value alarm)) "px)") }}
  [:div.alarm-flag (str "$" (:value alarm) " average buy/sell of CoinAlarm users")]])

(defn horizontal-line [y-pos]
  (str "M517," y-pos " L0," y-pos))

(defn render-chart [cursor owner]
  (reify
    om/IRenderState
    (render-state [_ state]
      (let [alarm-positions   (->> state :alarms (map :value) (map get-y-pos)) ;; 39, 239
            popular-positions (->> state :popular (map :value) (map get-y-pos))];; 187, 140
      (html [:svg#chart
               [:path {:d "M0.5,108.5 L107.5,31.5 L239.242187,138.828125 L362.554688,61.8867188 L401.175781,133.253906 L518.119141,0.15234375" :stroke "#FF0000" :fill "none"}]
               (for [alarm alarm-positions]
                 [:path.alarm-line {:d (horizontal-line alarm) :stroke "#FFFFFF" :strokeDasharray "3"}])
               (for [p popular-positions]
                 [:path.alarm-line {:d (horizontal-line p) :stroke "#C7C7C7" :strokeDasharray "3"}])])))))

(defn alarms-selector [cursor owner]
    (reify
      om/IInitState
      (init-state [this]
                  {:delete-chan (async/chan)
                   :selected nil
                   :popular [{:value 729 :text "" :key 1}
                            {:value 630 :text "" :key 2}]})
      om/IWillMount
      (will-mount [this]
           (go (while true
                 (let [to-remove (async/<! (om/get-state owner :delete-chan))]
                   (om/transact! cursor :alarms
                                 (fn [alarms] (vec (remove #(= to-remove %) alarms))))))))
      om/IRenderState
      (render-state [this state]
          (html [:div
                   [:h2 "When do you want to buy and sell?"]
                   (om/build render-chart cursor {:state {:alarms (:alarms cursor)
                                                          :popular (:popular state)}})
                   [:div.alarm-box
                     (om/build-all alarm-pointer (:alarms cursor)  {:key :key
                                                                    :state {:delete-chan (:delete-chan state)
                                                                            :coin (-> cursor :coin (or "USD-BTC"))}})
                     (map #(render-popular % cursor owner state) (:popular state))]
                   [:div.box-footer
                     [:a.button {:href "#"
                                 :onClick (fn [] (om/transact! cursor #(assoc % :page "phoneconfirm")))} "done"]]]))))

(def app-state (atom {:unused ""}))
