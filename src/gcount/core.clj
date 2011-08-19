(ns gcount.core
  "generates charts out of the result from google search term(s)."
  (:use (incanter core charts))
  (:require [clj-http.client :as http-client]))

(def *search-provider* "http://www.google.com/search?hl=en&q=")
(def *search-pattern* #"About.*?([\d,]+).*?")

(defn search-for-term [term]
  (let [encoded-term (.replaceAll (apply str term) " " "+")
	qstring (str *search-provider* encoded-term)
	page (:body (http-client/get qstring))
	hits (second (re-find *search-pattern* page))
	nhits (Integer/parseInt (.replaceAll hits "," ""))]
    (hash-map term nhits)))

(defn view-bar-chart [mcoll]
  (view (bar-chart (keys mcoll) (vals mcoll)
	 :x-label "Search terms"
	 :y-label "Search results")))
	
(defn search-view-terms [terms]
  (->> (reduce merge (map search-for-term terms))
       (view-bar-chart)))