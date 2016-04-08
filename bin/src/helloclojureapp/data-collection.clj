(ns helloclojureapp.data-collection
  (:require [clj-http.client :as client]
            [clojure.data.json :as json])
  (:use [clojure.java.io :as io]))

(def apiUrl
  "https://api.stackexchange.com/2.2")

(def accessToken
  "ITpdfu(iqioZyEj1Rroekw))")

(def apiKey
  "MAHU*9Isu*x3DoGH6dVZbQ((")

(def accessData
  (str "&access_token=" accessToken "&key=" apiKey))

(def closedQuestionsPath
  "data/closedQuestions.json")

(def notClosedQuestionsPath
  "data/notClosedQuestions.json")

(defn getQuestionsFromApi 
  "Sends http request to api with given parameters and receives json response"
  [fromDate toDate closed]
	 (:body
    (client/get
      (str apiUrl "/search/advanced?fromdate=" fromDate "&todate=" toDate "&closed=" closed "&site=stackoverflow&sort=activity&order=desc&filter=withBody" accessData)
      {:as :json} )))

(defn getUserById 
  "Gets details about question's owner by their id"
  [userid]
  (:body
    (client/get 
      (str apiUrl "/users/" userid "?order=desc&sort=reputation&site=stackoverflow" accessData)
      {:as :json} )))

(defn getBadgesByUserId 
  "Gets user's badges"
  [userid]
  (:body
    (client/get 
      (str apiUrl "/users/" userid "/badges?order=desc&sort=rank&site=stackoverflow" accessData)
      {:as :json} )))

(defn getBadgeById
  [badgeId]
  (:body
    (client/get 
      (str apiUrl "/badges/" badgeId "?order=desc&sort=rank&site=stackoverflow" accessData)
      {:as :json} )))

(defn getQuestionsOfUser
  [userid]
  (:body
    (client/get 
      (str apiUrl "/users/" userid "/questions?order=desc&sort=activity&site=stackoverflow" accessData)
      {:as :json} )))

(defn getAnswersOfUser
  [userid]
  (:body
    (client/get 
      (str apiUrl "/users/" userid "/answers?order=desc&sort=activity&site=stackoverflow" accessData)
      {:as :json} )))

(defn getCommentsOfUser
  [userid]
  (:body
    (client/get 
      (str apiUrl "/users/" userid "/comments?order=desc&sort=creation&site=stackoverflow" accessData)
      {:as :json} )))

(defn saveQuestionData 
  "Collects closed and not closed questions and saves them to json files"
  []
  (with-open [wrtr (io/writer closedQuestionsPath)]
    (.write wrtr
      (json/write-str 
        (getQuestionsFromApi true 1404172800 1419984000))))
  (with-open [wrtr (io/writer notClosedQuestionsPath)]
    (.write wrtr
      (json/write-str 
        (getQuestionsFromApi false 1404172800 1419984000)))))


