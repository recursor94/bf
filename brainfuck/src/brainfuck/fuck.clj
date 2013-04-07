;;;This is an interpreter for the brainfuck programming language written in
;;;clojure.  I tried to abstract out as much functionality as I could
;;;into compact functions with specific purposes.  Not everything in
;;:this code may be entirely idiomatic.
;;Andrew Spano
(ns brainfuck.fuck
  (:gen-class))

(use 'clojure.tools.trace)

(declare end-loop exec-instruction)

;(def ^{:dynamic true} cells  (vec (repeat 300 0))) ;a lazy vector to represent the brainfuck memory cell.

;;(def ^{:dynamic true} pointer 0) ;;the pointer which points to the current active memory cell

(defn plus
  "(+) Increments current memory cell"
  {:operation-type :cell}
  [cells pointer]
  (inc (cells pointer)))

(defn minus
  "(-) Decrements current memory cell"
  {:operation-type :cell}
  [cells pointer]
  (dec (cells pointer)))


(defn +pointer
  "(>) increment the data pointer (move one cell to the right)"
  {:operation-type :pointer}
  [pointer]
  (inc pointer))


(defn -pointer
  "(<) Decrement the data pointer (move one cell to the left)"
  {:operation-type :pointer}
  [pointer]
  (cells pointer))

(defn input-character
  "(,) input a single character into the current memory cell"
  {:operation-type :io}
  [cells pointer]
  (assoc (cells pointer) (int (.read System/in))))

(defn output-character
  "(.) output the character in the current memory cell"
  {:operation-type :io}
  [cells pointer]
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
  (let [operation])
  (instruct))

(defn exec-operations
  "executes the operations and keeps track of data pointer"
  [instructions] ;;maybe I don't even need the global vars at all?
  (let [cells ((atom vec (repeat 300 0)))
            code-pos 0
        cell ((atom cells code-pos))
        operation (var instructions)]))


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
