(ns io.perun.rss
  (:require [io.perun.core   :as perun]
            [clj-rss.core    :as rss-gen]))

(defn rss-definitions [files]
  (for [file files]
    {:link        (:canonical-url file)
     :guid        (:canonical-url file)
     :pubDate     (:date-published file)
     :title       (:name file)
     :description (:description file)
     :author      (:author-email file)}))

(defn generate-rss-str [files options]
  (let [opts         (select-keys options [:site-title :site-description :base-url])
        channel-opts (clojure.set/rename-keys
                      opts
                      {:site-title :title
                       :site-description :description
                       :base-url :link})
        items        (rss-definitions (filter :name files))
        rss-str      (apply rss-gen/channel-xml channel-opts items)]
    rss-str))

(defn generate-rss [tgt-path files options]
  (let [rss-filepath (str (:out-dir options) "/" (:filename options))
        rss-string   (generate-rss-str files options)]
    (perun/create-file tgt-path rss-filepath rss-string)
    (perun/report-info "rss" "generated RSS feed and saved to %s" rss-filepath)))
