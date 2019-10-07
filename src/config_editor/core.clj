(ns config-editor.core
  (:require [config-editor.impl :as impl]
            [clojure.edn :as edn]
            [clojure.java.io :as io])
  (:import (org.eclipse.lsp4j.services LanguageServer LanguageClientAware TextDocumentService WorkspaceService)
           (org.eclipse.lsp4j.launch LSPLauncher)
           (org.eclipse.lsp4j DidSaveTextDocumentParams DidCloseTextDocumentParams DidChangeTextDocumentParams DidOpenTextDocumentParams RenameParams DocumentOnTypeFormattingParams DocumentRangeFormattingParams DocumentFormattingParams CodeLens CodeLensParams CodeActionParams DocumentSymbolParams TextDocumentPositionParams ReferenceParams CompletionItem CompletionParams InitializeParams ServerCapabilities InitializeResult CompletionOptions TextDocumentSyncKind CodeLensOptions WorkspaceSymbolParams DidChangeConfigurationParams DidChangeWatchedFilesParams CompletionItemKind)
           (java.util.concurrent CompletableFuture Executors)
           (java.time Instant)
           (java.lang ProcessHandle)
           (com.google.gson Gson)
           (java.util HashMap)
           (org.eclipse.lsp4j.jsonrpc.messages Either)
           (java.util.function Supplier))
  (:gen-class))


(let [gson (Gson.)]
  (defn |> [o]
    (let [json (.toJson gson o)]
      (into {} (.fromJson gson json ^Class HashMap)))))

(let [file (io/file "/Users/paul.rutledge/IdeaProjects/config-editor/resources/log.txt")]
  (defn log [arg]
    (with-open [writer (io/writer file :append true)]
      (let [pid (.pid (ProcessHandle/current))]
        (.write writer (str (Instant/now) ":" pid ": " (pr-str arg) \newline))))))

(defmacro eventually [& body]
  `(let [f# (reify Supplier
              (get [this]
                (try
                  ~@body
                  (catch Exception e#
                    (.printStackTrace e#)))))]
     (CompletableFuture/supplyAsync f#)))

(defn completion-item [data]
  (doto (CompletionItem.)
    (.setInsertText "{:test 1}")
    (.setLabel "Suggestion...")
    (.setKind CompletionItemKind/Snippet)))


(defn text-document-service [{:keys [] :or {} :as options}]
  (reify TextDocumentService

    (^CompletableFuture completion [this ^CompletionParams params]
      (let [p (|> params)]
        (log p))
      (eventually (Either/forLeft [(completion-item nil)])))

    (^CompletableFuture resolveCompletionItem [this ^CompletionItem item]
      (let [p (|> item)]
        (log p))
      (eventually))

    (^CompletableFuture hover [this ^TextDocumentPositionParams position]
      (let [p (|> position)]
        (log p))
      (eventually))

    (^CompletableFuture signatureHelp [this ^TextDocumentPositionParams position]
      (let [p (|> position)]
        (log p))
      (eventually))

    (^CompletableFuture definition [this ^TextDocumentPositionParams position]
      (let [p (|> position)]
        (log p))
      (eventually))

    (^CompletableFuture references [this ^ReferenceParams params]
      (let [p (|> params)]
        (log p))
      (eventually))

    (^CompletableFuture documentHighlight [this ^TextDocumentPositionParams position]
      (let [p (|> position)]
        (log p))
      (eventually))

    (^CompletableFuture documentSymbol [this ^DocumentSymbolParams params]
      (let [p (|> params)]
        (log p))
      (eventually))

    (^CompletableFuture codeAction [this ^CodeActionParams params]
      (let [p (|> params)]
        (log p))
      (eventually))

    (^CompletableFuture codeLens [this ^CodeLensParams params]
      (let [p (|> params)]
        (log p))
      (eventually))

    (^CompletableFuture resolveCodeLens [this ^CodeLens params]
      (let [p (|> params)]
        (log p))
      (eventually))

    (^CompletableFuture formatting [this ^DocumentFormattingParams params]
      (let [p (|> params)]
        (log p))
      (eventually))

    (^CompletableFuture rangeFormatting [this ^DocumentRangeFormattingParams params]
      (let [p (|> params)]
        (log p))
      (eventually))

    (^CompletableFuture onTypeFormatting [this ^DocumentOnTypeFormattingParams params]
      (let [p (|> params)]
        (log p))
      (eventually))

    (^CompletableFuture rename [this ^RenameParams params]
      (let [p (|> params)]
        (log p))
      (eventually))

    (^void didOpen [this ^DidOpenTextDocumentParams params]
      (let [p (|> params)]
        (log p)))

    (^void didChange [this ^DidChangeTextDocumentParams params]
      (let [p (|> params)]
        (log p)))

    (^void didClose [this ^DidCloseTextDocumentParams params]
      (let [p (|> params)]
        (log p)))

    (^void didSave [this ^DidSaveTextDocumentParams params]
      (let [p (|> params)]
        (log p)))))



(defn workspace-service [{:keys [] :or {} :as options}]
  (reify WorkspaceService

    (^CompletableFuture symbol [this ^WorkspaceSymbolParams params]
      (let [p (|> params)]
        (log p)
        (eventually)))

    (^void didChangeConfiguration [this ^DidChangeConfigurationParams change]
      (let [p (|> change)]
        (log p)))

    (^void didChangeWatchedFiles [this ^DidChangeWatchedFilesParams change]
      (let [p (|> change)]
        (log p)))))

(defn ^LanguageServer language-server [{:keys [] :or {} :as options}]
  (reify
    LanguageServer
    (^CompletableFuture initialize [this ^InitializeParams params]
      (eventually
        (log (|> params))
        (InitializeResult.
          (doto (ServerCapabilities.)
            (.setCompletionProvider
              (doto (CompletionOptions.)))
            (.setTextDocumentSync TextDocumentSyncKind/Incremental)))))

    (shutdown [this]
      (shutdown-agents)
      (eventually nil))

    (exit [this]
      (System/exit 0))

    (getTextDocumentService [this]
      (text-document-service options))

    (getWorkspaceService [this]
      (workspace-service options))

    LanguageClientAware
    (connect [this client]
      )))


(defn -main [& args]
  (let [opts   (edn/read-string (or (first args) "{}"))
        server (language-server opts)
        launch (LSPLauncher/createServerLauncher
                 server System/in System/out)
        client (.getRemoteProxy launch)]
    (.connect server client)
    @(.startListening launch)))

