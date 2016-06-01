(ns web_app.web-handler
  (:require [web_app.views :as views]
            [compojure.core :as core]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.adapter.jetty :as jetty])
  (:gen-class))

(core/defroutes app-routes
  (core/GET "/"
       []
       (views/home-page))
  (core/POST "/classify"
       {params :params}
       (views/results-page params))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))

(defn -main
  [& [port]]
  (let [port (Integer. (or port
                           (System/getenv "PORT")
                           5002))]
    (jetty/run-jetty #'app {:port  port
                            :join? false})))
