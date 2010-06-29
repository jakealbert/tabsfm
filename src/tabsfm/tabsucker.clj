(ns tabsfm.tabsucker
  (:use compojure.core)
  (:use [hiccup :only [html]])
  (:import (java.io Reader InputStream InputStreamReader ByteArrayInputStream IOException))
  (:require [clojure.xml :as xml])
  (:require [clojure.zip :as zip])
  (:require [clojure.contrib.zip-filter.xml :as zf])
  (:require [clojure-http.resourcefully :as res]))

 
