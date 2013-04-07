;;;This is an interpreter for the brainfuck programming language written in
;;;clojure.  I tried to abstract out as much functionality as I could
;;;into compact functions with specific purposes.  Not everything in
;;:this code may be entirely idiomatic.
;;Andrew Spano
(ns brainfuck.fuck
  (:gen-class))

(use 'clojure.tools.trace)

(declare end-loop exec-instruction)

(def cells ^{:dynamic true} (vec (repeat 300 0))) ;a lazy vector to represent the brainfuck memory cell.

(def ^{:dynamic true} pointer 0) ;;the pointer which points to the current active memory cell

(defn plus
  "(+) Increments current memory cell"
  {:operation-type :cell}
  []
  (inc (cells pointer)))

(defn minus
  "(-) Decrements current memory cell"
  {:operation-type :cell}
  []
  (dec (cells pointer)))


(defn +pointer
  "(>) increment the data pointer (move one cell to the right)"
  {:operation-type :pointer}
  []
  (inc pointer))


(defn -pointer
  "(<) Decrement the data pointer (move one cell to the left)"
  {:operation-type :pointer}
  []
  (dec pointer))

(defn input-character
  "(,) input a single character into the current memory cell"
  []
  (assoc (cells pointer) (int (.read System/in))))

(defn output-character
  "(.) output the character in the current memory cell"
  []
  (print (char (cells pointer)))
  (flush))

(defn begin-loop
  "[ run through a loop until current cell drops to zero"
  []
  )

(defn end-loop
  "] set code pointer to start of loop"
  [start-index]
  )

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

(defn exec-instruction
  "executes each brainfuck function in sequential order"
  [instruct]
  (instruct))

(defn exec-operations
  "executes the operations and keeps track of data pointer"
  [instructs] ;;maybe I don't even need the global vars at all?x
  (loop [instructs (vec instructs)
         code-pos 0
         pointer pointer]
    (binding [cells cells]
      (println code-pos cells pointer)
      (instructs code-pos)
      (if (< code-pos (dec (count instructs)))
        (recur instructs (inc code-pos) cells pointer)))))


;;parser function for input should link translate and add-instruction
(defn parse-input
  "takes input and sends each individual instruction to exec-instruction"
  [input]
  (exec-operations
   (map translate-instruction input)))


(defn -main []
  (println "Andrew's brainfuck interpreter Version 0.01"
           "Enter a brainfuck expression for evaluation:\n")
  (parse-input (read-line)))
