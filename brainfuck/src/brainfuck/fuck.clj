;;;This is an interpreter for the brainfuck programming language written in
;;;clojure.  I tried to abstract out as much functionality as I could
;;;into compact functions with specific purposes.  Not everything in
;;:this code may be entirely idiomatic.
;;Andrew Spano
(ns brainfuck.fuck
  (:gen-class))

(use 'clojure.tools.trace)

(declare end-loop exec-instruction)

(def cells (vector (repeat 0))) ;a lazy vector to represent the brainfuck memory cell.

(def pointer 0)  ;;the pointer which points to the current active memory cell

(defn reset-instructions
  "resets the current brainfuck map"
  []
  (swap! codemap assoc :struct [] ))

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
  (reset! cells (assoc @cells @pointer (int (.read System/in)))))

(defn output-character
  "(.) output the character in the current memory cell"
  []
  (println (char (@cells @pointer)))
  (flush))

(defn set-code-pos
  "sets the postion of the code pointer"
  [pos]
  (swap! codemap assoc-in [:index] pos))

(defn inc-code-pos
  "increments the poistion of the code pointer"
  []
  (swap! codemap update-in [:index] inc))

(defn dec-code-pos
  "decrements the position of the code pointer"
  []
  (swap! codemap update-in [:index] dec))



;;going to risk some mutual recursion now
(defn begin-loop
  "run through a loop until current cell drops to zero"
  []
  (loop [loop-counter (@cells @pointer)
         codevec (@codemap :struct)
         start-index (@codemap :index)
         _ (inc-code-pos)
         instruct (codevec start-index)]
    (println "loopcounter: " loop-counter
             "instruction:" instruct)
    (if (= instruct #'end-loop)
      (do   
        (println true)
        (instruct start-index))
      (do (println false)
        (instruct)))
    (when-not (> loop-counter 0)
      (inc-code-pos)
      (println "incremented" (codevec (@codemap :index)))
      (recur loop-counter codevec start-index nil (codevec (@codemap :index))))))

(defn end-loop
  "] set code pointer to start of loop"
  [start-index]
  (set-code-pos start-index))

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
  (when (var? operation)
    (let [length (count (@codemap :struct))]
      (reset! codemap (assoc-in @codemap [:struct]
                        (assoc (@codemap :struct) length operation))))))


;;parser function for input should link translate and add-instruction
(defn parse-input
  "takes input and sends each individual instruction to exec-instruction"
  [input]
  (exec-instruction
   (map translate-instruction input)))


(defn exec-instruction
  "executes each brainfuck function in sequential order"
  [instructions]
  (doseq [instruct instructions]
         (intruct)))

(defn -main []
  (println "Andrew's brainfuck interpreter Version 0.01"
           "Enter a brainfuck expression for evaluation:\n")

  ;;horribly redundant. Fix later
  (print ">>> ")
  (flush)
  (loop [input (read-line)]
    (flush)
    (print ">>> ")
    (if input
      (do
        (parse-input input)
        (exec-instruction)
        (reset-instructions)
        (flush)
        (recur (read-line))))))
