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
                 :posts_with_neg_score
                 :post_score
                 :accepted_answer_score
                 :comment_score
                 :num_of_URLs
                 :num_of_stackOverflow_URLs
                 :title_length
                 :body_length
                 :num_of_tags
                 :num_of_punctuation_marks
                 :num_of_short_words
                 :num_of_special_characters
                 :num_of_lower_case_characters
                 :num_of_upper_case_characters
                 :code_snippet_length ]}))
     
(defn prepare-dataset
  [dataset]
  (dataset-set-class
    (filter-apply discretize dataset) 17))

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

(defn train-classifiers
  []  
  (train-classifier naive-bayes-classifier)
  (train-classifier support-vector-machines-classifier)
  (train-classifier logistic-regression-classifier))

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
        (:confusion-matrix (evaluate support-vector-machines-classifier)))))
  (println
    (str "Classification results saved to " classifier-evaluation-file)))

;;(train-classifiers)
;;(save-results)


