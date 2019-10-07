(ns config-editor.impl)

(def DB (atom {}))

(defn char-range [start end]
  (map char (range (int start) (inc (int end)))))