(ns rmap.core
  (:require [clojure.string :as str]
            [clojure.spec :as s]))

(defprotocol RoutesMap
  (match[this request]))

(defn- matches?
  [part http-verb rule]
  (let [rverbs (:keys (:on rule))]
    (if (contains? ((:keys (:on rule))) http-verb)
      (if-let [path (:path rule)]
        (if (= path part) true false)
        (let [param (:param rule)]
          (if-let [matches-constraint? (:constraint rule)]
            (matches-constraint? part)
            true)))
      false)))

(defrecord Routes [routes]
  RoutesMap
  (match
    [this request]
    (let [uri-parts (str/split (:uri request) #"/")
         http-verb (:request-method request)]
      (loop [part (first uri-parts)
             parts (rest uri-parts)
             rule this
             sibling-rules nil]
        (if (matches? part http-verb rule)
          (if-let [nested-rules (:/ rule)]
            (recur (first parts) (rest parts) (first nested-rules) (rest nested-rules))
            (let [func (http-verb (:on rule))]
              (func request)))
          (if-let [[rule & sibling-rules] sibling-rules]
            (recur part parts rule sibling-rules)
            false))))))

;; {:path "" :on {:get get-home
;;           :/ '({:path "articles" :on {:get get-articles
;;                                      :post create-article}
;;                                 :/ '({:param :article_id :on {:get get-article
;;                                                              :put update-article}})})}}



(defn- valid-routes-description?
  [routes]
  (= clojure.lang.PersistentArrayMap (type routes)))

(defn routes
  "Creates routes map record by valid routes description"
  [routes]
  {:pre [(valid-routes-description? routes)]}
  (->Routes routes))

