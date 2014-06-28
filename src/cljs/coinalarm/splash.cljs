(ns coinalarm.splash
  (:require
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true])
  (:import [goog.net XhrIo]
           goog.net.EventType
           [goog.events EventType]))
