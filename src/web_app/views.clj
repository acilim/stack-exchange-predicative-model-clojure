(ns web_app.views
  (:require [clojure.string :as str]
            [hiccup.page :as page])
  (:use [machinelearning.data-classification]))

(defn home-page
  []
  (page/html5
    [:head
     [:title (str "Stack exchange predicative model")]
     (page/include-css "/css/style.css" "/css/reset.css")
     (page/include-js "/js/validation.js")]   
     [:div {:class "basic-grey"
            :style "display:block"}
      [:h1 "Classify your question! "]
      [:h2 "Enter data:"]
      [:form {:action "/classify"
              :method "POST"
              :name "qForm"
              :onsubmit "return validateForm()"} 
       [:label "Your user id:"]
       [:input {:type "text"
                :name "user-id" 
                :placeholder "id"
                :class "inputText"}]
       [:label "Your question:"]
       [:input {:type "text"
                :name "text" 
                :placeholder "text"}]
       [:button "Classify"]]]))

(defn results-page
  [{:keys [text user-id]}]
  (let [closed (classify user-id text)]
    (page/html5
      [:head
       [:title (str "Stack exchange predicative model")]
       (page/include-css "/css/style.css" "/css/reset.css")]  
      [:div {:class "basic-grey"}
       [:h1 "Your result:"]  
       [:h2 closed]      
       [:a {:href "/"} "Classify another"]])))
