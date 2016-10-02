(ns clojure-imgash.core
  (use mikera.image.core)
  (use mikera.image.colours)
  (use ring.middleware.params)
  (use org.httpkit.server)
  (:gen-class))

(defn abs [n]
  (max n (- 0 n)))

(defn get-rgb [h' c x]
  (let [to-drop (* 3 (Math/floor h'))]
    (take 3 (drop to-drop [c x 0 x c 0 0 c x 0 x c x 0 c c 0 x]))))

(defn hsl-to-rgb [h s l]
  (let [c (* (- 1 (abs (- (* 2 l) 1))) s)
        h' (/ (mod h 360) 60.0)
        x (* c (- 1 (abs (- (mod h' 2) 1))))
        rgb (get-rgb h' c x)
        m (- l (/ c 2))]
    (map #(+ % m) rgb)))

(defn mirror-row [row blocks]
  (if (even? blocks)
    (concat row (reverse row))
    (concat row (rest (reverse row)))))

(defn get-random [items r]
  (nth items (.nextInt r (count items))))

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
    (map #(apply rgb %) [(hsl-to-rgb bg 0.6 0.6)
                         (hsl-to-rgb fg 0.75 0.6)])))

(defn create-img [blocks block-size input]
  (let [r (java.util.Random. (hash input))
        colours (generate-colours r)
        size (* (+ blocks 2) block-size)
        img (new-image size size)
        pixels (get-img-pixels blocks block-size colours r)]
    (set-pixels img (int-array pixels))
    img))

(defn write-image [n]
  (let [img (create-img 7 10 n)]
    (save img (format "/tmp/images/%s.png" n))))

(defn generate-image [i]
  (write-image i)
  {:status  200
   :headers {"Content-Type" "image/png"
             "X-Accel-Redirect" (format "/internal_images/%s.png" i)}})

(defn app [req]
  (let [i (get-in req [:query-params "i"])]
    (if (string? i)
      (generate-image i)
      {:status 400})))

(defn -main
  [& args]
;  (let [stdin (line-seq (java.io.BufferedReader. *in*))]
;    (dorun
;      (pmap write-image stdin))
;    (shutdown-agents)))
  (run-server (wrap-params app) {:port 9000}))


  ;(println (map #(* % 255) (hsl-to-rgb 0 0 0))))
  ;(println (get-pallete 2)))

