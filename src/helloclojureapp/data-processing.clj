(ns helloclojureapp.data-processing
  (:require [clojure.data.json :as json]
            [clojure.data.csv :as csv])
  (:use [helloclojureapp.data-collection]
        [clojure.java.io :as io]))

(def training-set-file
  "data/trainingSet.csv")
(def test-set-file
  "data/testSet.csv")

(def closed-questions-json 
  (get
    (json/read-str
      (slurp closed-questions-file) :key-fn keyword) :items))

(def not-closed-questions-json
  (get
    (json/read-str
      (slurp not-closed-questions-file) :key-fn keyword) :items))

(defn getScore [question]
  (get question :score))

(defn getUserId [question]
  (get (get question :owner) :user_id))

(defn getUser
  "Gets user's data with given id"
  [id]
  (first
    (get
      (get-user-by-id-from-api id) :items)))

(defn getBody [question]
  (get question :body))

(defn getAgeOfAccount
  "Calculates the value of feature A1: ageOfAccount"
  [question]
  (println "Getting feature A1...")
  (-
    (.getTime
      (new java.util.Date))
    (if (nil? (get
                (getUser
                  (getUserId question)) :creation_date))
      0
      (get
        (getUser
          (getUserId question)) :creation_date))))

(defn getBadges [question]
  (get
    (get-badges-by-user-id-from-api
      (getUserId question)) :items))

(defn getBadgeData [badge]
  (get-badge-by-id-from-api
    (get badge :badge_id)))

(defn getBadge [id]
  (first
    (get
      (get-badge-by-id-from-api id) :items)))

(defn getBadgeScore 
  "Calculates the value of feature A2 : badgeScore"
  [question]
  (println "Getting feature A2...")
  (let [score (atom 0)]
    (doseq 
      [badge (getBadges question)] 
      (if 
        (> 
          (get
            (getBadge
              (get badge :badge_id)) :award_count) 0)
        (swap!
          score
          #(+ % (/ 1.0 (get badge :award_count))))))
    @score))

(defn getQuestionsByUser [question]
  (get
    (get-user's-questions-from-api
      (getUserId question)) :items))

(defn getAnswersByUser [question]
  (get
    (get-user's-answers-from-api
      (getUserId question)) :items))

(defn getCommentsByUser [question]
  (get
    (get-user's-comments-from-api
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
  (count
    (re-seq
      (re-pattern "<a href=")
      (getBody question))))

(defn getNumberOfStackOverflowURLs
  "Calculates the value of feature C2 : numberOfStackoverflowURLs"
  [question]
  (println "Getting feature C2...")  
  (count
    (re-seq #"stackoverflow/"
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
  (count
    (re-seq
      (re-pattern "[.,:;!?\\-]")
      (getBody question))))

(defn getNumberOfShortWords
  "Calculates the value of feature D5 : numberOfShortWords"
  [question]
  (println "Getting feature D5...")  
  (let [number (atom 0)]
    (doseq [word (clojure.string/split (getBody question) #"\s+")] 
      (if (< (count word) 4)
        (swap! number #(+ % 1))))
    @number))

(defn getNumberOfSpecialCharacters
  "Calculates the value of feature D6 : numberOfSpecialCharacters"
  [question]
  (println "Getting feature D6...")
  (count
    (re-seq
      (re-pattern "[^A-Za-z0-9]")
      (getBody question))))

(defn getNumberOfLowercaseLetters
  "Calculates the value of feature D7 : numberOfSpecialCharacters"
  [question]
  (println "Getting feature D7...")
  (count
    (re-seq
      (re-pattern "[a-z]")
      (getBody question))))

(defn getNumberOfUppercaseLetters
  "Calculates the value of feature D8 : numberOfSpecialCharacters"
  [question]
  (println "Getting feature D8...")
  (count
    (re-seq
      (re-pattern "[A-Z]")
      (getBody question))))

(defn getCodeSnippetLength
  "Calculates the value of feature D9 : code snippet length"
  [question]
  (println "Getting feature D9...")
  (let [length (atom 0)]
    (doseq [match (re-seq
                    (re-pattern "<code>(.*?)</code>")
                    (getBody question))]
      (swap! 
        length 
        #(+ % (count (second match)))))
    @length))

(defn get-features 
  [question is-closed]
  [(getAgeOfAccount question)
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
   (getNumberOfPunctuationMarks question)
   (getNumberOfShortWords question)
   (getNumberOfSpecialCharacters question)
   (getNumberOfLowercaseLetters question)
   (getNumberOfUppercaseLetters question)
   (getCodeSnippetLength question)
   is-closed])

(defn create-dataset
  "Creates csv file with questions' features"
  [path closedQuestions notClosedQuestions]
  (println (str "Writing to " path "..."))
  (with-open [wrtr (io/writer path)]
    (csv/write-csv 
      wrtr
      [["age_of_account"
        "badge_score"
        "posts_with_neg_score"
        "post_score"
        "accepted_answer_score"
        "comment_score" 
        "num_of_URLs" 
        "num_of_stackOverflow_URLs"
        "title_length" 
        "body_length"
        "num_of_tags"
        "num_of_punctuation_marks"
        "num_of_short_words" 
        "num_of_special_characters"
        "num_of_lower_case_characters" 
        "num_of_upper_case_characters"
        "code_snippet_length"
        "class"]])
    (doseq [question closedQuestions]
      (println (str "\nGetting data about question: "
                    (get question :question_id)))
      (csv/write-csv
        wrtr 
        [(get-features question "closed")]))
    (doseq [question notClosedQuestions]
      (println 
        (str "\nGetting data about question: " 
             (get question :question_id)))
      (csv/write-csv
        wrtr 
        [(get-features question "not_closed")])))
  (println
    (str "Dataset created and saved to " path)))

(defn create-training-dataset
  []
  (println "Creating training dataset.....")
  (create-dataset
    training-set-file 
    (take 400 closed-questions-json) 
    (take 400 not-closed-questions-json)))

(defn create-test-dataset
  []
  (println "Creating test dataset.....")
  (create-dataset
    test-set-file
    (take-last 100 closed-questions-json)
    (take-last 100 not-closed-questions-json)))

;;(create-training-dataset)
;;(create-test-dataset)
