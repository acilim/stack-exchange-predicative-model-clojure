(ns helloclojureapp.tests
  (:use [clojure.test])
  (:use [helloclojureapp.data-processing]))

(def testQuestion 
  {:question_id 24617665
   :title "TestQuestion" 
   :body "Question body. <code>123456</code> Here are some special characters: !#%^&*( and some more code: <code>789</code>.\nLet's also have some urls: <a href=...>url1</a> <a href='otherurl'>...</a> And one stackoverflow/questions/... url."
   :owner {:user_id 655490} 
   :tags ["first" "second" "third"]
   :score 12 })

(deftest test-number-of-urls 
  (is
    (getNumberOfURLs testQuestion 2)))

(deftest test-number-of-stackoverflow-urls 
  (is
    (getNumberOfURLs testQuestion 1)))

(deftest test-title-length 
  (is
    (getTitleLength testQuestion 12)))

(deftest test-body-length 
  (is
    (getBodyLength testQuestion 230)))

(deftest test-num-of-tags
  (is
    (getNumberOfTags testQuestion 3)))

(deftest test-num-of-punctuation_marks
  (is
    (getNumberOfPunctuationMarks testQuestion 16)))

(deftest test-num-of-short-words
  (is
    (getNumberOfShortWords testQuestion 6)))

(deftest test-num-of-lowercase-letters
  (is
    (getNumberOfLowercaseLetters testQuestion 141)))

(deftest test-num-of-uppercase-letters
  (is
    (getNumberOfUppercaseLetters testQuestion 4)))

(deftest test-code-snippet-length
  (is
    (getNumberOfUppercaseLetters testQuestion 9)))


