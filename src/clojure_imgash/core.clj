(ns clojure-imgash.core
  (use clojure-imgash.color)
  (use clojure-imgash.image)
  (use ring.middleware.params)
  (use org.httpkit.server)
  (:gen-class))

(defn mirror-row [row blocks]
  (if (even? blocks)
    (concat row (reverse row))
    (concat row (rest (reverse row)))))

(defn get-random [items r]
  (let [i (if (< 0.4 (.nextFloat r)) 1 0)]
    (nth items i)))

(defn get-row-pixels [blocks block-size colours r]
  (let [bg (first colours)
        random-colour (partial get-random colours r)
        half-blocks (int (Math/ceil (/ blocks 2)))
        half-row (cons bg (repeatedly half-blocks random-colour))
        row (mirror-row half-row blocks)]
    (repeat block-size (map #(repeat block-size %) row))))

(defn get-img-pixels [blocks block-size colours r]
  (let [row-func (partial get-row-pixels blocks block-size colours r)
        rows (repeatedly blocks row-func)
        border-pixel-count (* (* block-size block-size) (+ blocks 2))
        border (repeat border-pixel-count (first colours))]
    (flatten (concat border rows border))))

(defn generate-colours [r]
  (let [bg (.nextInt r 360)
        fg (+ bg (+ 120 (.nextInt r 120)))]
    [(hsl-to-int bg 0.6 0.6)
     (hsl-to-int fg 0.75 0.6)]))

(defn create-img [blocks block-size input]
  (let [r (java.util.Random. (hash input))
        colours (generate-colours r)
        size (* (+ blocks 2) block-size)
        pixels (get-img-pixels blocks block-size colours r)]
    (image size size pixels)))

(defn write-image [n]
  (let [img (create-img 8 12 n)]
    (save img (format "/tmp/images/%s.png" (hash n)))))

(defn return-image-for-input [i]
  (write-image i)
  {:status  200
   :headers {"Content-Type" "image/png"
             "X-Accel-Redirect" (format "/images/cached/%s.png" (hash i))}})

(defn app [req]
  (let [i (get-in req [:query-params "i"])]
    (if (string? i)
      (return-image-for-input i)
      {:status 400})))

(defn -main
  [& args]
  (run-server (wrap-params app) {:port 9000}))

