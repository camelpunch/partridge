(ns ^:figwheel-always partridge.core
    (:require
     [reagent.core :as reagent :refer [atom]]
     [ajax.core :refer [GET]]))

(enable-console-print!)

(def waterways ["Trent & Mersey Canal"
                "Shropshire Union Canal"])

(def selected-waterway (atom (first waterways)))

(def current-polylines (clojure.core/atom))

(defn create-polyline [coords]
  (js/google.maps.Polyline. (clj->js {:path coords
                                      :geodesic true
                                      :strokeColor "#000000"
                                      :strokeOpacity 1.0,
                                      :strokeWeight 4})))

(defn swap-polylines [gmap]
  (fn [old new]
    (doseq [line old] (.setMap line nil))
    (doseq [line new] (.setMap line gmap))
    new))

(defn get-waterway [gmap waterway]
  (GET "http://localhost:3000/centrelines"
       {:params {"q" waterway}
        :handler (fn [sections]
                   (.setCenter gmap (clj->js (ffirst sections)))
                   (println (count (flatten sections)))
                   (let [new-polylines (map create-polyline sections)]
                     (swap! current-polylines
                            (swap-polylines gmap)
                            new-polylines)))}))

(defn home []
  [:div
   [:div#controls
    [:label {:for "waterway"} "Waterway: "]
    [:select#waterway
     {:value @selected-waterway
      :on-change #(reset! selected-waterway (-> % .-target .-value))}
     (map (fn [name] [:option name]) waterways)]
    [:h1 @selected-waterway]]
   [:div#map-canvas]])

(defn home-did-mount []
  (let [map-canvas (.getElementById js/document "map-canvas")
        map-options (clj->js {:center {:lat 53.0672367 :lng -2.52393}
                              :zoom 9})
        gmap (js/google.maps.Map. map-canvas map-options)]
    (get-waterway gmap @selected-waterway)
    (add-watch selected-waterway ::waterway-watcher
               (fn [key ref old new]
                 (get-waterway gmap new)))
    ))

(defn home-component []
  (reagent/create-class {:render home
                         :component-did-mount home-did-mount}))

(reagent/render-component [home-component]
                          (. js/document (getElementById "app")))
