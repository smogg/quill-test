(ns quill-test.prod
  (:require [quill-test.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
