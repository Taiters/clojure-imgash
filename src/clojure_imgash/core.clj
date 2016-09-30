(ns clojure-imgash.core
  (use mikera.image.core)
  (use mikera.image.colours)
  (:gen-class))

(defn hsl-to-rgb [h s l]
  (let [c (* (- 1 (Math/abs (- (* 2 l) 1))) s)] 
    (println c)))

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

(defn create-img [blocks block-size colours]
  (let [size (* (+ blocks 2) block-size)
        img (new-image size size)
        pixels (get-img-pixels blocks block-size colours)]
    (println (count pixels))
    (println (count (get-pixels img)))
    (set-pixels img (int-array pixels))
    img))

(defn -main
  [& args]

  (show (time (create-img 7 20 (repeatedly 2 rand-colour)))))
