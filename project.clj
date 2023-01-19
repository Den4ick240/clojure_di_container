(defproject diclojure "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [javax.annotation/javax.annotation-api "1.3.2"]
                 [javax.inject/javax.inject "1"]]
  :repl-options {:init-ns diclojure.core}
  :java-source-paths ["src/java"]
  :source-paths ["src/clojure"]
  )
