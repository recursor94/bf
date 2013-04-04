;a lazy vector to represent the brainfuck memory cell.


(declare begin-loop end-loop)

(def cells (atom (vec (repeat 3000 0))))

(def pointer (atom 0))

(defn plus
  "(+) Increments current memory cell"
  []
  (reset! cells (assoc @cells @pointer (inc (get @cells @pointer))))) ;uses reset because swap! does not work for some reason

(defn minus
  "(-) Decrements current memory cell"
  []
  (reset! cells (assoc @cells @pointer (dec (get @cells @pointer)))))


(defn +pointer
  "(>) increment the data pointer (move one cell to the right)"
  []
  (swap! pointer inc))


(defn -pointer
  "(<) Decrement the data pointer (move one cell to the left)"
  []
  (swap! pointer dec))

(defn input-character
  "(,) input a single character into the current memory cell"
  []
  (reset! cells (assoc @cells @pointer (char (.read System/in)))))

(defn output-character
  "(.) output the character in the current memory cell"
  []
  (println (char (@cells @pointer))))

(defn begin-loop
  []
  "([) loop until the end character (]) is reached")

(defn exec-instruction
  "performs the appropriate brainfuck operation for an instruction"
  [instruction]
  (condp = instruction
    \+ (plus)
    \- (minus)
    \> (+pointer)
    \< (-pointer)
    \. (output-character)
    \, (input-character)
    \[ (begin-loop)
    \] (end-loop)
    ))
