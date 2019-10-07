(defproject config-editor "0.1.0-SNAPSHOT"

  :repositories
  [["jitpack" "https://jitpack.io"]]

  :dependencies
  [[org.clojure/clojure "1.10.1"]
   [com.vodori/missing "0.1.12"]
   [prismatic/schema "1.1.12"]
   [com.google.code.gson/gson "2.8.6"]
   [org.eclipse.lsp4j/org.eclipse.lsp4j "0.8.1"]]

  :repl-options
  {:init-ns config-editor.core}

  :main config-editor.core)
