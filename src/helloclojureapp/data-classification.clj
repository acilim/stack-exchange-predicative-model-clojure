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

(def discretize
  (make-filter :unsupervised-discretize
               {:dataset-format training-set
                :attributesklapaucius
                [:age_of_account
                 :badge_score
                 :posts_with_negative_scores
                 :post_score
                 :accepted_answer_score
                 :comment_score
                 :number_of_urls
                 :number_of_stackoverflow_urls
                 :title_length
                 :body_length
                 :number_of_tags
                 :number_of_short_words]}))

(defn prepare-dataset
  [dataset]
  (dataset-set-class
    (filter-apply discretize dataset) 0))

(def classifier
  (make-classifier :bayes :naive))

(defn train-classifier []
  (classifier-train
    classifier
    (prepare-dataset training-set)))

(def evaluation
  (classifier-evaluate
    classifier
    :dataset
    (prepare-dataset training-set)
    (prepare-dataset test-set)))

(println (:summary evaluation))
(println (:confusion-matrix evaluation))

(defn save-classifier
  [classifier path]
  (serialize-to-file classifier path))

