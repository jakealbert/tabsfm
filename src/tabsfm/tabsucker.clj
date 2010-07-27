(ns tabsfm.tabsucker
  (:require [clojure.contrib.str-utils2 :as s2])
  (:require [clojure-http.resourcefully :as res]))

 
(defn get-tabs-by-artist
  [artist]
  (let [artist-911tabs-url (s2/lower-case (s2/replace artist #" " "_"))
	first-letter (s2/lower-case (s2/take artist 1))
	response (try
		  (res/get (str "http://www.911tabs.com/tabs/" 
				first-letter
				"/"
				artist-911tabs-url
				"/"))
		  (catch IOException e
		    nil))]
   (if (nil? response)
     #{}
     (let [bodyseq (:body-seq response)
	   bodystr (apply str bodyseq)
	   splitone (s2/split bodystr #"video lessons")
	   splittwo (s2/split (nth splitone 2) #"<td colspan=2 height=80 align=center>")
	   splittr (s2/split (first splittwo) #"(<tr class=tr>)|(<tr class=\"tr1\">)")
	   splittr (drop 1 (drop-last 1 splittr))]
       (map (fn [tr]
	      (let [tds (first (s2/split tr #"</tr>"))
		    tds (s2/butlast (s2/drop (s2/replace tds #"\t" "") 4) 5)
		    [title & tabs] (s2/split tds #"</td><td align=\"center\">")
		    [guitar bass drum piano power pro] (map (fn [tab]
							      (if (or (nil? tab)
								      (= "" tab))
								nil
								tab)) tabs)]
	        [title guitar bass drum piano power pro]))
	    splittr)))))