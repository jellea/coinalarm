(ns coinalarm.btc
  (:require [org.httpkit.client :as client]
            [clojure.data.json :as json]))


(def apis
  {:bitcoincharts "http://api.bitcoincharts.com/v1/markets.json"})

(def currency-markets
  {"CHF" ["ruxumCHF" "anxhkCHF" "localbtcCHF" "mtgoxCHF"]
   "HKD" ["btchkexHKD" "ruxumHKD" "anxhkHKD" "localbtcHKD" "mtgoxHKD"]
   "EUR" ["intrsngEUR" "hitbtcEUR" "btceurEUR" "zyadoEUR"
          "itbitEUR" "ruxumEUR" "btcexEUR" "anxhkEUR" "b7EUR"
          "fbtcEUR" "globalEUR" "imcexEUR" "btc24EUR" "btceEUR"
          "justEUR" "bcEUR" "rockEUR" "bitcurexEUR" "mtgoxEUR" "crytrEUR"
          "rippleEUR" "krakenEUR" "bitmarketEUR" "aqoinEUR" "thEUR" "vcxEUR"
          "bitaloEUR" "localbtcEUR" "btcdeEUR"]
   "ARS" ["localbtcARS"]
   "MXN" ["localbtcMXN"]
   "DKK" ["localbtcDKK" "mtgoxDKK"]
   "NMC" ["krakenNMC"]
   "USD" ["bitkonanUSD" "justUSD" "cotrUSD" "hitbtcUSD" "crytrUSD" "localbtcUSD" "rippleUSD"
          "itbitUSD" "1coinUSD" "bitbayUSD" "bitboxUSD" "b7USD" "bitmeUSD" "cryptoxUSD" "bitfloorUSD"
          "btc24USD" "btcexUSD" "btcexWMZ" "btctreeUSD" "anxhkUSD" "thUSD" "bitstampUSD" "bcmBMUSD"
          "bcmLRUSD" "bcmMBUSD" "b2cUSD" "exchbUSD" "fbtcUSD" "globalUSD" "imcexUSD" "ruxumUSD" "thLRUSD"
          "cbxUSD" "krakenUSD" "bitfinexUSD" "lakeUSD" "rockUSD" "vcxUSD" "bitaloUSD" "mtgoxUSD"
          "bcmPPUSD" "lybitUSD" "btceUSD" "bitmarketUSD" "intrsngUSD" "weexUSD"]
   "CAD" ["lybitCAD" "mtgoxCAD" "anxhkCAD" "virtexCAD" "weexCAD" "localbtcCAD"]
   "CLP" ["thCLP"], "NOK" ["localbtcNOK" "justNOK"]
   "ZAR" ["bitxZAR" "ruxumZAR" "localbtcZAR"]
   "INR" ["localbtcINR" "thINR"]
   "THB" ["ruxumTHB" "localbtcTHB" "mtgoxTHB"]
   "CNY" ["btcnCNY" "okcoinCNY" "mtgoxCNY" "rmbtbCNY" "anxhkCNY"]
   "SGD" ["anxhkSGD" "itbitSGD" "ruxumSGD" "fybsgSGD" "localbtcSGD" "mtgoxSGD"]
   "AUD" ["cryptoxAUD" "ruxumAUD" "anxhkAUD" "mtgoxAUD" "btcmarketsAUD" "thAUD" "weexAUD" "bitmarketAUD" "localbtcAUD" "wbxAUD"]
   "ILS" ["bit2cILS" "localbtcILS"], "KRW" ["krakenKRW" "korbitKRW"]
   "RON" ["btcxchangeRON"]
   "PLN" ["mtgoxPLN" "bitbayPLN" "bidxtrmPLN" "localbtcPLN" "bitomatPLN" "freshPLN" "globalPLN" "ruxumPLN" "intrsngPLN" "bitaloPLN" "bitmarketplPLN" "bitchangePLN" "bitmarketPLN" "bitcurexPLN"]
   "SLL" ["rockSLL" "virwoxSLL"]
   "JPY" ["btcexJPY" "anxhkJPY" "ruxumJPY" "mtgoxJPY"]
   "CZK" ["localbtcCZK" "bitcashCZK" "bitstockCZK"]
   "GBP" ["ruxumGBP" "britcoinGBP" "ibwtGBP" "coinfloorGBP" "anxhkGBP" "globalGBP"
          "localbtcGBP" "bitmarketGBP" "bitaloGBP" "bit121GBP" "bcGBP" "mtgoxGBP" "intrsngGBP"]
   "XRP" ["krakenXRP" "justXRP" "rippleXRP" "snwcnXRP"]
   "IDR" ["btcoidIDR"]
   "NZD" ["anxhkNZD" "mtgoxNZD" "bitnzNZD" "localbtcNZD"]
   "SEK" ["localbtcSEK" "ruxumSEK" "fybseSEK" "kptnSEK" "mtgoxSEK"]
   "HUF" ["ruxumHUF"], "GAU" ["bcmPXGAU" "bcmBMGAU"]
   "LTC" ["bitmeLTC" "justLTC" "krakenLTC"]
   "BRL" ["bbmBRL" "mrcdBRL" "localbtcBRL"]
   "UAH" ["ruxumUAH"]
   "RUB" ["btcexRUB" "btcexWMR" "btcexYAD" "localbtcRUB" "ruxumRUB" "btceRUR" "bitmarketRUB" "mtgoxRUB"]})

(defn get-latest-data [{:keys [api query-params callback]}]
  (let [url (or (api apis) (:bitcoincharts apis))]
    (client/get url {:query-params query-params}
                callback)))

(defn json->dict [data]
  (json/read-str data :key-fn keyword))

(defn restructure-dict [data]
  (into {} (map (fn [[f s]] (vector f (group-by :symbol s)))
                (group-by :currency (json->dict (:body (deref (client/get (:bitcoincharts apis)))))))))

(defn get-latest-symbols [callback]
  (get-latest-data {:api :bitcoincharts
                    :callback (fn [{:keys [status headers body error]}]
                                ((comp callback restructure-dict json->dict) body))}))

(deref (get-latest-symbols print))



