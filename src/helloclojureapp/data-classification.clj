(ns helloclojureapp.data-classification
  (:use [clj-ml.io]
        [clj-ml.filters]
        [clj-ml.classifiers]
        [clj-ml.data]
        [clj-ml.utils]
        [clojure.java.io :as io]
        [helloclojureapp.data-processing])
  (:import [weka.core.converters ArffLoader CSVLoader]
           [weka.classifiers.bayes NaiveBayes]
           [java.io File]))

(def training-set
  (load-instances :csv training-set-file))

(def test-set
  (load-instances :csv test-set-file))

(def classifier-evaluation-file
  "data/results.txt")

(def discretize
  (make-filter :unsupervised-discretize
               {:dataset-format training-set
                :attributes
                [:age_of_account
                 :badge_score
                 :posts_with_negative_scores
                 :post_score
                 :accepted_answer_score
                 :comment_score
                 :no_of_urls
                 :no_of_stackoverflow_urls
                 :title_length
                 :body_length
                 :no_of_tags
                 :no_of_punctuation_marks
                 :no_of_short_words
                 :no_of_special_characters
                 :no_of_lowercase_letters
                 :no_of_uppercase_letters
                 :code_snippet_length ]}))
     
(defn prepare-dataset
  [dataset]
  (dataset-set-class
    (filter-apply discretize dataset) 0))

(def naive-bayes-classifier
  (make-classifier :bayes :naive))

(def logistic-regression-classifier
  (make-classifier :regression :logistic))

(def support-vector-machines-classifier
  (make-classifier :support-vector-machine :smo))

(defn train-classifier
  [classifier]
  (classifier-train
    classifier
    (prepare-dataset training-set)))

(defn evaluate
  [classifier]
  (classifier-evaluate
    classifier
    :dataset
    (prepare-dataset training-set)
    (prepare-dataset test-set)))

(defn save-results 
  "Saves classifier evaluation results to file"
  []
  (with-open [wrtr (io/writer classifier-evaluation-file)]
    (.write wrtr
      (str 
        "***Naive Bayes results: \n"
        (:summary (evaluate naive-bayes-classifier))
        "'n"
        (:confusion-matrix (evaluate naive-bayes-classifier))
        "\n***SMO results: \n"
        (:summary (evaluate support-vector-machines-classifier))
        "\n"
        (:confusion-matrix (evaluate support-vector-machines-classifier))
        "\n***Logistic regression results:\n"
        (:summary (evaluate support-vector-machines-classifier))
        "\n"
        (:confusion-matrix (evaluate support-vector-machines-classifier))))))

;;(train-classifier naive-bayes-classifier)
;;(train-classifier support-vector-machines-classifier)
;;(train-classifier logistic-regression-classifier)
;;(save-results)


