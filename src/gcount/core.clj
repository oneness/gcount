(ns gcount.core
  "Generates charts out of the result from google search term(s)."
  (:use (incanter core charts))
  (:require [etaoin.api :as e]))

(def google "https://www.google.com/search?q=")
(def result #"About.*?([\d,]+).*?")

(defn search-for-term [term & [driver provider search-result-pattern]]
  (when term
    (try (let [encoded-term (-> (apply str term)
                                (.replaceAll " " "+"))
               qstring (str (or provider google) encoded-term)
               driver (e/firefox)
               _  (e/go driver qstring)
               _  (e/wait-visible driver {:id "result-stats"})
               page (e/get-element-text driver {:id "result-stats"})
               hits (-> (or search-result-pattern
                            result)
                        (re-find page)
                        second)
               nhits (-> (.replaceAll hits "," "")
                         Integer/parseInt)]
           (e/quit driver)
           (hash-map term nhits))
         (catch Exception e
           (println (reduce str (repeat 80 "*")))
           (println "Failed to get result for " term)
           (println "The result stats needs to be visible for this to work.")
           (println "Click on the Tools menu on the google search page to show it")
           (println (reduce str (repeat 80 "*")))
           (throw e)))))

(defn as-of-now-str []
  (as-> (bean (java.util.Date.)) date
    (format "%s-%02d-%02d"
            (+ 1900 (:year date))
            (inc (:month date))
            (:date date))))

(defn view-bar-chart [mcoll]
  (-> (bar-chart (keys mcoll)
                 (vals mcoll)
                 :x-label (format "Search terms - as of %s"
                                  (as-of-now-str))
                 :y-label "Search results")
      view))

(defn save-bar-chart [file-path mcoll]
  (-> (bar-chart (keys mcoll)
                 (vals mcoll)
                 :x-label (format "Search terms - as of %s"
                                  (as-of-now-str))
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
