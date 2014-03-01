(ns purnam-crafty-game.core
  (:require [goog.object :as o])
  (:use-macros [purnam.core :only [obj arr ! def.n def*n def* do*n]]))

 (def* G
  {:grid {:width 24
           :height 16
           :tile {:width 16 :height 16}}
   :width
   (fn [] (* this.grid.width this.grid.tile.width))

   :height
   (fn [] (* this.grid.height this.grid.tile.height))

   :start
   (fn []
     (let [G self]
       (js/Crafty.init (G.width) (G.height))
       (js/Crafty.background "rgb(20, 75, 40)")
       (js/Crafty.scene "Game")))})

(do*n
 (js/Crafty.c
  "Grid"
  {:init
   (fn []
     (this.attr {:w G.grid.tile.width
                 :h G.grid.tile.height}))
   :at
   (fn [x y]
     (cond (and (= x js/undefined)
                (= y js/undefined))
           {:x (/ this.x G.grid.tile.width)
            :y (/ this.y G.grid.tile.height)}
           :else
           (do
             (this.attr {:x (* x G.grid.tile.width)
                         :y (* y G.grid.tile.height)})
             this)))})

 (js/Crafty.c
  "Actor"
  {:init
   (fn [] (-> this
             (.requires "2D, Color, Canvas, Grid")
             (.color "rgb(20, 75, 40)")))})

 (js/Crafty.c
  "PlayerCharacter"
  {:init
   (fn []
     (-> this
         (.requires "Actor, Fourway, Collision, SpriteAnimation, spr_player")
         (.fourway 2)
         (.stopOnSolids)
         (.onHit "Village" this.visitVillage)
         (.animate "PlayerMovingUp"    0 0 2)
         (.animate "PlayerMovingRight" 0 1 2)
         (.animate "PlayerMovingDown"  0 2 2)
         (.animate "PlayerMovingLeft"  0 3 2)
         (.bind "NewDirection"
                (fn [data]
                  (let [speed 8]
                    (cond (> data.x 0) (this.animate "PlayerMovingRight" speed -1)
                          (< data.x 0) (this.animate "PlayerMovingLeft" speed -1)
                          (> data.y 0) (this.animate "PlayerMovingDown" speed -1)
                          (< data.y 0) (this.animate "PlayerMovingUp" speed -1)
                          :else (this.stop)))))))

   :stopOnSolids
   (fn []
     (-> this
         (.onHit "Solid" this.stopMovement))
     this)

   :stopMovement
   (fn []
     ;;(js/console.log )
     (! this._speed 0)
     (when this._movement
       (! this.x (- this.x this._movement.x))
       (! this.y (- this.y this._movement.y))))

   :visitVillage
   (fn [data]
     (let [village data.0.obj]
       (village.collect)))
   })

 (js/Crafty.c
  "Village"
  {:init
   (fn []
     (-> this
         (.requires "Actor, Color, spr_village")
         ;;(.color "rgb(170, 125, 40)")
         ))
   :collect
   (fn []
     (this.destroy)
     (js/Crafty.audio.play "knock")
     (js/Crafty.trigger "VillageVisited" this))})

 (js/Crafty.c
  "Tree"
  {:init
   (fn []
     (-> this
         (.requires "Actor, Color, Solid, spr_tree")
         ;;(.color "rgb(20, 125, 40)")
         ))})

 (js/Crafty.c
  "Bush"
  {:init
   (fn []
     (-> this
         (.requires "Actor, Color, Solid, spr_bush")
         ;;(.color "rgb(20, 185, 40)")
         ))})

 (js/Crafty.c
  "Rock"
  {:init
   (fn []
     (-> this
         (.requires "Actor, Color, Solid, spr_rock")
         ;;(.color "rgb(20, 90, 40)")
         ))})

 (js/Crafty.scene
  "Game"
  (fn []
     (js/Crafty.init (G.width) (G.height))
     (js/Crafty.background "rgb(20, 75, 40)")

    (let [occupied (obj)
          v   {:count 0}
          max 10]
      (! this.player
         (-> (js/Crafty.e "PlayerCharacter")
             (.at 5 5)))
      (! occupied.5.5 true)
      (doseq [x (range G.grid.width)
              y (range G.grid.height)]
         (let [edge? (or (zero? x)
                         (zero? y)
                         (= x (dec G.grid.width))
                         (= y (dec G.grid.height)))]
           (cond edge?
                 (do (-> js/Crafty
                         (.e "Tree") (.at x y))
                     (! occupied.|x|.|y| true))

                 (< (js/Math.random) 0.06)
                 (let [label (if (< (js/Math.random) 0.7)
                               "Bush" "Rock")]
                   (-> (js/Crafty.e label)
                       (.at x y))
                   (! occupied.|x|.|y| true)))))
      (doseq [x (range G.grid.width)
              y (range G.grid.height)]
        (cond (and (< (js/Math.random) 0.04)
                   (not= occupied.|x|.|y| true)
                   (< v.count max))
              (do
                  (! v.count (inc v.count))
                  (-> js/Crafty
                      (.e "Village") (.at x y))
                  (! occupied.|x|.|y| true)))))
    (js/Crafty.audio.play "ring")
    (! this.show_victory
       (.bind this
        "VillageVisted"
        (fn []
          (js/console.log (.-length (js/Crafty "Village")))
          (if (.-length (js/Crafty "Village"))
            (js/Crafty.scene "Victory"))))))
  (fn []
    (js/Crafty.unbind "VillageVisited" this.show_victory)))

 (comment "Victory and Loading Scenes not Implemented"
 (js/Crafty.scene
  "Victory"
  (fn []
    (-> (js/Crafty.e "2D, DOM, Text")
        (.text "All Villages Visited")
        (.attr {:x 0 :y (- (/ (G.height) 2) 24) :w (G.width)}))
    (js/Crafty.audio.play "applause")
    #_(js/Crafty.bind
     "Keydown"
     (fn [] (js/Crafty.scene "Game")))))

 (js/Crafty.scene
  "Loading"
  (fn []
    (-> (js/Crafty.e "2D, DOM, Text")
        (.text "Loading: Please Wait")
        (.attr {:x 0 :y (- (/ (G.height) 2) 24) :w (G.width)})))))

 (js/Crafty.load
  ["crafty/16x16_forest_2.gif"
   "crafty/hunter.png"
   "crafty/door_knock_3x.mp3"
   "crafty/door_knock_3x.ogg"
   "crafty/door_knock_3x.aac"
   "crafty/board_room_applause.mp3"
   "crafty/board_room_applause.ogg"
   "crafty/board_room_applause.aac"
   "crafty/candy_dish_lid.mp3"
   "crafty/candy_dish_lid.ogg"
   "crafty/candy_dish_lid.aac"]
  (fn []
    (js/Crafty.sprite
     16 "crafty/16x16_forest_2.gif"
     {:spr_tree [0 0]
      :spr_bush [1 0]
      :spr_village [0 1]
      :spr_rock [1 1]})
    (js/Crafty.sprite
     16 "crafty/hunter.png"
     {:spr_player [0 2]} 0 2)

    (js/Crafty.audio.add
     {:knock     ["crafty/door_knock_3x.mp3"
                  "crafty/door_knock_3x.ogg"
                  "crafty/door_knock_3x.aac"]
      :applause  ["crafty/board_room_applause.mp3"
                  "crafty/board_room_applause.ogg"
                  "crafty/board_room_applause.aac"]
      :ring      ["crafty/candy_dish_lid.mp3"
                  "crafty/candy_dish_lid.ogg"
                  "crafty/candy_dish_lid.aac"]})

    )))


(def START (fn [] (js/Crafty.scene "Game")))
