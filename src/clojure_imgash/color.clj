(ns clojure-imgash.color)

(defn- abs [n]
  (max n (- 0 n)))

(defn- get-rgb-values
  "Find the (R, G, B) face with the same hue (h') and chroma (c)
   as our color"
  [h' c x]
  (let [to-drop (* 3 (Math/floor h'))]
    (take 3 (drop to-drop [c x 0 x c 0 0 c x 0 x c x 0 c c 0 x]))))

(defn int-rgb
  "Convert RGB values to an int"
  [r g b]
  (bit-or (bit-shift-left (int r) 16)
          (bit-shift-left (int g) 8)
          (int b)))

(defn hsl-to-rgb 
  "Convert a HSL color to RGB"
  [h s l]
  (let [c (* (- 1 (abs (- (* 2 l) 1))) s)
        h' (/ (mod h 360) 60.0)
        x (* c (- 1 (abs (- (mod h' 2) 1))))
        rgb-values (get-rgb-values h' c x)
        m (- l (/ c 2))]
    (map #(* 255 (+ % m)) rgb-values)))

(defn hsl-to-int
  "Convert a HSL color to an int RGB"
  [h s l]
  (apply int-rgb (hsl-to-rgb h s l)))
