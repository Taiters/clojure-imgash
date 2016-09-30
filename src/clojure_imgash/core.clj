(ns clojure-imgash.core
  (use mikera.image.core)
  (use mikera.image.colours)
  (:gen-class))

(defn abs [n]
  (max n (- 0 n)))

(defn between [n low high]
  (and (>= low n) (< n high)))

(defn get-rgb [h' c x]
  (let [to-drop (* 3 (Math/floor h'))]
    (take 3 (drop to-drop [c x 0 x c 0 0 c x 0 x c x 0 c c 0 x]))))

(defn hsl-to-rgb [h s l]
  (let [c (* (- 1 (abs (- (* 2 l) 1))) s)
        h' (/ h 60.0)
        x (* c (- 1 (abs (- (mod h' 2) 1))))
        rgb (get-rgb h' c x)
        m (- l (/ c 2))]
    (map #(+ % m) rgb)))

(defn mirror-row [row blocks]
  (if (even? blocks)
    (concat row (reverse row))
    (concat row (rest (reverse row)))))

(defn get-row-pixels [blocks block-size colours]
  (let [bg (first colours)
        random-colour (partial rand-nth colours)
        half-blocks (int (Math/ceil (/ blocks 2)))
        half-row (cons bg (repeatedly half-blocks random-colour))
        row (mirror-row half-row blocks)]
    (repeat block-size (map #(repeat block-size %) row))))

(defn get-img-pixels [blocks block-size colours]
  (let [row-func (partial get-row-pixels blocks block-size colours)
        rows (repeatedly blocks row-func)
        border-pixel-count (* (* block-size block-size) (+ blocks 2))
        border (repeat border-pixel-count (first colours))]
    (flatten (concat border rows border))))

(defn hue-to-colour [hue]
  (apply rgb (hsl-to-rgb (mod hue 360) 0.6 0.5)))

(defn get-pallete
  ([n] (get-pallete n [(rand 360)]))
  ([n hues]
    (if (> n (count hues))
      (recur n (cons (+ (first hues) (+ 100 (rand 160))) hues))
      (map hue-to-colour hues))))

(defn create-img [blocks block-size]
  (let [colours (get-pallete 2)
        size (* (+ blocks 2) block-size)
        img (new-image size size)
        pixels (get-img-pixels blocks block-size colours)]
    (set-pixels img (int-array pixels))
    img))

(defn -main
  [& args]
  ;(println (map #(* % 255) (hsl-to-rgb 0 0 0))))
  ;(println (get-pallete 2)))
  (dotimes [n 1000]
    (show (create-img 7 30))))
