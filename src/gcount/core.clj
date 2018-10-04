(ns gcount.core
  "Generates charts out of the result from google search term(s)."
  (:use (incanter core charts))
  (:require [clj-http.client :as http-client]))

(def google "http://www.google.com/search?hl=en&q=")
(def result #"About.*?([\d,]+).*?")

(defn search-for-term [term & [provider search-result-pattern]]
  (let [encoded-term (-> (apply str term)
                         (.replaceAll " " "+"))
        qstring (str (or provider google) encoded-term)
        page (-> (http-client/get qstring) :body)
        hits (-> (or search-result-pattern
                     result)
                 (re-find page)
                 second)
        nhits (-> (.replaceAll hits "," "")
                  Integer/parseInt)]
    (hash-map term nhits)))

(defn view-bar-chart [mcoll]
  (-> (bar-chart (keys mcoll)
                 (vals mcoll)
                 :x-label "Search terms"
                 :y-label "Search results")
      view))

(defn save-bar-chart [file-path mcoll]
  (-> (bar-chart (keys mcoll)
                 (vals mcoll)
                 :x-label "Search terms"
                 :y-label "Search results")
      (save file-path)))

(defn search-view-terms [terms]
  (->> (map search-for-term terms)
       (reduce merge)
       (view-bar-chart)))

(defn save-search-view-terms [terms file-path]
  (->> (map search-for-term terms)
       (reduce merge)
       (save-bar-chart file-path)))
