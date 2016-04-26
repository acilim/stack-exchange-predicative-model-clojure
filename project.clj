(defproject helloclojureapp "0.1.0-SNAPSHOT"
  :description "Prediction model that uses StackExchange API and predicts whether the question asked pon stackoverflow would be closed or not."
  :url "https://github.com/acilim/stack-exchange-predicative-model-clojure"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/data.json "0.2.0"]
                 [org.clojure/data.csv "0.1.3"]
                 [clj-http "0.6.0"]
                 [ring/ring-json "0.4.0"]
                 [nz.ac.waikato.cms.weka/weka-dev "3.7.7"]
                 [cc.artifice/clj-ml "0.6.1"]
                 [criterium "0.4.4"]]
    :main helloclojureapp.main-class)
