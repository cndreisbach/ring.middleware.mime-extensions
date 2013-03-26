(ns ring.middleware.mime-extensions
  (:require [clojure.java.io :as io]
            [clojurewerkz.urly.core :as url :refer [url-like]])
  (:import (java.net URL)
           (javax.activation MimetypesFileTypeMap)))

(defn mime-map
  ([] (MimetypesFileTypeMap.))
  ([filename] (MimetypesFileTypeMap. filename)))

(defn default-mime-map []
  (mime-map (.getPath (io/resource "mime.types"))))

(defn convert-filename-to-mime-type
  [mime-map filename]
  (.getContentType mime-map filename))

(defn wrap-convert-extension-to-accept-header
  ([handler] (wrap-convert-extension-to-accept-header handler (default-mime-map)))
  ([handler mime-map]
     (fn [{:keys [uri] :as request}]
       (let [url (url-like uri)
             filename (url/path-of url)
             ext-index (.lastIndexOf filename ".")]
         (if (> ext-index -1)
           (let [mime-type (convert-filename-to-mime-type mime-map filename)
                 new-uri (str (.mutatePath url (.substring filename 0 ext-index)))
                 extension (.substring uri (inc ext-index))]
             (-> request
                 (assoc-in [:headers "accept"] mime-type)
                 (assoc :original-uri uri)
                 (assoc :uri new-uri)
                 (assoc :extension extension)
                 handler))
           (handler request))))))
