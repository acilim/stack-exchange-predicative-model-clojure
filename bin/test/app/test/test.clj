(ns helloclojureapp.tests
  (:use [clojure.test])
  (:use [helloclojureapp.data-processing]))

(def testQuestion 
  "{        \"body\": \"The quick brown fox jumps over the lazy dog.\", 
            \"owner\": { \"user_id\": 655490, }, 
            \"question_id\": 24617665, 
            \"score\": 0, 
            \"tags\": [\"tag1\", \"tag2\", \"tag3\"], 
            \"title\": \"Java, Where to place public enums\"
        }")

(println (getNumberOfShortWords testQuestion))

(deftest testGetNumberOfShortWords 
  (is  (getNumberOfShortWords testQuestion )))
