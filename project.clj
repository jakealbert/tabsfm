1(defproject tabsfm "0.1.0-SNAPSHOT"
  :description "Social Tabs"
  :repositories {"incanter" "http://repo.incanter.org"}
  :dependencies [[compojure "0.4.0-RC3"]
                 [org.clojars.abedra/hiccup "0.2.5"]
		 [cssgen "0.2.1"]
		 [org.clojure/clojure-contrib "1.0-SNAPSHOT"]
		 [org.incanter/incanter-full "1.0.0"]
                 [ring/ring-servlet "0.2.1"]
		 [ring/ring-jetty-adapter "0.2.0"]
                 [com.google.appengine/appengine-tools-sdk "1.3.0"]
		 [appengine-api-1.0-sdk "1.3.3.1"]
		 [appengine-api-labs "1.3.3.1"]
		 [appengine-api-stubs "1.3.3.1"]
		 [appengine-local-runtime "1.3.3.1"]
		 [appengine-testing "1.3.3.1"]
		 [appengine-tools-api "1.3.3.1"]
		 [org.clojars.brockrockman/lastfm-bindings "1.0.0"]
		 [clj-time "0.1.0-SNAPSHOT"]
		 [org.clojars.sethtrain/postal "0.2.0"]
		 [clojure-http-client "1.1.0-SNAPSHOT"]]
  :dev-dependencies [[swank-clojure "1.2.1"]]
  :namespaces [tabsfm]
  :compile-path "war/WEB-INF/classes"
  :library-path "war/WEB-INF/lib")