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

(defn getScore
  [question]
  (:score question))

(defn getUserId
  [question]
  (:user_id (:owner question)))

(defn getUser
  "Gets user's data with given id"
  [id]
  (first
    (:items
      (get-user-by-id-from-api id))))

(defn getBody
  [question]
  (:body question))

(defn getAgeOfAccount
  "Calculates the value of feature A1: ageOfAccount"
  [question]
  (println "Getting feature A1...")
  (-
    (.getTime
      (new java.util.Date))
    (if (nil? (:creation_date
                (getUser
                  (getUserId question))))
      0
      (:creation_date
        (getUser
          (getUserId question))))))

(defn getBadges
  [question]
  (get
    (get-badges-by-user-id-from-api
      (getUserId question)) :items))

(defn getBadgeData 
  [badge]
  (get-badge-by-id-from-api
    (:badge_id badge)))

(defn getBadge
  [id]
  (first
    (:items
      (get-badge-by-id-from-api id))))

(defn getBadgeScore 
  "Calculates the value of feature A2 : badgeScore"
  [question]
  (println "Getting feature A2...")
  (let [score (atom 0)]
    (doseq [badge (getBadges question)] 
      (if (> (:award_count
               (getBadge
                 (:badge_id badge)))
             0)
        (swap! score  #(+ % (/ 1.0 (:award_count badge))))))
    @score))

(defn getQuestionsByUser 
  [question]
  (:items
    (get-user's-questions-from-api
      (getUserId question))))

(defn getAnswersByUser 
  [question]
  (:items
    (get-user's-answers-from-api
      (getUserId question))))

(defn getCommentsByUser 
  [question]
  (:items
    (get-user's-comments-from-api
      (getUserId question))))

(defn getPostsWithNegativeScores
  "Calculates the value of feature A3 : postsWithNegativeScores"
  [question]
  (println "Getting feature A3...")
  (let [score (atom 0)]
    (doseq [q (getQuestionsByUser question)] 
      (if (neg? (:score q))
        (swap! score #(inc %))))
    (doseq [a (getAnswersByUser question)] 
      (if (neg?  (:score a))
        (swap! score #(inc %))))
    @score))

(defn getPostScore
  "Calculates the value of feature B1 : postScore"
  [question]
  (println "Getting feature B1...")
  (let [score (atom 0)]
    (doseq [q (getQuestionsByUser question)] 
      (swap! score #(+ % (:score q))))
    (doseq [a (getAnswersByUser question)] 
      (swap! score #(+ % (:score q))))
    @score))

(defn getAcceptedAnswerScore
  "Calculates the value of feature B2 : acceptedAnswerScore"
  [question]
  (println "Getting feature B2...")
  (let [score (atom 0)]
    (doseq [a (getAnswersByUser question)] 
      (if (= (:is_accepted a) true)
        (swap! score #(+ % 15))))
    @score))

(defn getCommentScore
  "Calculates the value of feature B3 : commentScore"
  [question]
  (println "Getting feature B3...")
  (let [score (atom 0)]
    (doseq [c (getCommentsByUser question)] 
      (swap! score #(+ % (:score c))))
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
    (:title question)))

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
    (:tags question)))

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
        (swap! number #(inc %))))
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
      (swap!  length  #(+ % (count (second match)))))
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
