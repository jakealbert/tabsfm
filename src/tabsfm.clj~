(ns tabsfm
  (:gen-class :extends javax.servlet.http.HttpServlet)
  (:use [tabsfm.response :only [success not-found]]
	[tabsfm.templates :only [layout]]
	[tabsfm.content :only [sections]]
	[tabsfm.util :only [section]]
	[tabsfm.lastfm]
	[hiccup :only [html]]
	compojure.core
	ring.middleware.session
	[ring.util.response :only [redirect]]
	[ring.util.servlet :only [defservice]]))

(defn index
  [session params]
  (let
      [page (params "page")
       section (sections page)
       subpage (params "subpage")
       subsections (:subsections section)
       subsection  (if (nil? subsections)
		     nil
		     (subsections subpage))
       is-bummer  (or (nil? section)
		      (and (nil? subsection)
			   (not (nil? subpage))))
       fixed-section (if is-bummer
		       (sections "bummer")
		       section)
       status  (if is-bummer
		 success
		 not-found)
       section-title  (:title fixed-section)
       subsection-title (:title subsection)
       site-title     "Tabs.fm"
       title          (if (or (nil? section-title)
			      (= "" section-title))
			site-title
			(str section-title " - " site-title))
       title          (if (nil? subsection-title)
			title
			(str subsection-title " - " title))
       section-layout ((:body fixed-section) session params)]
    (status
     (layout
      session
      params
      title
      section-layout)
     session)))


(defn authorize
  [session params]
  (let [auth-response (auth_get-session params)]
    (if (or (nil? (:username auth-response))
	    (nil? (:key auth-response)))
      (redirect "/auth-error")
      (let [auth-session (assoc session :lastfm-user auth-response
				        :auth-level  (if (and (= (:username auth-response) "ob1cannoli")
							      (params "admin"))
						       :admin
						       :user))]
	(index auth-session (assoc params "page" "tabs"))))))


(defroutes admin-routes
  (GET "/admin/*" {session :session params :params}
       (index session params)))

(defroutes public-routes
  (GET "/" {session :session params :params}
       (index session (assoc params "page" "tabs")))
  (GET "/:page" {session :session params :params}
       (index session params))
  (GET "/:page/:subpage" {session :session params :params}
       (index session params)))

(defroutes auth-routes
  (GET "/logout" {session :session params :params}
       (index (dissoc session :lastfm-user) (assoc params "page" "logout")))
  (GET "/login/*" {session :session params :params}
       (authorize session params)))

(defroutes app
  auth-routes
  public-routes
  admin-routes
  (GET "/*" {session :session params :params}
       (index session (assoc params "page" "bummer"))))

(wrap! app
       :session)

(defservice app)

