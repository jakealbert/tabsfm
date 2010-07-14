(ns tabsfm.templates
  (:use [tabsfm.lastfm]
	[tabsfm.util]
	[clj-time.core]
	[clj-time.format]
	[clj-time.coerce]
	[hiccup :only [html]]
	[hiccup.page-helpers :only [include-css doctype]]
	[hiccup.form-helpers])
  (:require [clojure.contrib.str-utils2 :as s2]))


;;;;;;;;;;;;;;;;;;;
;; MENU ITEMS
;;;;;;;;;;;;;;;;;;;

(def navigation-items
  (list
   (struct section "Tabs" "tabs")
   (struct section "Songbook" "songbook")
   (struct section "Radio" "radio")
   (struct section "Charts" "charts")
   (struct-map section
     :title "Admin"
     :body "admin"
     :auth-level :admin)))

(def footer-items
  (list
   (struct section "Home" nil)
   (struct section "User Agreement" "user-agreement")
   (struct section "Privacy Policy" "privacy-policy")
   (struct section "Contact Us" "contact")
   (struct section "Android/iPhone" "mobile")
   (struct section "GitHub" "http://github.com/brockrockman/tabsfm")))


;;;;;;;;;;;;;;;;;;;
;; TEMPLATES
;;;;;;;;;;;;;;;;;;;

