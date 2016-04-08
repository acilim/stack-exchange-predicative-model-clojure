(ns helloclojureapp.data-classification
  (:require 
            [clojure.data.csv :as csv])
  (:use  [weka.core.converters ArffLoader CSVLoader]
         [clojure.java.io :as io]))

