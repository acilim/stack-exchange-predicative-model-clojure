# StackExchangePredicativeModel

## About the project

The aim of this project was to build a prediction model that would be able to predict whether the question posted on [stackoverflow.com] (http://stackoverflow.com/) was going to be closed or not.

Stackoverflow is a website where users can ask questions on various topics in computer programming, answer other users’ questions and earn points and badges by actively participating in the community. To prevent low quality questions stackoverflow has been using a closing questions mechanism since 2013, which allows experienced community members to mark a question closed if they estimate it not to be fit for the website. A question can be marked closed for five reasons: 
* *duplicate*, 
* *off-topic*,
*  *unclear-what-you’re-asking*, 
* *too broad* and 
* *primarily-opinion-based*.

The project workflow consisted of the following steps:
  1. Collecting the data relevant for the project using [StackExchangeAPI] (https://api.stackexchange.com/)
  2. Processing collected data - adding features for classification and creating the dataset
  3. Applying machine learning techniques for classification
  4. Evaluating classification results

1. Collecting data
==========

500 closed questions and 500 not closed questions were collected for the purposes of the project through the StackExchangeAPI. The questions were collected using the [/search]( https://api.stackexchange.com/docs/advanced-search) method with the following parmeters:
* *fromDate*: 1404172800 (1/7/2014)
* *toDate*: 1419984000 (31/12/2014)
* *closed*: true for closed questions, false for not closed questions
* *filter*: withBody, in order to get bodies of the questions
* *accessToken* and *key* obtained by registering to the api, in order to increase the daily request quota.

The results were saved to files [closedQuestions.json](https://github.com/acilim/StackExchangePredicativeModelClojure/blob/master/data/closedQuestions.json) and [notClosedQuestions.json](https://github.com/acilim/StackExchangePredicativeModelClojure/blob/master/data/notClosedQuestions.json).

2. Adding features and creating the dataset
============ 
In the next step, each question was added features for classification. The features can be divided into four groups:

| Group  | Name | Features |
| ------------- | ------------- |  ------------- |
| **A**  | User Profile  | *age_of_account*, *badge_score*, *posts_with_negative_score*  |
| **B**  | Community Process| *post_score*, *accepted_answer_score*, *comment_score*|
| **C**  | Question Content  | *number_of_urls*, *number_of_stackoverflow_urls*|
| **D** | Textual Style| *title_length*, *body_length*, *number_of_tags*, *number_of_punctuation_marks*, *number_of_short_words*, *number_of_special_characters*, *number_of_lower_case_characters*, *number_of_upper_case_characters*, *code_snippet_length*|

Features of group **A** are related to user’s profile and participation activities in the community, whereas features of group **B** are based on contributions to the community in the form of votes, answers, etc.  Group **C** contains features related to question content, and features of group **D** describe the textual style of the question title and body. Most of the features are self-describing, although some of them require further explanation:

* **Badge score**

 Let {b1 , … , bn} be the badges earned by the user. Then:

  ![equation](https://latex.codecogs.com/gif.latex?badgeScore%20%3D%20%5Csum_%7Bi%3D1%7D%5E%7Bn%7D%20%5Cfrac%7B1%7D%7BnumOfUsersWhoOwnb_i%7D)

* **Post score**

  Let {q1 , … , qn} be the set of questions asked by the user, and {a1 , … , am} the set of answers posted by the user. Then:

  ![equation](https://latex.codecogs.com/gif.latex?postScore%3D%5Csum_%7Bi%3D1%7D%5E%7Bn%7Dscore%28q_i%29%20&plus;%20%5Csum_%7Bj%3D1%7D%5E%7Bm%7Dscore%28a_j%29)

* **Comment score**

  Let {c1 , … , cn} be the comments posted by the user. Then:

  ![equation](https://latex.codecogs.com/gif.latex?commentScore%3D%5Csum_%7Bi%3D1%7D%5E%7Bn%7D%20score%28c_i%29)

* **Accepted answer score**

  Let {a1 , … , an} be the set of answers posted by the user which have been accepted. Each acepted answer has the score of 15, therefore:

  ![equation](https://latex.codecogs.com/gif.latex?acceptedAnswerScore%20%3D%20%5Csum_%7Bi%3D1%7D%5E%7Bn%7D%2015)

The following api methods were used to collect the necessary data:

* [/users/{ids}] (http://api.stackexchange.com/docs/users-by-ids) – the method which returns data about user with the requested id
* [/users/{ids}/badges](http://api.stackexchange.com/docs/badges-on-users) – returns the badges owned by the user with the requested id
* [/badges/{ids}](http://api.stackexchange.com/docs/badges-by-ids) – returns data about badge with the requested id
* [/users/{ids}/questions](http://api.stackexchange.com/docs/questions-on-users) – returns the questions that the requested user posted
* [/users/{ids}/answers](http://api.stackexchange.com/docs/answers-on-users) – returns the answers that the requested user posted
* [/users/{ids}/comments](http://api.stackexchange.com/docs/comments-on-users) – returns the comments that the requested user posted

Questions' features were saved to .csv files- one used for training: [trainingSet.csv] (https://github.com/acilim/StackExchangePredicativeModelClojure/blob/master/data/trainingSet.csv), with 90% of the data, and the other used for testing the classifiers: [testSet.csv] (https://github.com/acilim/StackExchangePredicativeModelClojure/blob/master/data/testSet.csv), with 10% of the data. Each dataset contains 18 attributes: 17 are numeric (the features), and the 18th is the class attribute with possible values *yes* or *no* (that shows whether or not the question is closed), the one whose value the program is aimed to predict.


3. Applying machine learning techniques for classification
=======================

The dataset was first loaded from the .csv file, and since it contained numeric attributes it needed to be discretized. After that, the classifier could be applied.
Three classifiers were used and evaluated:

* [NaiveBayes](http://weka.sourceforge.net/doc.dev/weka/classifiers/bayes/NaiveBayes.html)
* [Support Vector Machines](http://weka.sourceforge.net/doc.dev/weka/classifiers/functions/SMO.html)
* [Logistic Regression](http://weka.sourceforge.net/doc.dev/weka/classifiers/functions/Logistic.html)

4. Evaluation of the results
====================
All the clasiffiers were trained on the training dataset and later evaluated using the test dataset. Their results were as follows:

**Naive Bayes**

| DataSet | Correctly classified instances % | Precision | Recall | F1 |
| --- | ----------- | ------------- | ----------- | ---- |
| Training  |   |   |   |   |
| Test|  |   |  | |  |

Confusion matrix:

| a | b  | <-- classified as |
| --- | --- | --------- |
| |  | a (closed) |
|  |  | b (not_closed) |

**Support Vector Machines**

| DataSet | Correctly classified instances % | Precision | Recall | F1 |
| --- | ----------- | ------------- | ----------- | ---- |
| Training  |   |   |   |   |
| Test|  |   |  | |  |

Confusion matrix:

| a | b  | <-- classified as |
| --- | --- | ------------ |
| |  |  a (closed) |
| |  |  b (not_closed) |

**Logistic Regression**

| DataSet | Correctly classified instances % | Precision | Recall | F1 |
| --- | ----------- | ------------- | ----------- | ---- |
| Training  |  |   |   |   |
| Test|  |   |  | | |

Confusion matrix:

| a | b  | <-- classified as |
| --- | --- | ------------ |
| |  |  a (closed) |
| |  |  b (not_closed) |


5. Technical realisation
=============================


The application was written in Clojure programming language, using CounterClockwise IDE. The following dependencies were added to the project:

 * [org.clojure/clojure "1.5.1"]
 * [org.clojure/data.json "0.2.0"]
 * [org.clojure/data.csv "0.1.3"]
 * [clj-http "0.6.0"]
 * [ring/ring-json "0.4.0"]
 * [nz.ac.waikato.cms.weka/weka-dev "3.7.7"]
 * [cc.artifice/clj-ml "0.6.1"]

 
6. Acknowledgements 
========================

The project was developed as part of the assignment for the course [Software Engineering Tools and Methodology](http://ai.fon.bg.ac.rs/master/alati-i-metode-softverskog-inzenjerstva/) at the [Faculty of Organization Sciences](http://fon.rs), University of Belgrade, Serbia.
It was based on the project [StackExchangePredicativeModel] (https://github.com/acilim/StackExchangePredicativeModel) developed in Java during my bachelor studies, for which ideas and guidelines were found in the work [Fit or Unfit : Analysis and Prediction of ‘Closed Questions’] (http://arxiv.org/abs/1307.7291).


