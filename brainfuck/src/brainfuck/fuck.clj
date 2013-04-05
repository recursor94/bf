;;;This is an interpreter for the brainfuck programming language written in
;;;clojure.  I tried to abstract out as much functionality as I could
;;;into compact functions with specific purposes.  Not everything in
;;:this code may be entirely idiomatic.
;;Andrew Spano
(ns brainfuck.fuck
  (:gen-class))

(declare begin-loop end-loop)

(def cells (atom (vec (repeat 10 0)))) ;a lazy vector to represent the brainfuck memory cell.

(def pointer (atom 0))  ;;the mutable pointer which points to the current active memory cell

(def codemap
  "The map of the brainfuck code containing the code pointer index and its structure"
  (atom {:struct [] :index 0})) ;;using vector in the map

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

;;going to risk some mutual recursion now
(defn begin-loop
  "run through a loop until current cell drops to zero"
  [code-position]
  (loop [loop-counter (@cells @pointer)]
    ()))

(defn translate-instruction
  "returns the appropriate brainfuck operation for an instruction"
  [instruction] ;;optional parameter for codeblock
  (condp = instruction
    \+ #'plus
    \- #'minus
    \> #'+pointer
    \< #'-pointer
    \. #'output-character
    \, #'input-character
    \[ #'begin-loop
    \] #'end-loop
    true))


(defn add-instruction
  "adds an additional instruction to the codemap"
  [operation]
  (when (clojure.test/function? operation)
    (let [_ (println "ok")
          codevec '(@codemap :struct) ;doesn't work for some reason
          length (count (@codemap :struct))]
      (println length) ;HOLY MEATBALL WAY TOO COMPLICATED CORRECT IN THE FUTURE
      (reset! codemap (assoc-in @codemap [:struct]
                         (assoc (@codemap :struct) length operation))))))


;;parser function for input should link translate and add-instruction
(defn parse-input
  "takes input and stores it in the code map data structure"
  [input]
  (doseq [instruct input]
    (add-instruction (translate-instruction instruct))))

(defn -main []
  (println "Andrew's brainfuck interpreter"
           "Enter a brainfuck expression for evaluation")
  (parse-input (read-line)))
