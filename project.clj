(defproject purnam-crafty-game "0.0.0-SNAPSHOT"
  :description "Crafty Game Demo"
  :url "http://docs.caudate.me/purnam-crafty-game"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-1978"]
                 [im.chit/purnam "0.1.5"]]
  
  :cljsbuild
  {:builds  [{:source-paths ["src"],
              :id "crafty-demo",
              :compiler {:pretty-print true,
                         :output-to "resource/public/crafty-demo.js",
                         :optimizations :whitespace}}]})
