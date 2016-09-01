(ns rmap.core-test
  (:require [clojure.test :refer :all]
            [rmap.core :as rmap]))

(deftest core-test
  (testing "routes definition"
    (testing "when correct routes definition given"
      (testing "it returns correct routes map"
        (is (instance? clojure.lang.IRecord (rmap/routes {})))))
    (testing "when incorrect routes definition given"
      (testing "it raises error"
        (is (thrown? java.lang.AssertionError (rmap/routes 1)))
        (is (thrown? java.lang.AssertionError (rmap/routes []))))))

  (testing "routes matching"
    (testing "when good request"
      (testing "when matches")
      (testing "when does not match"))
    (testing "when bad request")))
