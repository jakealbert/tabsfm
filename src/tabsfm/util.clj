(ns tabsfm.util
  (:use [clj-time.core]
	[clj-time.format]))

(defstruct section :title :body)

(defn xor
  [a b]
  "Logical Two-Input Exclusive-OR"
  (or (and a (not b))
      (and b (not a))))


;;;;;;;;;;;;;;;
;; PREDICATES
;;;;;;;;;;;;;;;

(defn authorized-for?
  [session section]
  "Is the user of the current session's auth-level
    above the auth-level in the section map?"
  (let [user (session :lastfm-user)
	user-auth-level (session :auth-level)
	section-auth-level (section :auth-level)]
    (cond
     (nil? user-auth-level)      (nil? section-auth-level)
     (= user-auth-level :user)   (or (nil? section-auth-level)
				     (= section-auth-level :user))
     (= user-auth-level :admin)  (or (nil? section-auth-level)
				     (= section-auth-level :user)
				     (= section-auth-level :admin)))))

(defn in-section-for?
  [page subpage section subsection]
  "Returns whether the :section and :subsection of a widget/map contain
   the current page and subpage (http://tabs.fm/page/subpage)" 
   (or (nil? section)
       (and
	(nil? page)
	(or 
	 (and
	  (set? section)
	  (section "tabs"))
	 (and
	  (string? section)
	  (= section "tabs"))))
       (and
	(or
	 (and
	  (set? section)
	  (section page))
	 (and
	  (string? section)
	  (= section page)))
	(or
	 (nil? subsection)
	 (and (nil? subpage) 
	      (= page "charts")
	      (subsection "overview"))
	      
	       
	 (and
	  (set? subsection)
	  (subsection subpage))
	 (and
	  (string? subsection)
	  (= subsection subpage))))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; DATE SELECTION/CALENDAR FUNCTIONS
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn get-calendar-select
  [alod selected-date] 
  (if (empty? alod)
    '()
    (cons [:optgroup {:label (str (year (first alod)))}
	   (map (fn [dt] 
		  (let [option-attrs {:value (unparse
					      (formatter
					       "dd-MM-YYYY")
					      dt)}
			option-attrs (if (= (unparse (formatter "dd-MM-YYYY")
						     dt)
					    selected-date)
				       (assoc option-attrs :selected "selected")
				       option-attrs)]
		    
		    [:option option-attrs
		     (unparse (formatter "EEEE dd MMMM YYYY") dt)]))
		
		
		(filter (fn [dt]
			  (= (year dt) (year (first alod)))) alod))]
	  (get-calendar-select (filter (fn [dt]
				(not (= (year dt) (year (first alod))))) alod)
		      selected-date))))

(defn get-calendar-dates
  ([now-dt] (get-calendar-dates (minus now-dt (days (day-of-week now-dt))) '()))
  ([enddate acc]
     (let [startdate (date-time 2005 2 20)]
       (if (after? enddate startdate)
	 (get-calendar-dates
	  (minus enddate (weeks 1))
	  (cons enddate acc))
	 acc))))

(defn get-calendar-html
  [name selected-date]
  (vec
    (concat
     [:select {:name name
	       :id name}]
    (get-calendar-select (get-calendar-dates (now)) selected-date))))