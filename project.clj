(defproject example.purnam.game "0.0.0-SNAPSHOT"
  :description "Crafty Game Demo"
  :url "http://purnam.github.io/example.purnam.game"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [im.chit/purnam.core "0.4.3"]]
  :profiles {:dev {:dependencies [[org.clojure/clojurescript "0.0-2138"]]
                   :plugins [[lein-cljsbuild "1.0.0"]]}}
  :cljsbuild
  {:builds  [{:source-paths ["src"],
              :id "crafty-demo",
              :compiler {:pretty-print true,
                         :output-to "resource/public/crafty-demo.js",
                         :optimizations :whitespace}}]})