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

(defn phone-box [app owner]
  (reify
    om/IInitState
    (init-state [this]
      {:number ""})
    om/IRenderState
    (render-state [this state]
      (dom/div nil "phone box"))))


(println "Mounting phone box")
(om/root phone-box app-state {:target (. js/document (getElementById "phone"))})
