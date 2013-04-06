
;;;This is an interpreter for the brainfuck programming language written in
;;;clojure.  I tried to abstract out as much functionality as I could
;;;into compact functions with specific purposes.  Not everything in
;;:this code may be entirely idiomatic.
;;Andrew Spano
(ns brainfuck.fuck
  (:gen-class))

(declare end-loop exec-instruction tramp)

(def cells (atom (vec (repeat 9001 0)))) ;a lazy vector to represent the brainfuck memory cell.

(def pointer (atom 0))  ;;the mutable pointer which points to the current active memory cell

(def codemap
  "The map of the brainfuck code containing the code pointer index and its structure  :struct holds a vector containing the functions to be executed.
index holds the current position of the interpreter in its execution."
  (atom {:struct [] :index 0})) ;;using vector in the map

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

(defn find-end
  "helper function for begin-loop. Find the end of the loop"
  [code-position]

  (loop [pos code-position]
    (let [instruct ((@codemap :struct) pos)]
      (if (= instruct #'end-loop)
        pos
        ;else
        (recur (inc pos))))))
;;going to risk some mutual recursion now
(defn begin-loop
  "run through a loop until current cell drops to zero"
  []
  (loop [loop-counter (@cells @pointer)
         end-loop (find-end (@codemap :index))
         pos (@codemap :index)]
    (exec-instruction end-loop)
    (when-not (= loop-counter 0)
      (recur loop-counter end-loop pos))))

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
  "takes input and stores it in the code map data structure"
  [input]
  (doseq [instruct input]
    (add-instruction (translate-instruction instruct))))


(defn exec-instruction
  "executes each function in the codemap vector in sequential order"


    ([end-index]
       (loop [index (:index @codemap)]
             (let [codevec (@codemap :struct)
                   instruct (get codevec index)]
               (instruct))
             (when-not (= index (+ 1 end-index))
               (inc-code-pos)
               (recur (inc index)))))
    ([]
       (doseq [instruct (@codemap :struct)]
         (instruct) ;;higher order functions ftw
         (inc-code-pos))))

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
