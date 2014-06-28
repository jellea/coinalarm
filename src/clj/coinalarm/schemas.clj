(ns coinalarm.schemas
  (:require [schema.core :as s]))

(def Subscription
  {:upper s/Bool
   :price s/Num
   :currency s/Str
   :symbol s/Str})

(def User
  {:phone s/Num
   (s/optional-key :subscriptions) [Subscription]})
