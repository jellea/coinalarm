(ns coinalarm.btc
  (:require [org.httpkit.client :as client]
            [clojure.data.json :as json]))


(def apis
  {:bitcoincharts "http://api.bitcoincharts.com/v1/markets.json"})

(def symbols ["localbtcUSD" "bitxZAR" "itbitEUR" "anxhkCNY"
              "localbtcCAD" "btcexWMZ" "ruxumTHB" "krakenLTC"
              "bcEUR" "okcoinCNY" "rockUSD" "bitmeUSD" "btcexUSD"
              "anxhkSGD" "anxhkAUD" "wbxAUD" "ruxumSGD" "globalUSD"
              "ruxumAUD" "bcmBMGAU" "localbtcNOK" "freshPLN" "itbitUSD"
              "hitbtcEUR" "1coinUSD" "weexCAD" "anxhkJPY" "localbtcZAR"
              "weexUSD" "mtgoxTHB" "ruxumPLN" "bitbayPLN" "bcmMBUSD" "mtgoxCNY"
              "rippleXRP" "ruxumJPY" "lybitUSD" "lybitCAD" "mtgoxAUD"
              "bitfloorUSD" "mtgoxSGD" "bitmarketAUD" "bcmPXGAU" "localbtcINR"
              "anxhkGBP" "hitbtcUSD" "bitboxUSD" "krakenEUR" "localbtcTHB"
              "ruxumGBP" "britcoinGBP" "intrsngPLN" "bitmarketPLN" "mtgoxPLN"
              "justXRP" "btcmarketsAUD" "anxhkNZD" "mtgoxJPY" "localbtcAUD"
              "localbtcILS" "localbtcSGD" "krakenNMC" "bitaloEUR" "bitstockCZK"
              "thLRUSD" "mtgoxGBP" "localbtcPLN" "ruxumSEK" "ruxumHUF" "bitcurexPLN"
              "intrsngGBP" "bitmarketGBP" "krakenUSD" "bitmarketplPLN" "fybsgSGD"
              "b7EUR" "cbxUSD" "btceEUR" "itbitSGD" "localbtcCZK" "thEUR" "rockSLL"
              "ruxumUAH" "snwcnXRP" "btc24EUR" "bcmBMUSD" "bitaloUSD" "mtgoxNZD"
              "localbtcGBP" "cryptoxUSD" "btcexJPY" "bbmBRL" "weexAUD" "globalPLN"
              "justLTC" "btcdeEUR" "mtgoxSEK" "bitstampUSD" "imcexEUR" "korbitKRW"
              "b7USD" "btceurEUR" "btcnCNY" "globalGBP" "btceUSD" "virwoxSLL"
              "rippleEUR" "bit121GBP" "thUSD" "bit2cILS" "localbtcNZD" "ruxumRUB"
              "btc24USD" "anxhkHKD" "anxhkCHF" "thCLP" "b2cUSD" "localbtcSEK"
              "ruxumHKD" "bitcashCZK" "bcmLRUSD" "ruxumCHF" "anxhkEUR" "bcGBP"
              "imcexUSD" "bitomatPLN" "zyadoEUR" "bitchangePLN" "ruxumEUR"
              "bitfinexUSD" "mtgoxRUB" "rippleUSD" "justEUR" "localbtcBRL"
              "krakenKRW" "bitmeLTC" "bitmarketRUB" "mrcdBRL" "lakeUSD"
              "fbtcEUR" "btctreeUSD" "vcxEUR" "fybseSEK" "anxhkCAD"
              "btcexYAD" "mtgoxHKD" "mtgoxCHF" "anxhkUSD" "btcexWMR" "thINR"
              "intrsngEUR" "bitmarketEUR" "localbtcRUB" "ruxumUSD" "bitbayUSD" "rmbtbCNY"
              "cryptoxAUD" "mtgoxEUR" "coinfloorGBP" "justUSD" "kptnSEK" "bcmPPUSD"
              "crytrEUR" "btchkexHKD" "btcxchangeRON" "bitaloPLN" "btcexRUB" "fbtcUSD"
              "btceRUR" "vcxUSD" "mtgoxDKK" "localbtcHKD" "localbtcCHF" "localbtcEUR"
              "bitkonanUSD" "cotrUSD" "localbtcARS" "bitcurexEUR" "exchbUSD" "krakenXRP"
              "thAUD" "rockEUR" "intrsngUSD" "bitmarketUSD" "mtgoxCAD" "localbtcMXN" "mtgoxUSD"
              "btcexEUR" "bidxtrmPLN" "bitaloGBP" "btcoidIDR" "justNOK" "crytrUSD" "ruxumZAR"
              "virtexCAD" "aqoinEUR" "localbtcDKK" "ibwtGBP" "bitnzNZD" "globalEUR"])

(defn get-latest-data [{:keys [api query-params callback]}]
  (let [url (or (api apis) (:bitcoincharts apis))]
    (client/get url {:query-params query-params}
                callback)))

(defn json->dict [data]
  (json/read-str data :key-fn keyword))

(defn symbol-as-keys [data]
  (into {} (map #(vector (:symbol %) %) data)))

(defn get-latest-symbols [callback]
  (get-latest-data {:api :bitcoincharts
                    :callback (fn [{:keys [status headers body error]}]
                                ((comp callback symbol-as-keys json->dict) body))}))

;; (get-latest-symbols print)



