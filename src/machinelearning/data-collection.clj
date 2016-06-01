(ns machinelearning.data-collection
  (:require [clj-http.client :as client]
            [clojure.data.json :as json])
  (:use [clojure.java.io :as io]))

(def api-url
  "https://api.stackexchange.com/2.2")

(def access-token
  "ITpdfu(iqioZyEj1Rroekw))")

(def api-key
  "MAHU*9Isu*x3DoGH6dVZbQ((")

(def access-data
  (str "&access_token=" access-token "&key=" api-key))

(def closed-questions-file
  "data/closedQuestions.json")

(def not-closed-questions-file
  "data/notClosedQuestions.json")

(defn get-questions-from-api 
  "Sends http request to api with given parameters and receives json response"
  [from-date to-date closed]
  (:body
    (client/get
      (str api-url "/search/advanced?fromdate=" from-date "&todate=" to-date "&closed=" closed "&site=stackoverflow&sort=activity&order=desc&filter=withBody&pagesize=500" access-data)
      {:as :json} )))

(defn get-user-by-id-from-api 
  "Gets details about question's owner by their id"
  [userid]
  (:body
    (client/get 
      (str api-url "/users/" userid "?order=desc&sort=reputation&site=stackoverflow" access-data)
      {:as :json} )))

(defn get-badges-by-user-id-from-api 
  "Gets user's badges"
  [userid]
  (:body
    (client/get 
      (str api-url "/users/" userid "/badges?order=desc&sort=rank&site=stackoverflow" access-data)
      {:as :json} )))

(defn get-badge-by-id-from-api
  [badgeId]
  (:body
    (client/get 
      (str api-url "/badges/" badgeId "?order=desc&sort=rank&site=stackoverflow" access-data)
      {:as :json} )))

(defn get-user's-questions-from-api
  [userid]
  (:body
    (client/get 
      (str api-url "/users/" userid "/questions?order=desc&sort=activity&site=stackoverflow" access-data)
      {:as :json} )))

(defn get-user's-answers-from-api
  [userid]
  (:body
    (client/get 
      (str api-url "/users/" userid "/answers?order=desc&sort=activity&site=stackoverflow" access-data)
      {:as :json} )))

(defn get-user's-comments-from-api
  [userid]
  (:body
    (client/get 
      (str api-url "/users/" userid "/comments?order=desc&sort=creation&site=stackoverflow" access-data)
      {:as :json} )))

(defn collect-questions 
  "Collects closed and not closed questions and saves them to json files"
  []
  (with-open [wrtr (io/writer closed-questions-file)]
    (.write wrtr
      (json/write-str 
        (get-questions-from-api true 1404172800 1419984000))))
  (with-open [wrtr (io/writer not-closed-questions-file)]
    (.write wrtr
      (json/write-str 
        (get-questions-from-api false 1404172800 1419984000)))))
