(ns clojure-imgash.image
  (:import java.awt.image.BufferedImage
           javax.imageio.ImageIO)
  (use clojure.java.io))

(defn- set-pixel-data
  "Set a BufferedImage's pixel data from a pixel seq"
  [^BufferedImage img pixels]
  (let [pixels-int-arr (into-array Integer/TYPE pixels)]
    (.setRGB img 0 0 (.getWidth img) (.getHeight img) pixels-int-arr 0 1)))

(defn image
  "Get a new TYPE_INT_RGB BufferedImage populated from the supplied pixel sequence"
  (^BufferedImage [w h pixels]
    (let [img (BufferedImage. w h BufferedImage/TYPE_INT_RGB)
          pixel-arr (into-array Integer/TYPE pixels)]
      (.setRGB img 0 0 (.getWidth img) (.getHeight img) pixel-arr 0 (.getWidth img))
      img)))

(defn save
  "Save an image to the specified path"
  [^BufferedImage img ^String path]
  (let [f (file path)]
    (ImageIO/write img "png" f)))
