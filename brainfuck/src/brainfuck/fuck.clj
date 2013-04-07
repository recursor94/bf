;;;This is an interpreter for the brainfuck programming language written in
;;;clojure.  I tried to abstract out as much functionality as I could
;;;into compact functions with specific purposes.  Not everything in
;;:this code may be entirely idiomatic.
;;Andrew Spano
(ns brainfuck.fuck
  (:gen-class))

(use 'clojure.tools.trace)

(declare end-loop exec-instruction)

(def ^{:dynamic true} cells  (vec (repeat 300 0))) ;a lazy vector to represent the brainfuck memory cell.

(def ^{:dynamic true} pointer 0) ;;the pointer which points to the current active memory cell

(defn plus
  "(+) Increments current memory cell"
  {:operation-type :cell}
  [cell]
  (inc (cells pointer)))

(defn minus
  "(-) Decrements current memory cell"
  {:operation-type :cell}
  [cell]
  (dec (cells pointer)))


(defn +pointer
  "(>) increment the data pointer (move one cell to the right)"
  {:operation-type :pointer}
  [pointer]
  (inc pointer))


(defn -pointer
  "(<) Decrement the data pointer (move one cell to the left)"
  {:operation-type :pointer}
  []
  (pointer))

(defn input-character
  "(,) input a single character into the current memory cell"
  {:operation-type :io}
  [cell pointer]
  (assoc (cells pointer) (int (.read System/in))))

(defn output-character
  "(.) output the character in the current memory cell"
  {:operation-type :io}
  [cell pointer]
  (print (char (cells pointer)))
  (flush))

(defn begin-loop
  "[ run through a loop until current cell drops to zero"
  {:operation-type :loop}
  [index]
  )

(defn end-loop
  "] set code pointer to start of loop"
  {:operation-type :loop}
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
  [instructions] ;;maybe I don't even need the global vars at all?
  (loop [instructs (vec instructions)
         code-pos 0
         op-type (:operation-type (meta (instructs code-pos)))
         instruct (instructs code-pos)]
         (binding [cells cells pointer pointer]
           (println op-type)
           (condp = op-type
             :cell (set! cells (assoc  cells (instruct (cells pointer)) pointer))
             :pointer (set! pointer (instruct pointer))
             :io (set! cells (instruct (cells pointer) pointer))
             :loop (instruct code-pos)
             true)
           (println cells))))


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
