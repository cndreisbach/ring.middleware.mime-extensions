# ring.middleware.mime-extensions

Ring middleware to add an Accept header to every web request based off
of the URL extension.

## Usage

In your `project.clj`, add the following dependency:

```clj
[ring.middleware.mime-extensions "0.1.0"]
```

Then add the middleware to your stack. You can leave out the mime-type
map (an instance of
[`javax.activation.MimetypesFileTypeMap`][MimetypesFileTypeMap]) and a
default one will be used.

With a Compojure app, your setup can be as simple as:

```clj
(ns your-app
  (:require [[compojure.handler :as handler]
             [ring.middleware.mime-extensions 
              :refer [wrap-convert-extension-to-accept-header]]]))

(def app
  (-> (handler/site app-routes)
      wrap-convert-extension-to-accept-header))
```

This will not only set the "accept" header in your request. It will
also change the `:uri` to be the URI without the extension and set the
new key-value pairs `:extension` and `:original-uri` in your request.

## Customizing

To use a different [`MimetypesFileTypeMap`][MimetypesFileTypeMap], pass it as an argument
to `wrap-convert-extension-to-accept-header`. There is a convenience
method, `mime-map`, to create a new one, which can take a string
representing the filename of your `mime.types` file or an
`InputStream`.

Your custom `mime.types` file, which will detail all the mappings from
extensions to MIME types, must be in this format (from the Java
documentation):

```
# comments begin with a '#'
# the format is <mime type> <space separated file extensions>
# for example:
text/plain txt text TXT
# this would map file.txt, file.text, and file.TXT to
# the mime type "text/plain"
```

You can also add MIME type mappings to a `MimetypesFileTypeMap` by
calling `.addMimeTypes` with a string formatted like a `mime.types`
file. That is some mutable nonsense and if you do it, it's in your
hands, pal.


## Terms

This is free and unencumbered software released into the public
domain. It was originally written by Clinton Dreisbach, heavily
inspired by similar functionality previously in
[Clojure Liberator][Liberator].

[MimetypesFileTypeMap]: http://docs.oracle.com/javase/7/docs/api/javax/activation/MimetypesFileTypeMap.html
[Liberator]: http://clojure-liberator.github.com/
