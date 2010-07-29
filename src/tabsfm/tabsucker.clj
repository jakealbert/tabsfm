(ns tabsfm.tabsucker
  (:import (java.io Reader InputStream InputStreamReader ByteArrayInputStream IOException))
  (:require [clojure.contrib.str-utils2 :as s2])
  (:require [clojure-http.resourcefully :as res]))

 
(defn get-tabs-by-artist
  [artist]
  (let [artist-911tabs-url (s2/lower-case (s2/replace artist #" " "_"))
	first-letter (s2/lower-case (s2/take artist 1))]
    (letfn [(response-process [response]
				(if (nil? response)
				  #{}
				  (let [bodyseq (:body-seq response)
					bodystr (apply str bodyseq)
					splitone (s2/split bodystr #"video lessons")
					splittwo (s2/split (nth splitone 2) #"<td colspan=2 height=80 align=center>")
					splittr (s2/split (first splittwo) #"(<tr class=tr>)|(<tr class=\"tr1\">)")
					splittr (drop 1 (drop-last 0 splittr))]
				    (map (fn [tr]
					   (let [tds (first (s2/split tr #"</tr>"))
						 tds (s2/butlast (s2/drop (s2/replace tds #"\t" "") 4) 5)
						 [title & tabs] (s2/split tds #"</td><td align=\"center\">")
						 [title-anchor versions-link title] (re-find
										     #"<a href=\"(.*)\"  title=\".*\"  >(.*) tab</a>"
										     title)
						 [guitar bass drum piano power pro] (map (fn [tab]
											   (if (or (nil? tab)
												   (= "" tab))
											     nil
											     (let [[tab-anchor tab-link tab-count foo] (re-find #"<a href=\"(.*)\" class=\"ta\"  title=\".*\">(.*) (tab|tabs)</a>" tab)
												   tab-count (Integer/parseInt tab-count)]
											       {:link tab-link :count tab-count}))) tabs)
						 newmap {:title title
							 :versions {:link versions-link
								    :count (reduce + 0 (filter (fn [x] x) (map :count (list guitar bass drum piano power pro))))}
							 :guitar guitar
							 :bass bass
							 :drum drum
							 :piano piano
							 :power power
							 :pro pro}]
					     newmap))
					 splittr))))
	    (response [n acc]
		      (let [response-try (try
					  (res/get (str "http://www.911tabs.com/tabs/" 
							first-letter
							"/"
							artist-911tabs-url
							"/index" n ".html"))
					  (catch IOException e
					    nil))
			    processed (response-process response-try)]
			(if (empty? processed)
			  acc
			  (response (inc n) (concat acc processed)))))]
      (response 1 #{}))))
