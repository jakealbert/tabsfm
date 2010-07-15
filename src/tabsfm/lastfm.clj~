(ns tabsfm.lastfm
  (:use compojure.core)
  (:use [hiccup :only [html]])
  (:use [clojure-http.client])
  (:import (java.io Reader InputStream InputStreamReader ByteArrayInputStream IOException))
  (:import (java.security NoSuchAlgorithmException MessageDigest)
	   (java.math BigInteger))
  (:require [clojure.xml :as xml])
  (:require [clojure.zip :as zip])
  (:require [clojure.contrib.zip-filter.xml :as zf])
  (:require [clojure-http.resourcefully :as res]))


(def apikey  "95bbfb880f3495a0f13f4d29b524d4ef")
(def secret  "202ed3f4a070b39a4595db7e6e9758fb")
(def user "ob1cannoli")
(def pass "bass1027")
(def rooturl "http://ws.audioscrobbler.com/2.0/")
(def default-param-map (str "http://ws.audioscrobbler.com/2.0/?api_key=" apikey))
(def lastfm-auth-url (str "http://www.last.fm/api/auth/?api_key=" apikey))

(defn lastfm-getmethod
  [param-map]
  (let [param-map-url (apply str
			     (cons default-param-map
				   (map (fn [a b]
					  (str "&" a "=" b))
					(keys param-map)
					(vals param-map))))
	response (try (res/get param-map-url)
		      (catch IOException e
			nil))]
    (if (nil? response)
      nil
      (let
	  [xmlstr (apply str (:body-seq response))
	   input-stream (ByteArrayInputStream. (.getBytes xmlstr))
	   parsedxml (xml/parse input-stream)]
	parsedxml))))
	

(defn user_get-weekly-track-chart
  [username]
   (lastfm-getmethod
    {"method" "user.getweeklytrackchart"
     "user" username}))

(defn user_get-weekly-top-10
  [username]
  (for [x (xml-seq (user_get-weekly-track-chart username))
	:when (and (:rank (:attrs x))
		   (< (Integer/parseInt (:rank (:attrs x))) 11))]
    (let
	[track  (:content x)
	 artist (first (for [y track
			     :when (= (:tag y) :artist)]
			 (first (:content y))))
         name   (first (for [y track
			     :when (= (:tag y) :name)]
			 (first (:content y))))
         url    (first (for [y track
			     :when (= (:tag y) :url)]
			 (first (:content y))))]
      {:artist artist :name name :url url})))
    
(defn pad [n s]
  (let [padding (- n (count s))]
    (apply str (concat (apply str (repeat padding "0")) s))))

(defn md5-sum
  [#^String str]
  (let [alg (doto (MessageDigest/getInstance "MD5")
	      (.reset)
	      (.update (.getBytes str)))]
    (try
     (pad 32 (.toString (new BigInteger 1 (.digest alg)) 16))
     (catch NoSuchAlgorithmException e
       (throw (new RuntimeException e))))))


(defn auth_get-session
  [params]
  (let [token (params "token")
	api-unsigned (str "api_key" apikey "methodauth.getSessiontoken" token secret)
	api-sig (md5-sum api-unsigned)
	response (lastfm-getmethod {"method" "auth.getSession"
				    "token" token
				    "api_sig" api-sig})
	username   (first (for [x (xml-seq response)
				:when (= (:tag x) :name)]
			    (first (:content x))))
	key        (first (for [x (xml-seq response)
				:when (= (:tag x) :key)]
			    (first (:content x))))
	subscriber (first (for [x (xml-seq response)
				:when (= (:tag x) :subscriber)]
			    (first (:content x))))]
    {:username username :key key :subscriber subscriber}))
	

	