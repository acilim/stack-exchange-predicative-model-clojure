(ns helloclojureapp.data-processing
  (:require [clj-http.client :as client]
            [clojure.data.json :as json]
            [clojure.data.csv :as csv])
  (:use [helloclojureapp.data-collection]
        [clojure.java.io :as io]))

(def notClosedPath "data/notClosedQuestions.json")
(def closedPath "data/closedQuestions.json")

(def closedQuestionsJson 
  (get
    (json/read-str
      (slurp closedPath) :key-fn keyword) :items))

(def notClosedQuestionsJson
  (get
    (json/read-str
      (slurp notClosedPath) :key-fn keyword) :items))

(defn getScore [question]
  (get question :score))

(defn getUserId [question]
  (get (get question :owner) :user_id))

(defn getUser
  "Gets user's data with given id"
  [id]
  (first
    (get
      (getUserById id) :items)))

(defn getBody [question]
  (get question :body))

(defn getAgeOfAccount
  "Calculates the value of feature A1: ageOfAccount"
  [question]
  (println "Getting feature A1...")
  (- (.getTime
       (new java.util.Date))
     (get
       (getUser
         (getUserId question)) :creation_date)))

(defn getBadges [question]
  (get
    (getBadgesByUserId
      (getUserId question)) :items))

(defn getBadgeData [badge]
  (getBadgeById (get badge :badge_id)))

(defn getBadge [id]
  (first
    (get
      (getBadgeById id) :items)))

(defn getBadgeScore 
  "Calculates the value of feature A2 : badgeScore"
  [question]
  (println "Getting feature A2...")
  (let [score (atom 0)]
    (doseq [badge (getBadges question)] 
      (if 
        (> 
          (get (getBadge (get badge :badge_id)) :award_count) 0)
        (swap! score #(+ % (/ 1.0 (get badge :award_count))))))
    @score))

(defn getQuestionsByUser [question]
  (get
    (getQuestionsOfUser
      (getUserId question)) :items))

(defn getAnswersByUser [question]
  (get
    (getAnswersOfUser
      (getUserId question)) :items))

(defn getCommentsByUser [question]
  (get
    (getCommentsOfUser
      (getUserId question)) :items))

(defn getPostsWithNegativeScores
  "Calculates the value of feature A3 : postsWithNegativeScores"
  [question]
  (println "Getting feature A3...")
  (let [score (atom 0)]
    (doseq [q (getQuestionsByUser question)] 
      (if (<  (get q :score) 0)
        (swap! score #(+ % 1))))
    (doseq [a (getAnswersByUser question)] 
      (if (<  (get a :score) 0)
        (swap! score #(+ % 1))))
    @score))

(defn getPostScore
  "Calculates the value of feature B1 : postScore"
  [question]
  (println "Getting feature B1...")
  (let [score (atom 0)]
    (doseq [q (getQuestionsByUser question)] 
      (swap! score #(+ % (get q :score))))
    (doseq [a (getAnswersByUser question)] 
      (swap! score #(+ % (get a :score))))
    @score))

(defn getAcceptedAnswerScore
  "Calculates the value of feature B2 : acceptedAnswerScore"
  [question]
  (println "Getting feature B2...")
  (let [score (atom 0)]
    (doseq [a (getAnswersByUser question)] 
      (if (= (get a :is_accepted) true)
        (swap! score #(+ % 15))))
    @score))

(defn getCommentScore
  "Calculates the value of feature B3 : commentScore"
  [question]
  (println "Getting feature B3...")
  (let [score (atom 0)]
    (doseq [c (getCommentsByUser question)] 
      (swap! score #(+ % (get c :score))))
    @score))

(defn getNumberOfURLs
  "Calculates the value of feature C1 : numberOfURLs"
  [question]
  (println "Getting feature C1...")
  (count (re-seq (re-pattern "<a href=")
                 (getBody question))))

(defn getNumberOfStackOverflowURLs
  "Calculates the value of feature C2 : numberOfStackoverflowURLs"
  [question]
  (println "Getting feature C2...")  
  (count (re-seq #"stackoverflow/"
                 (getBody question))))

(defn getTitleLength
  "Calculates the value of feature D1 : titleLength"
  [question]
  (println "Getting feature D1...")  
  (count
    (get question :title)))

(defn getBodyLength
  "Calculates the value of feature D2 : bodyLength"
  [question]
  (println "Getting feature D2...")  
  (count
    (getBody question)))

(defn getNumberOfTags
  "Calculates the value of feature D3 : numberOfTags"
  [question]
  (println "Getting feature D3...")  
  (count
    (get question :tags)))

(defn getNumberOfPunctuationMarks
  "Calculates the value of feature D4 : numberOfPunctuationMarks"
  [question]
  (println "Getting feature D4...")  
  (let [number (atom 0)
        punctuationMarks ".,!?:;"]
    (doseq [char (getBody question)] 
      (if ((.contains punctuationMarks char))
        (swap! number #(+ % 1))))
    @number))

(defn getNumberOfShortWords
  "Calculates the value of feature D5 : numberOfShortWords"
  [question]
  (println "Getting feature D5...")  
  (let [number (atom 0)]
    (doseq [word (clojure.string/split (getBody question) #"\s+")] 
      (if (< (count word) 4)
        (swap! number #(+ % 1))))
    @number))

(defn createDatsetFile
  "Creates csv file with questions' features"
  []
  (with-open [wrtr (io/writer "data/dataset.csv")]
    (csv/write-csv wrtr [["is_closed" "age_of_account" "badge_score" "posts_with_negative_scores" "post_score" "accepted_answer_score" "comment_score" "number_of_urls" "number_of_stackoverflow_urls" "title_length" "body_length" "number_of_tags" "number_of_short_words"]])
    (doseq [question (take 1 closedQuestionsJson)]
      (csv/write-csv
        wrtr 
        [["closed"
          (getAgeOfAccount question)
          (getBadgeScore question)
          (getPostsWithNegativeScores question)
          (getPostScore question)
          (getAcceptedAnswerScore question)
          (getCommentScore question)
          (getNumberOfURLs question)
          (getNumberOfStackOverflowURLs question)
          (getTitleLength question)
          (getBodyLength question)
          (getNumberOfTags question)
          (getNumberOfShortWords question)
          ]])
      (println "Question written to csv.")
      (println "")
      )))

(createDatsetFile)
