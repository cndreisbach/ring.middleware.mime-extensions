(ns ring.middleware.mime-extensions-test
  (:use clojure.test
        ring.middleware.mime-extensions))

(deftest correct-mime-types-are-found
  (let [convert (partial convert-filename-to-mime-type (default-mime-map))]
    (are [x y] (= x y)
         "text/javascript" (convert "hello.js")
         "application/json" (convert "data.json")
         "text/javascript" (convert "data.jsonp")
         "text/x-clojure" (convert "mime.clj")
         "text/html" (convert "index.markdown.html"))))

(deftest mime-types-can-be-added
  (let [turkey-mime-map (mime-map)
        convert (partial convert-filename-to-mime-type turkey-mime-map)]
    (is (= "application/octet-stream" (convert "index.turkey")))  
    (.addMimeTypes turkey-mime-map "application/x-turkey turkey")
    (is (= "application/x-turkey" (convert "index.turkey")))))

(deftest middleware-adds-accept-header
  (let [middleware (wrap-convert-extension-to-accept-header identity)
        request {:uri "http://localhost/rufus.xml"
                 :headers {}}
        req-after (middleware request)]
    (is (= "application/xml" (get-in req-after [:headers "accept"])))))

(deftest middleware-leaves-url-with-no-ext-alone
  (let [middleware (wrap-convert-extension-to-accept-header identity)
        request {:uri "http://localhost/rufus"
                 :headers {}}
        req-after (middleware request)]
    (is (= nil (get-in req-after [:headers "accept"])))))

(deftest middleware-leaves-url-with-no-path-alone
  (let [middleware (wrap-convert-extension-to-accept-header identity)
        request {:uri "http://localhost/"
                 :headers {}}
        req-after (middleware request)]
    (is (= nil (get-in req-after [:headers "accept"])))))
