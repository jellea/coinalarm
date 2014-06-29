(ns coinalarm.phone
  (:require [sablono.core :as html :refer-macros [html]]
            [om.core :as om :include-macros true]))

(def app-state (atom {:number ""}))

(defn send-number [e state app owner]
  (when (:valid state)
    (let [phone-number (-> (om/get-node owner "phone-field")
                           .-value)]
      (println "sendint phone number" phone-number)
      (om/transact! app #(assoc % :page "marketselector")))))

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
      (html
        [:div
          [:p "Fill in your phone number to receive text messages"]
          ;; error message
          (when (:message state)
            [:div "Yo that's not a real phone number"])
          [:input {:placeholder "+47 999 999 000"
                   :ref "phone-field"
                   :onChange #(handle-number % state owner)
                   :value (:number state)}]
          [:div.box-footer
            [:a.button {:href "#"
                        :onClick #(send-number % state app owner)} "done"]]]))))