(defn gen-head
  [title]
  (letfn [(when-ie [content]
		   (str "<!--[if IE]>"
			content
			"<![endif]-->"))]
    [:head
     [:script {:type "text/javascript"
	       :src "/js/jquery-1.3.2.min.js"}]
     [:script {:type "text/javascript"
	       :src "/js/jquery-ui-1.7.1.custom.min.js"}]
     [:script {:type "text/javascript"
	       :src "/js/selectToUISlider.jQuery.js"}]
     [:script {:type "text/javascript"
	       :src "/js/tabsfm.js"}]
     [:link {:type "text/css"
	     :href "/css/redmond/jquery-ui-1.7.1.custom.css"
	     :rel "stylesheet"}]
     [:link {:type "text/css"
	     :href "/css/ui.slider.extras.css"
	     :rel "stylesheet"}]       
     [:link {:type "text/css"
	     :href "/blueprint/screen.css"
	     :media "screen, projection"
	     :rel "stylesheet"}]
     [:link {:type "text/css"
	     :href "/blueprint/print.css"
	     :media "print"
	     :rel "stylesheet"}]
     (when-ie
      [:link {:type "text/css"
	      :href "/blueprint/ie.css"
	      :media "screen, projection"
	      :rel "stylesheet"}])
     [:link {:type "text/css"
	     :href "/stylesheet.css"
	     :media "screen"
	     :rel "stylesheet"}]
     [:link {:type "text/css"
	     :href "http://fonts.googleapis.com/css?family=Cardo"
	     :rel "stylesheet"}]
     [:link {:type "text/css"
	     :href "http://fonts.googleapis.com/css?family=Nobile"
	     :rel "stylesheet"}]
     [:title title]
     [:script {:type "text/javascript"}
      (str "
  var _gaq = _gaq || [];
  _gaq.push(['_setAccount', 'UA-17142498-1']);
  _gaq.push(['_trackPageview']);

  (function() {
    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
  })();")]]))
   


;;;;;;;;;;;;;;;;;;;
;; SECTIONS
;;;;;;;;;;;;;;;;;;;

(defn full-section
  [& content]
  [:div.span-22.prepend-1.append-1.append-top.append-bottom.last
   content])

(defn widget-section
  [session params widgets]
  (html
   [:div.tabsection.prepend-1.span-13.append-1.colborder
    (map (fn [widget] (html [:div.box [:h3 (:title widget)] ((:body widget) session params)]))
	 (filter (fn [widget] (and (not (= (:position widget) :right))
				   (in-section-for? (params "page")
						    (params "subpage")
						    (:section widget)
						    (:subsection widget))
				   (authorized-for? session widget)))
		 widgets))]
   [:div.tabsection.span-8.last
    (map (fn [widget] (html [:div.box [:h3 (:title widget)] ((:body widget) session params)]))
	 (filter (fn [widget] (and (= (:position widget) :right)
				   (in-section-for? (params "page")
						    (params "subpage")
						    (:section widget)
						    (:subsection widget))
				   (authorized-for? session widget)))
		 widgets))]))

(defn centered-section
  [title body]
  [:div.span-18.prepend-3.append-3.prepend-top.append-bottom.last
   [:div.box
    [:h2 title]
    [:hr]
    body]])
      
;;;;;;;;;;;;;;;;;;;
;; SUBSECTIONS
;;;;;;;;;;;;;;;;;;;

(defn full-subsection
  [& content]
  [:div.span-18.last
    content])

(defn widget-subsection
  [session params widgets]
  (html
   [:div.widgetsubsection.span-11.append-1.colborder
    (map (fn [widget] (html [:div.box.subsection [:h3 (:title widget)] ((:body widget) session params)]))
	 (filter (fn [widget] (and (not
				    (or 
				     (and (keyword? (:position widget))
					  (= (:position widget) :right))
				     (and (map? (:position widget))
					  (= (get (:position widget) (params "subpage") (val (first (:position widget)))) :right))))
				   (in-section-for? (params "page")
						    (params "subpage")
						    (:section widget)
						    (:subsection widget))
				   (authorized-for? session widget)))
		 widgets))]
   [:div.widgetsubsection.span-6.last
    (map (fn [widget] (html [:div.box.subsection [:h3 (:title widget)] ((:body widget) session params)]))
	 (filter (fn [widget] (and (or
				    (and (keyword? (:position widget))
					 (= (:position widget) :right))
				    (and (map? (:position widget))
					 (= (get (:position widget) (params "subpage") (val (first (:position widget)))) :right)))
				   (in-section-for? (params "page")
						    (params "subpage")
						    (:section widget)
						    (:subsection widget))
				   (authorized-for? session widget)))
		 widgets))]))

(defn tabbed-section
  [session params tabs section]
  [:div.span-22.prepend-top.append-bottom.prepend-1.append-1
   [:div.span-3
    [:ul.tabbed-section.box
     (for [tab tabs
	   :when (authorized-for? session tab)]
       (let [page     (params "page")
	     page-url (if (= page "tabs")
			"/"
			(str "/" page "/"))
	     subpage   (params "subpage")
	     subpage   (if (nil? subpage)
			 (:body (first tabs))
			 subpage)
	     link      [:a {:href (str page-url (:body tab))} (:title tab)]]
	 (if (= subpage (:body tab))
	       [:li.current link]
	       [:li link])))]]
   [:div.span-18.box.last.column.tabbed-section
    (let [subpage    (params "subpage")
	  subpage    (if (nil? subpage)
		       (:body (first tabs))
		       subpage)
	  subsections (:subsections section)
	  subsection (subsections subpage)
	  title      (if (:long-title subsection)
		       (:long-title subsection)
		       (:title subsection))
	  title      (if (fn? title)
		       (title session params)
		       title)
	  description (:description subsection)
	  description (if (fn? description)
			(description session params)
			description)
	  description (if (string? description)
			description
			"")]
      (full-subsection
       [:h2 title]
       [:div.subsection-description description]
       [:hr]
       ((:body subsection) session params)))]])




;;;;;;;;;;;;;;;;;;;
;; TRACKLISTS
;;;;;;;;;;;;;;;;;;;


(defn track-to-list-item
  [track]
  (let [actions #{"Songbooks"
		  "View on Last.fm" "Share"}
	track-url   (:url track)
	track-url  (s2/split track-url #"/_/")
	name-url   (second track-url)
	artist-url  (second (s2/split (first track-url) #"/music/"))
	album-info  (if (or (nil? (:album-mbid track))
			    (= "" (:album-mbid track)))
		      (album_get-info-by-artist (:artist track))
		      (album_get-info-by-mbid (:album-mbid track)))
	album-art-url (get-album-image-url album-info)
	album-art-url (if (or (nil? album-art-url)
			      (= "" album-art-url))
			"/images/no-art.png"
			album-art-url)
	album-art  [:img {:src album-art-url :alt (str (:artist track) " - " (:name track)) :width "64px" :height "64px"}]
	track-body [:div
		    [:div.album-art
		     [:a {:href (str "/artist/" artist-url "/" name-url)} album-art]]
		    [:h4
		     [:a {:href (str "/artist/" artist-url)}
		      (:artist track)]
		     " - "
		     [:a {:href (str "/artist/" artist-url "/" name-url)}
		      (:name track)]]
		    [:div.date (dt-time-ago (lastfm-date-to-dt (:date track)))]
		    [:div.track-actions
		     [:a.current  {:href "/"} (first actions)]
		     (for [action (rest actions)]
		       (html " | " [:a {:href "/"} action]))]
		    [:div.action-expand]]]
    (if (true? (:now-playing track))
      [:li.track.now-playing track-body]
      [:li.track track-body])))

(defn tracks-to-ol
  [tracks]
  [:ol.tracklist
   (for [track tracks]
     (track-to-list-item track))])

(defn tracks-to-ul
  [tracks]
  [:ul.tracklist
   (for [track tracks]
     (track-to-list-item track))])


;;;;;;;;;;;;;;;;;;;
;; LAYOUT
;;;;;;;;;;;;;;;;;;;

(defn layout [session params title content]
  (html
   [:html
    (doctype :xhtml-strict)
    (gen-head title)
    [:body
     [:div#preheader.container]
     [:div#header
      [:div.container
       [:div#logo.span-16.prepend-top
	[:h1 [:a {:href "/"} "Tabs.fm"]]]
       [:div#userbox.prepend-1.span-7.last.box
	   (let [user (session :lastfm-user)
		 logged-in (and (:username user)
				(:key user))]
	     (if logged-in
	       (html
		"Welcome, " 
		[:a {:href (str "/users/" (:username user))} (:username user)]
		". "
		[:a {:href "/logout"} "Logout"])
	       [:a {:href lastfm-auth-url} "Login to Last.fm"]))]
       [:div#navbar.span-24.last
	[:ul
	 (for [nav-item navigation-items
	       :when (authorized-for? session nav-item)]
	   (let [link [:a {:href (str "/" (:body nav-item))} (:title nav-item)]
		 page (params "page")]
	     (if (= page (:body nav-item))
	       [:li.current link]
	       [:li link])))]]]]
     [:div#content.container
      [:div.prepend-top.append-bottom
       content]]
     [:div#footer
      [:div.container
       [:div#footernav.span-24.last
	(let [first-link (first footer-items)]
	  [:a {:href (str "/" (:body first-link))} (:title first-link)])
	(for [footer-item (rest footer-items)]
	  (html
	   " | " [:a {:href (if (s2/contains? (:body footer-item) "://")
			      (:body footer-item)
			      (str "/" (:body footer-item)))}
		  (:title footer-item)]))]
       [:div#copyright.span-24.last
	"&copy; Tabs.fm 2010. All rights reserved.  A"
	[:a {:href "http://youbroughther.com"} "YBH"]
	" Production."]]]]]))
       


